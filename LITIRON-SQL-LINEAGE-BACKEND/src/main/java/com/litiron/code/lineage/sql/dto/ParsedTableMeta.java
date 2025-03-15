package com.litiron.code.lineage.sql.dto;

import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableBaseDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableNodeDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 解析数据之后的相关的信息
 * @author: Litiron
 * @create: 2024-06-08 10:19
 **/
@Getter
@Setter
public class ParsedTableMeta {

    private List<SqlLineageTableDto> sourceTableList = new ArrayList<>();

    private List<SqlLineageTableDto> destTableList = new ArrayList<>();

    @Override
    public String toString() {
        return "sourceTableList: " + sourceTableList.toString() + "\n" + "destTableList: " + destTableList.toString();
    }
}
