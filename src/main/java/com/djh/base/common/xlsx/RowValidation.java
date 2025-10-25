package com.djh.base.common.xlsx;

import java.util.List;

public interface RowValidation {
    void run(String currentExcelRow, List<List<String>> data);
}
