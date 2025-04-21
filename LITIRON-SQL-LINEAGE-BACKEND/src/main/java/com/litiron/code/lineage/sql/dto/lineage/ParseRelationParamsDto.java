package com.litiron.code.lineage.sql.dto.lineage;

import lombok.Getter;
import lombok.Setter;

/**
 * @description: sql解析相关参数
 * @author: Litiron
 * @create: 2024-06-16 19:42
 **/
@Getter
@Setter
public class ParseRelationParamsDto {
    private String sql;
    private String connectionId;
    private String pgDbName;
}
