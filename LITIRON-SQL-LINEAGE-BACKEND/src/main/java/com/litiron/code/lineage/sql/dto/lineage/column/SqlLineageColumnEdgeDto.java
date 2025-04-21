package com.litiron.code.lineage.sql.dto.lineage.column;

import com.litiron.code.lineage.sql.entity.column.SqlLineageColumnNodeEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @description: 字段的边DTO
 * @author: 李日红
 * @create: 2025/4/17 20:52
 */
@Getter
@Setter
public class SqlLineageColumnEdgeDto {

    private String id;

    // 业务逻辑唯一
    private String businessId;

    /*
     * 上游或者是下游数据
     *
     */
    private String direction;

    private SqlLineageColumnNodeDto sqlLineageColumnNode;
}
