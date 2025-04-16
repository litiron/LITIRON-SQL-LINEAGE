package com.litiron.code.lineage.sql.service.column;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.litiron.code.lineage.sql.common.constants.TableConstants;
import com.litiron.code.lineage.sql.dao.column.SqlLineageColumnRepository;
import com.litiron.code.lineage.sql.dto.lineage.ParseRelationParamsDto;
import com.litiron.code.lineage.sql.dto.lineage.column.ParsedColumnMetaDto;
import com.litiron.code.lineage.sql.dto.lineage.column.SqlLineageColumnDependencyDto;
import com.litiron.code.lineage.sql.dto.lineage.table.SqlLineageTableColDepDto;
import com.litiron.code.lineage.sql.entity.column.SqlLineageColumnEdgeEntity;
import com.litiron.code.lineage.sql.entity.column.SqlLineageColumnNodeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 字段级别解析服务
 * @author: Litiron
 * @create: 2025-03-30 19:08
 **/
@Service
@Slf4j
public class SqlLineageColumnServiceImpl implements SqlLineageColumnService {


    private final SqlLineageColumnRepository sqlLineageColumnRepository;


    @Autowired
    public SqlLineageColumnServiceImpl(SqlLineageColumnRepository sqlLineageColumnRepository) {
        this.sqlLineageColumnRepository = sqlLineageColumnRepository;
    }


    @Override
    public void parseColumnDependency(ParseRelationParamsDto parseRelationParamsDto) {
        new ColumnInnerParser(parseRelationParamsDto.getConnectionIp(), parseRelationParamsDto.getPort(), parseRelationParamsDto.getDatabaseType())
                .parseColumnDependency(parseRelationParamsDto.getSql());
    }

    @Override
    public List<ParsedColumnMetaDto> parseColumnRelation(ParseRelationParamsDto parseRelationParamsDto) {
        return new ColumnInnerParser(parseRelationParamsDto.getConnectionIp(), parseRelationParamsDto.getPort(), parseRelationParamsDto.getDatabaseType())
                .parseColumnMeta(parseRelationParamsDto.getSql());
    }

    @Getter
    @Setter
    class ColumnInnerParser {
        // 别名管理待优化 todo 嵌套子查询导致别名覆盖
        private Map<String, SqlLineageTableColDepDto> aliasTableMap = new HashMap<>(16);

        private String connectionIp;

        private Integer port;

        private String databaseName;

        private String databaseType;

        public ColumnInnerParser(String connectionIp, Integer port, String databaseType) {
            this.connectionIp = connectionIp;
            this.port = port;
            this.databaseType = databaseType;
        }

        public List<ParsedColumnMetaDto> parseColumnMeta(String sql) {
            List<ParsedColumnMetaDto> columns = new ArrayList<>();
            // 解析SQL语句
            List<SQLStatement> sqlStatements = parsePgStatements(sql);
            // 解析SQL语句
            for (SQLStatement stmt : sqlStatements) {
                if (stmt instanceof SQLSelectStatement select) {
                    handleSelectStatement(select.getSelect().getQuery(), columns);
                } else if (stmt instanceof SQLInsertStatement) {
                    handleInsertStatement((SQLInsertStatement) stmt, columns);
                } else if (stmt instanceof SQLUpdateStatement) {
                    handleUpdateStatement((SQLUpdateStatement) stmt, columns);
                } else if (stmt instanceof SQLDeleteStatement) {
                    handleDeleteStatement((SQLDeleteStatement) stmt, columns);
                }
            }
            // 去除一些重复信息，有些可能因为解析顺序没有拿到alias对应的表信息，如果要完善应当还需要再增一个当前处理不到的集合，在这个地方进行最后的处理
            columns = columns.stream().filter(col -> StrUtil.isNotBlank(col.getTableName())).collect(Collectors.toList());
            // 还需要进行去重，根据数据库+表+字段名（简单一点这里先不做处理）
            // todo 通过当前类的connectionIp和port去查看数据库 填充数据库字段表名批注信息
            return columns;
        }


