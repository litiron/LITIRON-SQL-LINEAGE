package com.litiron.code.lineage.sql.service.langchain.impl;

import com.litiron.code.lineage.sql.constants.DatabaseConnectionConstant;
import com.litiron.code.lineage.sql.dto.database.ColumnStructureInfoDto;
import com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto;
import com.litiron.code.lineage.sql.dto.database.DatabaseStructInfoDto;
import com.litiron.code.lineage.sql.dto.database.TableStructureInfoDto;
import com.litiron.code.lineage.sql.service.DatabaseComplexService;
import com.litiron.code.lineage.sql.service.LineageAnalysisService;
import com.litiron.code.lineage.sql.service.database.DatabaseConnectionService;
import com.litiron.code.lineage.sql.service.langchain.AIService;
import com.litiron.code.lineage.sql.service.langchain.tools.DatabaseContentTools;
import com.pgvector.PGvector;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


/**
 * @author 李日红
 * @description: AI服务实现类
 * @create 2025/4/8 23:32
 */
@Service
@Slf4j
@AllArgsConstructor
public class AIServiceImpl implements AIService {

    private final OpenAiStreamingChatModel langchain4jWebClient;
    private final DatabaseComplexService databaseComplexService;
    private final OpenAiEmbeddingModel embeddingModel;
    private final DatabaseConnectionService databaseConnectionService;
    private final LineageAnalysisService lineageAnalysisService;

