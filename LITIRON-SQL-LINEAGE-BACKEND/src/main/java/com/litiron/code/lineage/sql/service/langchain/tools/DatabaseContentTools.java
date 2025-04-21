package com.litiron.code.lineage.sql.service.langchain.tools;

/**
 * @author 李日红
 * @description: Langchain4j自定义工具类
 * @create 2025/4/13 0:11
 */

import cn.hutool.json.JSONUtil;
import com.litiron.code.lineage.sql.constants.DatabaseConnectionConstant;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageSearchNodeParamsDto;
import com.litiron.code.lineage.sql.dto.lineage.table.SqlLineageTableEdgeDto;
import com.litiron.code.lineage.sql.dto.lineage.table.SqlLineageTableNodeDto;
import com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity;
import com.litiron.code.lineage.sql.service.LineageAnalysisService;
import com.litiron.code.lineage.sql.service.database.DatabaseConnectionService;
import com.pgvector.PGvector;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.*;

@AllArgsConstructor
public class DatabaseContentTools {
    private final OpenAiEmbeddingModel embeddingModel;
    private final DatabaseConnectionService databaseConnectionService;
    private final LineageAnalysisService lineageAnalysisService;

    @Tool("根据生成的SQL和表结构并执行获取返回的数据")
    public String getSqlExeData(String sql, Map<String, String> tableStruct) {
        String schemaName = tableStruct.get("schema_name");
        String connectionId = tableStruct.get("connection_id");
        DatabaseConnectionEntity connectionEntity = databaseConnectionService.getDatabaseConnectionInfoById(connectionId);
        String url = "";
        String driveClass = "";
        if (connectionEntity.getType().equals(DatabaseConnectionConstant.CONNECTION_TYPE_MYSQL)) {
            url = "jdbc:mysql://" + connectionEntity.getIp() + ":" + connectionEntity.getPort() + "?useSSL=false&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8";
            driveClass = "com.mysql.cj.jdbc.Driver";
        } else {
            url = "jdbc:postgresql://" + connectionEntity.getIp() + ":" + connectionEntity.getPort() + "/" + schemaName;
            driveClass = "org.postgresql.Driver";
        }
        try {
            Class.forName(driveClass);
            Connection conn = DriverManager.getConnection(url, connectionEntity.getUserName(), connectionEntity.getPassword());
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<Map<String, Object>> dataList = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                dataList.add(row);
            }
            rs.close();
            stmt.close();
            conn.close();
            return JSONUtil.toJsonStr(dataList);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Tool("根据用户问题查找最相关的表结构,返回表结构信息")
    public Map<String, String> findRelevantTable(String userQuery) {
        Embedding queryEmbedding = embeddingModel.embed(userQuery).content();
        String url = "jdbc:postgresql://47.120.71.217:5432/sql_lineage_pg";
        String driveClass = "org.postgresql.Driver";
        try {
            Class.forName(driveClass);
            Connection conn = DriverManager.getConnection(url, "postgres", "lrh@89757");
            String sql = "SELECT  table_name, database_name, schema_name, connection_id, table_description "
                    + "FROM ods_hr.table_embeddings ORDER BY embedding <=> ? LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, new PGvector(embeddingToFloatArray(queryEmbedding)));
            ResultSet rs = stmt.executeQuery();
            Map<String, String> resultMap = new HashMap<>();
            while (rs.next()) {
                resultMap.put("table_name", rs.getString("table_name"));
                resultMap.put("database_name", rs.getString("database_name"));
                resultMap.put("schema_name", rs.getString("schema_name"));
                resultMap.put("connection_id", rs.getString("connection_id"));
                resultMap.put("table_description", rs.getString("table_description"));
            }
            rs.close();
            stmt.close();
            conn.close();
            return resultMap.isEmpty() ? Collections.emptyMap() : resultMap;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("查询表结构失败: " + e.getMessage(), e);
        }
    }

    @Tool("获取表的血缘关系，返回关联表结构及字段信息")
    public String findRelevantLineage(Map<String, String> tableStruct) {
        List<Map<String, Object>> lineageInfo = new ArrayList<>();
        SqlLineageSearchNodeParamsDto sqlLineageSearchNodeParamsDto = buildSqlLineageTableNodeParamsDto(tableStruct);
        // 获取原始血缘关系
        List<SqlLineageTableNodeDto> relationships = lineageAnalysisService.retrieveNeo4jTableInfo(sqlLineageSearchNodeParamsDto);
        if (relationships.isEmpty()) {
            return "该表没有血缘关系，不需要做表连接";
        }
        for (SqlLineageTableEdgeDto relation : relationships.get(0).getOutgoingRelationShip()) {
            Map<String, Object> relationInfo = new HashMap<>();
            relationInfo.put("related_table", relation.getTo().getTableName());
            relationInfo.put("relation_field", relation.getRelationFiled());
            //关联表的结构
            Map<String, String> relevantTable = findRelevantTable(relation.getTo().getTableName());
            relationInfo.put("fields", relevantTable);
            lineageInfo.add(relationInfo);
        }
        return JSONUtil.toJsonStr(lineageInfo);
    }

    private SqlLineageSearchNodeParamsDto buildSqlLineageTableNodeParamsDto(Map<String, String> tableStruct) {
        SqlLineageSearchNodeParamsDto sqlLineageSearchNodeParamsDto = new SqlLineageSearchNodeParamsDto();
        sqlLineageSearchNodeParamsDto.setTableName(tableStruct.get("table_name"));
        sqlLineageSearchNodeParamsDto.setSchemaName(tableStruct.get("schema_name"));
        sqlLineageSearchNodeParamsDto.setDatabaseName(tableStruct.get("database_name"));
        sqlLineageSearchNodeParamsDto.setId(tableStruct.get("connection_id"));
        return sqlLineageSearchNodeParamsDto;
    }

    private float[] embeddingToFloatArray(Embedding embedding) {
        float[] floats = new float[embedding.vector().length];
        for (int i = 0; i < embedding.vector().length; i++) {
            floats[i] = embedding.vector()[i];
        }
        return floats;
    }
}
