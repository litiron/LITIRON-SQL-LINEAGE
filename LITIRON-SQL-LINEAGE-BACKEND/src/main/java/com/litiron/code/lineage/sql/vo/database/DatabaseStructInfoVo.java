package com.litiron.code.lineage.sql.vo.database;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 李日红
 * @description: 数据库的树形结构Vo
 * @create 2024/12/7 11:43
 */
@Setter
@Getter
public class DatabaseStructInfoVo {
    private String databaseName;
    private List<TableStructureInfoVo> tableStructureInfoDtoList;
}