        private void handleSelectStatement(SQLSelectQuery query, List<ParsedColumnMetaDto> columns) {
            if (query instanceof SQLSelectQueryBlock queryBlock) {
                // 处理SELECT项
                for (SQLSelectItem item : queryBlock.getSelectList()) {
                    if (item.getExpr() instanceof SQLPropertyExpr expr) {
                        if (expr.getOwner() instanceof SQLIdentifierExpr) {
                            addColumn(columns, this.getDatabaseName(), expr.getName(), item.getAlias());
                        }
                    }
                }
                // 处理FROM子句
                processTableSource(queryBlock.getFrom(), columns);
            }
        }

        private void handleInsertStatement(SQLInsertStatement stmt, List<ParsedColumnMetaDto> columns) {
            // 处理目标表
            String database = stmt.getTableSource().getSchema();
            String table = stmt.getTableName().getSimpleName();

            // 处理插入的字段
            for (SQLExpr column : stmt.getColumns()) {
                String columnName = column.toString().replace("`", "");
                addColumn(columns, database, table, columnName);
            }

            // 如果有SELECT子句，处理来源字段
            if (stmt.getQuery() != null) {
                handleSelectStatement(stmt.getQuery().getQuery(), columns);
            }
        }

        private void handleUpdateStatement(SQLUpdateStatement stmt, List<ParsedColumnMetaDto> columns) {
            String database = stmt.getTableSource().toString();
            String table = stmt.getTableName().getSimpleName();

            // 处理更新的字段
            for (SQLUpdateSetItem item : stmt.getItems()) {
                if (item.getColumn() instanceof SQLIdentifierExpr) {
                    String columnName = ((SQLIdentifierExpr) item.getColumn()).getName();
                    addColumn(columns, database, table, columnName);
                }
            }

            // 处理WHERE子句中的字段
            if (stmt.getWhere() != null) {
                processWhereClause(stmt.getWhere(), columns);
            }
        }

        private void handleDeleteStatement(SQLDeleteStatement stmt, List<ParsedColumnMetaDto> columns) {
            // 处理WHERE子句中的字段
            if (stmt.getWhere() != null) {
                processWhereClause(stmt.getWhere(), columns);
            }
        }


        private void processTableSource(SQLTableSource tableSource, List<ParsedColumnMetaDto> columns) {
            if (tableSource instanceof SQLJoinTableSource join) {
                processTableSource(join.getLeft(), columns);
                processTableSource(join.getRight(), columns);

                // 处理JOIN条件
                if (join.getCondition() != null) {
                    processWhereClause(join.getCondition(), columns);
                }
            } else if (tableSource instanceof SQLSubqueryTableSource subQuery) {
                String alias = subQuery.getAlias();
                // 标记为临时表
                aliasTableMap.put(alias, new SqlLineageTableColDepDto().setSubQuery(true));
                handleSelectStatement(subQuery.getSelect().getQuery(), columns);

            } else if (tableSource instanceof SQLExprTableSource table) {
                // 记录基础表信息，可用于后续解析字段所属表
                String database = table.getSchema();
                String tableName = table.getName().getSimpleName();
                String alias = table.getAlias();
                // 可以存储表别名映射关系，用于解析字段所属表
                if (alias != null) {
                    aliasTableMap.put(alias, new SqlLineageTableColDepDto()
                            .setParentTableName(tableName)
                            .setParentDatabase(database));
                }
            }
        }

        private void processWhereClause(SQLExpr where, List<ParsedColumnMetaDto> columns) {
            Map<String, SqlLineageTableColDepDto> tmp = this.aliasTableMap;
            // 使用访问者模式收集WHERE子句中的字段
            where.accept(new SQLASTVisitorAdapter() {
                @Override
                public boolean visit(SQLPropertyExpr x) {
                    if (x.getOwner() instanceof SQLIdentifierExpr) {
                        String tableAlias = ((SQLIdentifierExpr) x.getOwner()).getName();
                        SqlLineageTableColDepDto table = tmp.getOrDefault(tableAlias, new SqlLineageTableColDepDto());
                        if (table.isSubQuery()) {
                            return true;
                        }
                        addColumn(columns, table.getDatabaseName(), table.getTableName(), x.getName());
                    }
                    return true;
                }
            });
        }

        private void addColumn(List<ParsedColumnMetaDto> columns, String database,
                               String table, String column) {
            columns.add(ParsedColumnMetaDto.builder()
                    .databaseName(database)
                    .tableName(table)
                    .columnName(column)
                    .build());
        }

