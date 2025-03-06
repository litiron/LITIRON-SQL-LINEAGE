package com.litiron.code.lineage.sql.dto.lineage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 李日红
 * @description: 结点连接线关系DTO
 * @create 2025/2/28 17:50
 */
@Setter
@Getter
public class SqlLineageTableEdgeDto {
    private String id;
    private SqlLineageTableNodeDto to;
    private String relation;
}
