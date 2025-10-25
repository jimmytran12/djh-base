package com.djh.base.common.xlsx;

import com.djh.base.common.xlsx.model.ExcelDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CellInfo {
    private ExcelDataType dataType;
    private String value;
}