        // 解析SQL语句中的字段依赖关系
        public void parseColumnDependency(String sql) {
            // 解析SQL语句
            List<SQLStatement> sqlStatements = parsePgStatements(sql);
            // 遍历解析后的SQL语句
            for (SQLStatement sqlStatement : sqlStatements) {
                // 如果是INSERT语句
                if (sqlStatement instanceof PGInsertStatement insert) {
                    // 获取SELECT语句
                    SQLSelect select = insert.getQuery();
                    // 获取目标表信息
                    String targetTable = insert.getTableName().getSimpleName();
                    String targetSchema = insert.getTableSource().getSchema();
                    String catalog = insert.getTableSource().getCatalog();

                    // 获取插入的字段列表
                    List<SQLExpr> columns = insert.getColumns();

                    // 获取SELECT语句的字段列表
                    SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) select.getQuery();
                    List<SQLSelectItem> selectItems = queryBlock.getSelectList();
                    // 解析FROM子句
                    processFromClause(queryBlock.getFrom());
                    // 建立字段映射关系
                    for (int i = 0; i < columns.size(); i++) {
                        // 获取插入的字段
                        SQLExpr column = columns.get(i);
                        // 获取SELECT语句的字段
                        SQLSelectItem selectItem = selectItems.get(i);

                        // 创建目标字段节点
                        SqlLineageColumnNodeEntity root = new SqlLineageColumnNodeEntity();
                        root.setSchemaName(targetSchema);
                        root.setTableName(targetTable);
                        root.setConnectionIp(this.getConnectionIp());
                        root.setPort(this.getPort());
                        root.setDatabaseName(StrUtil.isBlank(catalog) ? TableConstants.DEFAULT_DATABASE_NAME : catalog);
                        root.setDatabaseType(DbType.postgresql.toString());
                        root.setColumnName(column.toString().replaceAll("`", ""));

                        // 分析来源字段,构建字段依赖关系
                        List<SqlLineageColumnNodeEntity> sourceColumns = extractSourceColumns(selectItem);
                        root.convertEdge(sourceColumns);
                        // 保存节点和关系
                        createColumnLineage(root);
                    }
                }
            }
        }


        private List<SQLStatement> parsePgStatements(String sql) {
            return SQLUtils.parseStatements(sql, DbType.postgresql);
        }

        private void createColumnLineage(SqlLineageColumnNodeEntity targetColumn) {
            SqlLineageColumnNodeEntity sqlLineageColumnNodeEntity = retrieveColumnNode(targetColumn.getDatabaseName(), targetColumn.getSchemaName(), targetColumn.getTableName(), targetColumn.getColumnName());
            if (Objects.isNull(sqlLineageColumnNodeEntity)) {
                // 走新增
                sqlLineageColumnRepository.save(targetColumn);
                return;
            }
            // 对比 边数据
            List<SqlLineageColumnEdgeEntity> edgeList = sqlLineageColumnNodeEntity.getOutRelationShip();
            Map<String, SqlLineageColumnEdgeEntity> newEdgeMap = targetColumn.getOutRelationShip().stream().collect(Collectors.toMap(SqlLineageColumnEdgeEntity::getBusinessId, Function.identity()));
            List<SqlLineageColumnEdgeEntity> targetOutgoingList = targetColumn.getOutRelationShip();
            for (SqlLineageColumnEdgeEntity sqlLineageColumnEdgeEntity : edgeList) {
                // 原有的边也需要在这次新增中
                if (!newEdgeMap.containsKey(sqlLineageColumnEdgeEntity.getBusinessId())) {
                    targetOutgoingList.add(sqlLineageColumnEdgeEntity);
                }
            }
            sqlLineageColumnRepository.save(targetColumn);
        }

        private List<SqlLineageColumnNodeEntity> extractSourceColumns(SQLSelectItem selectItem) {
            List<SqlLineageColumnNodeEntity> sourceColumns = new ArrayList<>();

            // 递归分析表达式
            analyzeExpression(selectItem.getExpr(), sourceColumns);

            return sourceColumns;
        }

