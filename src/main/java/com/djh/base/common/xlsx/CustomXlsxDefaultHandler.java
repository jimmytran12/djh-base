package com.djh.base.common.xlsx;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class CustomXlsxDefaultHandler extends DefaultHandler {
    // Shared string table object
    private final SharedStrings sst;
    // Cell content
    private String lastContents;
    // Is it a string
    private boolean nextIsString;
    // Is it a numeric (date field is a numeric)
    private boolean nextIsNumeric;
    // Key-value pair (cell column name, cell value)
    private final HashMap<String, String> cellMap;
    // Cell coordinates
    private String cellCoordinates;
    // Cell value
    private String cellValue;
    // Style index
    private int styleIndex;
    // Style table of all workbook
    private final StylesTable stylesTable;
    // Cell style - data formatter
    private final DataFormatter formatter;
    // List row data, each row has list column data
    private final List<List<String>> data;

    // custom validation
    private final RowValidation validation;

    public CustomXlsxDefaultHandler(
            SharedStrings sst, StylesTable stylesTable, RowValidation validation) {
        this.sst = sst;
        this.stylesTable = stylesTable;
        this.validation = validation;
        this.formatter = new DataFormatter();
        this.cellMap = new HashMap<>();
        this.data = new ArrayList<>();
    }

    public List<List<String>> getData() {
        return data;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) {
        // c => cell
        if ("c".equals(name)) {
            // Print the cell reference
            this.cellCoordinates = attributes.getValue("r");
            this.cellValue = "";
            // Figure out if the value is an index in the SST
            var cellType = attributes.getValue("t");
            if ("s".equals(cellType)) {
                this.nextIsString = true;
            } else {
                // When the cell is in date format, the value of the s attribute in the c tag is a
                // number
                cellType = attributes.getValue("s");
                this.nextIsString = false;
            }
            // Determine whether it is a date format
            if (cellType != null
                    && Pattern.compile("^[-\\+]?[\\d]*$").matcher(cellType).matches()) {
                this.styleIndex = Integer.parseInt(cellType);
                this.nextIsNumeric = true;
            } else {
                this.nextIsNumeric = false;
            }
        }
        this.lastContents = "";
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        // Process the last contents as required.
        // Do now, as characters() may be called more than once
        if (this.nextIsString) {
            int idx = Integer.parseInt(this.lastContents);
            this.lastContents = sst.getItemAt(idx).getString();
            this.nextIsString = false;
        }
        if (this.nextIsNumeric && !"".equals(this.lastContents)) {
            var style = stylesTable.getStyleAt(this.styleIndex);
            short formatIndex = style.getDataFormat();
            var formatString = style.getDataFormatString();
            this.lastContents =
                    formatter.formatRawCellContents(
                            Double.parseDouble(this.lastContents), formatIndex, formatString);
            this.nextIsNumeric = false;
        }
        // v => contents of a cell
        // Output after we've seen the string contents
        if ("v".equals(name)) {
            this.cellValue = this.lastContents;
        } else if ("c".equals(name)) {
            this.cellMap.put(this.cellCoordinates.replaceAll("\\d+", ""), this.cellValue);
        } else if ("row".equals(name)) {
            var currentExcelRow =
                    this.cellCoordinates.replaceAll(
                            this.cellCoordinates.replaceAll("\\d+", ""), "");
            data.add(new ArrayList<>(this.cellMap.values()));
            validateRowData(currentExcelRow);
            this.cellMap.clear();
        }
    }

    private void validateRowData(String currentExcelRow) {
        validation.run(currentExcelRow, data);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        this.lastContents += new String(ch, start, length);
    }
}
