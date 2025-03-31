package com.litiron.code.lineage.sql.dto.lineage.table;

import com.litiron.code.lineage.sql.entity.table.SqlLineageTableNodeEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 结点连接线关系
 * @author: Litiron
 * @create: 2024-07-02 22:01
 **/
@Setter
@Getter
public class SqlLineageEdgeDto {

    private SqlLineageTableNodeEntity to;

    private String leftTableName;

    private String rightTableName;

    private List<Map<String, String>> joinFieldList = new ArrayList<>();
}