        private void analyzeExpression(SQLExpr expr, List<SqlLineageColumnNodeEntity> sourceColumns) {
            if (expr == null) return;

            switch (expr) {
                case SQLPropertyExpr sqlPropertyExpr ->
                    //普通字段引用 (例如: table.column)
                        handlePropertyExpr(sqlPropertyExpr, sourceColumns);
                case SQLIdentifierExpr sqlIdentifierExpr ->
                    //无表前缀的字段引用
                        handleIdentifierExpr(sqlIdentifierExpr, sourceColumns);
                case SQLQueryExpr sqlQueryExpr ->
                    //子查询
                        handleSubQuery(sqlQueryExpr, sourceColumns);
                case SQLBinaryOpExpr sqlBinaryOpExpr ->
                    //二元运算表达式 (例如: a + b, a = b)
                        handleBinaryOpExpr(sqlBinaryOpExpr, sourceColumns);
                case SQLMethodInvokeExpr sqlMethodInvokeExpr ->
                    //函数调用 (例如: COUNT(*), SUM(column))
                        handleMethodInvokeExpr(sqlMethodInvokeExpr, sourceColumns);
                case SQLCaseExpr sqlCaseExpr ->
                    //CASE表达式
                        handleCaseExpr(sqlCaseExpr, sourceColumns);
                default -> {
                }
            }
        }

        private void handleIdentifierExpr(SQLIdentifierExpr expr, List<SqlLineageColumnNodeEntity> sourceColumns) {
            //没有表前缀的列引用
            // 这种情况需要从上下文中推断表信息
            String columnName = expr.getName();

            // 如果只有一个表，可以直接使用该表信息
            if (aliasTableMap.size() == 1) {
                SqlLineageTableColDepDto tableInfo = aliasTableMap.values().iterator().next();
                SqlLineageColumnNodeEntity column = new SqlLineageColumnNodeEntity();
                column.setSchemaName(tableInfo.getSchemaName());
                column.setTableName(tableInfo.getTableName());
                column.setColumnName(columnName);
                sourceColumns.add(column);
            }
            // 如果有多个表，可能需要通过其他方式确定列属于哪个表
            // 这里可以添加更复杂的逻辑来处理
        }

        private void handlePropertyExpr(SQLPropertyExpr expr, List<SqlLineageColumnNodeEntity> sourceColumns) {
            String owner = "";
            if (expr.getOwner() instanceof SQLIdentifierExpr) {
                owner = ((SQLIdentifierExpr) expr.getOwner()).getName();
            }

            // 通过别名查找实际的表信息
            SqlLineageTableColDepDto tableInfo = aliasTableMap.get(owner);
            if (Objects.isNull(tableInfo)) {
                log.warn("Empty tableInfo for alias: {}", owner);
                return;
            }

            if (!tableInfo.isSubQuery() || Objects.isNull(tableInfo.getColumnMap())) {
                SqlLineageColumnNodeEntity column = new SqlLineageColumnNodeEntity();
                column.setSchemaName(tableInfo.getSchemaName());
                column.setDatabaseName(tableInfo.getDatabaseName());
                column.setTableName(tableInfo.getTableName());
                column.setColumnName(expr.getName().replaceAll("`", ""));
                // todo 临时写死PG
                column.setDatabaseType(DbType.postgresql.toString());
                sourceColumns.add(column);
                return;
            }
            // 因为子查询中的临时表没有数据表的信息，实际上是内部真实表的信息
            SqlLineageColumnDependencyDto subColumnDependencyDto = tableInfo.getColumnMap().get(expr.getName());
            SqlLineageColumnNodeEntity subColumnNodeEntity = SqlLineageColumnNodeEntity.convert(subColumnDependencyDto);
            sourceColumns.add(subColumnNodeEntity);
        }

        private void handleSubQuery(SQLQueryExpr expr, List<SqlLineageColumnNodeEntity> sourceColumns) {
            SQLSelectQueryBlock subQuery = (SQLSelectQueryBlock) expr.getSubQuery().getQuery();

            // 保存当前的别名映射
            Map<String, SqlLineageTableColDepDto> oldAliasMap = new HashMap<>(aliasTableMap);

            // 处查询的FROM子句，更新别名映射
            processFromClause(subQuery.getFrom());

            // 处查询的选择项
            for (SQLSelectItem item : subQuery.getSelectList()) {
                analyzeExpression(item.getExpr(), sourceColumns);
            }

            // 恢复之前的别名映射
            aliasTableMap = oldAliasMap;
        }