    @Override
    public SseEmitter askQuestion(String question) {
        SseEmitter emitter = new SseEmitter();

        DatabaseContentTools queryTool = new DatabaseContentTools(embeddingModel, databaseConnectionService, lineageAnalysisService);

        Assistant assistant = AiServices.builder(Assistant.class).streamingChatLanguageModel(langchain4jWebClient).tools(queryTool).build();

        TokenStream tokenStream = assistant.generateSql(question);
        StringBuilder markdownBuffer = new StringBuilder();
        tokenStream.onPartialResponse((String partialResponse) -> {
            log.info(partialResponse);
            if ("[DONE]".equals(partialResponse)) {
                if (markdownBuffer.length() > 0) {
                    try {
                        emitter.send(SseEmitter.event().data(markdownBuffer.toString()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    markdownBuffer.setLength(0);
                }
                emitter.complete();
                return;
            }
            markdownBuffer.append(partialResponse);
            int splitPos = findNaturalSplit(markdownBuffer);
            if (splitPos > 0) {
                String chunk = markdownBuffer.substring(0, splitPos);
                try {
                    emitter.send(SseEmitter.event().data(chunk));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                markdownBuffer.delete(0, splitPos);
            }
        }).onRetrieved(System.out::println).onToolExecuted(System.out::println).onCompleteResponse((ChatResponse response) -> emitter.complete()).onError(Throwable::printStackTrace).start();

        return emitter;
    }

    @Override
    public void storageVector() {
        List<DatabaseConnectionDto> connectionList = databaseComplexService.retrieveDatabaseConnectionInfo();

        for (DatabaseConnectionDto connection : connectionList) {
            if (DatabaseConnectionConstant.CONNECTION_TYPE_PGSQL.equals(connection.getType())) {
                //pgsql
                List<String> pgDbList = databaseComplexService.retrievePgDatabasesInfo(connection.getId());
                for (String pg : pgDbList) {
                    List<DatabaseStructInfoDto> databaseStructInfoDtoList = databaseComplexService.updateDatabaseConnection(connection.getId(), pg);
                    storageTableStruct(databaseStructInfoDtoList, pg, connection.getId());

                }

            }
            List<DatabaseStructInfoDto> databaseStructInfoDtoList = databaseComplexService.updateDatabaseConnection(connection.getId(), "");
            storageTableStruct(databaseStructInfoDtoList, "", connection.getId());


        }
    }

    private void storageTableStruct(List<DatabaseStructInfoDto> databaseStructInfoDtoList, String pgDbName, String id) {
        for (DatabaseStructInfoDto db : databaseStructInfoDtoList) {
            String databaseName = db.getDatabaseName();
            List<TableStructureInfoDto> tableStructureInfoDtoList = db.getTableStructureInfoDtoList();
            for (TableStructureInfoDto table : tableStructureInfoDtoList) {
                String description = generateTableDescription(table); // 生成表结构描述
                Embedding embedding = embeddingModel.embed(description).content();
                String url = "jdbc:postgresql://47.120.71.217:5432/sql_lineage_pg";
                String driveClass = "org.postgresql.Driver";
                // 存储到pgvector
                try {
                    Class.forName(driveClass);
                    Connection conn = DriverManager.getConnection(url, "postgres", "lrh@89757");
                    String sql = "INSERT INTO \"ods_hr\".\"table_embeddings\"(table_name,database_name,schema_name,connection_id, table_description, embedding) " + "VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, table.getTableName());
                    stmt.setString(2, databaseName);
                    stmt.setString(3, pgDbName);
                    stmt.setString(4, id);
                    stmt.setString(5, description);
                    stmt.setObject(6, new PGvector(embeddingToFloatArray(embedding)));
                    stmt.executeUpdate();
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

    private float[] embeddingToFloatArray(Embedding embedding) {
        float[] floats = new float[embedding.vector().length];
        for (int i = 0; i < embedding.vector().length; i++) {
            floats[i] = (float) embedding.vector()[i];
        }
        return floats;
    }

    private String generateTableDescription(TableStructureInfoDto table) {
        StringBuilder sb = new StringBuilder();
        sb.append("表名：").append(table.getTableName()).append("，\n");
        sb.append("表注释：").append(table.getTableComment()).append("，\n");
        sb.append("字段结构：，\n");

        for (ColumnStructureInfoDto column : table.getColumnStructureInfoDtoList()) {
            sb.append("- ").append(column.getColumnName()).append("（").append(column.getColumnComment()).append("），\n");
        }
        return sb.toString();
    }

    private int findNaturalSplit(StringBuilder buffer) {
        int codeBlockEnd = buffer.lastIndexOf("```\n");
        if (codeBlockEnd != -1) {
            return codeBlockEnd + 4;
        }
        int doubleNewline = buffer.lastIndexOf("\n\n");
        if (doubleNewline != -1) {
            return doubleNewline + 2;
        }
        int lastSentenceEnd = Math.max(buffer.lastIndexOf("。"), Math.max(buffer.lastIndexOf("！"), Math.max(buffer.lastIndexOf("？"), buffer.lastIndexOf("\n"))));
        return lastSentenceEnd != -1 ? lastSentenceEnd + 1 : -1;
    }


}

interface Assistant {
    @SystemMessage("""
            你是一个智能 SQL 生成器，根据以下规则工作：
            1. 使用 findRelevantTable 工具自动选择最相关的表结构信息
            2. 若工具返回未匹配，提示用户补充信息
            3. 根据上一步的结果（最相关表的结构信息）使用 findRelevantLineage 工具自动获取与其相关联的表以及关联字段和表结构
            4. JOIN条件需严格匹配relation_field，根据表结构信息以及关联关系（字段）严格使用匹配到的表结构生成 SQL
            5. 优先使用表注释理解业务含义
            6. 生成SQL（字段引用格式：MySQL用`数据库.表.字段`，PG用`模式.表.字段`）
            7. 根据上一步的结果(SQL和表结构信息)使用 getSqlExeData 工具自动获取数据信息（如果结果数据为0则填充0，不用省略）
            8. Echarts数据映射必须用data1:作为属性前缀，在xAxis配置中必须包含 data1: 开头的数组声明，每个series对象必须包含 data1: 开头的数组声明，只要是有data:的内容
            统统改为data1:，例如以下结构 
              xAxis: {
                       data1: data.map(item => item.name),
                        },
                        series: [{
                            data1: data.map(item => item.value),
                        }]
                        , legend: {
                          data1:['学生人数']
                                     },
            9. 请根据以上数据格式要求，返回生成的SQL 以及 返回 用户要求的完整的图形HTML代码,代码后需要给出一句话”点击下方按钮可运行此图表“，其他话不用描述
            """)
    @UserMessage("问题：{{query}}")
    TokenStream generateSql(String query);
}
