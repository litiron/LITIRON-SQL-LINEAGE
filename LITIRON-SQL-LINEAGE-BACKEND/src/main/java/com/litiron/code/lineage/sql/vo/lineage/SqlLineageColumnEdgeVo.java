package com.litiron.code.lineage.sql.vo.lineage;

import com.litiron.code.lineage.sql.entity.column.SqlLineageColumnNodeEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @description: 字段的边vo
 * @author: 李日红
 * @create: 2025/4/17 20:52
 */
@Getter
@Setter
public class SqlLineageColumnEdgeVo {

    private String id;

    // 业务逻辑唯一
    private String businessId;

    private SqlLineageColumnNodeVo sqlLineageColumnNode;
}
