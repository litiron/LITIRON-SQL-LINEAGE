package com.litiron.code.lineage.sql.dto.database;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 李日红
 * @description: 数据库的树形结构Dto
 * @create 2024/11/30 21:07
 */
@Getter
@Setter
public class DatabaseStructInfoDto {
    String databaseName;
    List<TableStructureInfoDto> tableStructureInfoDtoList;
}