        private void handleBinaryOpExpr(SQLBinaryOpExpr expr, List<SqlLineageColumnNodeEntity> sourceColumns) {
            // 递归处左右两边的表达式
            analyzeExpression(expr.getLeft(), sourceColumns);
            analyzeExpression(expr.getRight(), sourceColumns);
        }

        private void handleMethodInvokeExpr(SQLMethodInvokeExpr expr, List<SqlLineageColumnNodeEntity> sourceColumns) {
            //函数的参数
            for (SQLExpr arg : expr.getArguments()) {
                analyzeExpression(arg, sourceColumns);
            }
        }

        private void handleCaseExpr(SQLCaseExpr expr, List<SqlLineageColumnNodeEntity> sourceColumns) {
            //CASE表达式的值部分
            analyzeExpression(expr.getValueExpr(), sourceColumns);

            //WHEN子句
            for (SQLCaseExpr.Item item : expr.getItems()) {
                analyzeExpression(item.getConditionExpr(), sourceColumns);
                analyzeExpression(item.getValueExpr(), sourceColumns);
            }

            //ELSE子句
            analyzeExpression(expr.getElseExpr(), sourceColumns);
        }


        private void processFromClause(SQLTableSource tableSource) {
            if (tableSource instanceof SQLJoinTableSource join) {
                processFromClause(join.getLeft());
                processFromClause(join.getRight());
                //JOIN条件
                if (join.getCondition() != null) {
                    List<SqlLineageColumnNodeEntity> joinColumns = new ArrayList<>();
                    analyzeExpression(join.getCondition(), joinColumns);
                }
            } else if (tableSource instanceof SQLSubqueryTableSource subQuery) {
                String subQueryAlias = subQuery.getAlias();
                SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) subQuery.getSelect().getQuery();

                // 创建子查询的表信息
                SqlLineageTableColDepDto subQueryInfo = new SqlLineageTableColDepDto()
                        .alias(subQueryAlias)
                        .isSubQuery(true)
                        .columnMap(new HashMap<>());
                aliasTableMap.put(subQueryAlias, subQueryInfo);

                processFromClause(queryBlock.getFrom());

                for (SQLSelectItem item : queryBlock.getSelectList()) {
                    processSubQuerySelectItem(item, subQueryAlias);
                }
            } else if (tableSource instanceof SQLExprTableSource table) {
                //基础表
                String schema = table.getSchema();
                String tableName = table.getName().getSimpleName();
                String alias = table.getAlias();

                aliasTableMap.put(alias != null ? alias : tableName,
                        new SqlLineageTableColDepDto()
                                .schemaName(schema)
                                .tableName(tableName)
                                .alias(alias));
            }
        }

        private void processSubQuerySelectItem(SQLSelectItem item, String subQueryAlias) {
            String columnAlias = item.getAlias();
            if (columnAlias == null && item.getExpr() instanceof SQLPropertyExpr) {
                columnAlias = ((SQLPropertyExpr) item.getExpr()).getName();
            }

            if (item.getExpr() instanceof SQLPropertyExpr expr) {
                String sourceAlias = ((SQLIdentifierExpr) expr.getOwner()).getName();
                String sourceColumn = expr.getName();

                // 查找原始表信息
                SqlLineageTableColDepDto sourceTable = aliasTableMap.get(sourceAlias);
                if (sourceTable != null) {
                    SqlLineageColumnDependencyDto columnInfo = SqlLineageColumnDependencyDto.builder()
                            .sourceSchema(sourceTable.getSchemaName())
                            .sourceTable(sourceTable.getTableName())
                            .sourceColumn(sourceColumn)
                            .targetColumn(columnAlias)
                            .intermediateAlias(subQueryAlias)
                            .build();

                    // 将映射信息存储在子查询的表信息中
                    SqlLineageTableColDepDto subQueryTable = aliasTableMap.get(subQueryAlias);
                    if (subQueryTable != null) {
                        subQueryTable.getColumnMap().put(columnAlias, columnInfo);
                    }
                }
            }
        }


        public SqlLineageColumnNodeEntity retrieveColumnNode(String database, String schema, String table, String column) {
            String id = database + ":" + schema + ":" + table + ":" + column;
            return sqlLineageColumnRepository.retrieveColumnNodeById(id);
        }


    }


}
