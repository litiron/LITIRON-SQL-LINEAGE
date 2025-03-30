package com.litiron.code.lineage.sql.vo.lineage;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 李日红
 * @description: 结点连接线关系DTO
 * @create 2025/2/28 17:50
 */
@Setter
@Getter
public class SqlLineageTableEdgeVo {
    private String id;

    private SqlLineageTableNodeVo to;

    private String leftTableName;

    private String rightTableName;

    private String direction;

    private String relationFiled;

    private List<Map<String, String>> joinFieldList = new ArrayList<>();
}
