package com.djh.base.common.xlsx;

import com.djh.base.common.config.exception.DoubleJsHouseException;
import com.djh.base.common.config.exception.ErrorCode;
import com.djh.base.common.constant.BaseConstant;
import com.djh.base.common.xlsx.model.ExcelCellStyle;
import com.djh.base.common.xlsx.model.ExcelDataType;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class XlsxCommon<T> {
    private static final Logger logger = LoggerFactory.getLogger(XlsxCommon.class);
    protected static final byte[] FOREGROUND_COLOR_HEADER =
            new byte[]{(byte) 242, (byte) 242, (byte) 242};

    protected abstract List<CellInfo> getColumnElements(int rowIndex, T model);

    protected abstract List<String> getHeaderFields();

    public File createXlsxFile(String fileName, List<T> modelData) {
        File file = null;
        try (var workbook = new SXSSFWorkbook()) {
            var xSSFSheet = workbook.createSheet(fileName);
            var cellStyleMap = setUpCellStyle(workbook);

            // setup worksheet
            setUpWorkSheets(xSSFSheet, getHeaderFields(), cellStyleMap);
            setUpWorkSheetsConstraint(xSSFSheet, modelData);

            // process data
            processWorkSheet(xSSFSheet, modelData, cellStyleMap);
            file =
                    new File(
                            fileName
                                    + LocalDateTime.now()
                                    .format(
                                            DateTimeFormatter.ofPattern(
                                                    "_yyyy_MM_dd_HH_mm_ss"))
                                    + BaseConstant.EXTENSIONS_XLSX);
            var out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            logger.info("Error generating excel file: {} {} ", fileName, e.getMessage());
        }
        return file;
    }

    protected Map<ExcelCellStyle, XSSFCellStyle> setUpCellStyle(SXSSFWorkbook workbook) {
        var cellStyleMap = new EnumMap<ExcelCellStyle, XSSFCellStyle>(ExcelCellStyle.class);
        var headerStyle = createCellStyleString(workbook);

        headerStyle.getFont().setBold(true);
        headerStyle.setFillForegroundColor(new XSSFColor(FOREGROUND_COLOR_HEADER, null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyleMap.put(ExcelCellStyle.HEADER, headerStyle);

        var textCellStyle = createCellStyleString(workbook);
        cellStyleMap.put(ExcelCellStyle.TEXT, textCellStyle);

        var dateCellStyle = createCellStyleString(workbook);
        dateCellStyle.setDataFormat(BaseConstant.FORMAT_DATE);
        cellStyleMap.put(ExcelCellStyle.DATE, dateCellStyle);

        var format = workbook.createDataFormat();
        var numberCellStyle = createCellStyleString(workbook);

        numberCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        numberCellStyle.setDataFormat(format.getFormat("#,##0.00"));
        cellStyleMap.put(ExcelCellStyle.NUMERIC, numberCellStyle);

        return cellStyleMap;
    }

    protected void setUpWorkSheets(
            Sheet sheet, List<String> fields, Map<ExcelCellStyle, XSSFCellStyle> cellStyleMap) {
        var row = sheet.createRow(0);
        int len = fields.size();
        for (int i = 0; i < len; i++) {
            var genericCell = row.createCell(i);
            genericCell.setCellValue(fields.get(i));
            genericCell.setCellStyle(cellStyleMap.get(ExcelCellStyle.HEADER));
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 10 * 256);
        }
    }

    protected void setUpWorkSheetsConstraint(Sheet sheet, List<T> modelData) {
    }

    protected void processWorkSheet(
            Sheet sheet, List<T> entries, Map<ExcelCellStyle, XSSFCellStyle> cellStyleMap) {
        int rowIndex = 0;
        for (T entry : entries) {
            var row = sheet.createRow(++rowIndex);
            var elements = getColumnElements(rowIndex, entry);
            writeRecord(row, elements, cellStyleMap);
        }
    }

    public List<List<String>> readXlsxData(InputStream fileInputStream) throws IOException {
        if (fileInputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        InputStream sheet = null;
        OPCPackage pkg = null;

        try {
            pkg = OPCPackage.open(fileInputStream);
            var xssfReader = new XSSFReader(pkg);
            var sst = xssfReader.getSharedStringsTable();

            var customXlsxDefaultHandler = new CustomXlsxDefaultHandler(
                    sst,
                    xssfReader.getStylesTable(),
                    this::rowDataValidation
            );

            var factory = SAXParserFactory.newDefaultInstance();
            factory.setNamespaceAware(true); // Add this
            var parser = factory.newSAXParser();
            var xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(customXlsxDefaultHandler);

            // Consider getting first sheet more robustly
            var sheetsIterator = xssfReader.getSheetsData();
            if (!sheetsIterator.hasNext()) {
                throw new IOException("No sheets found in Excel file");
            }
            sheet = sheetsIterator.next();

            var sheetSource = new InputSource(sheet);
            xmlReader.parse(sheetSource);

            return customXlsxDefaultHandler.getData();

        } catch (ParserConfigurationException | SAXException e) {
            logger.error("Error parsing Excel file: " + e.getMessage(), e);
            throw new IOException("Failed to parse Excel file", e);
        } catch (Exception e) {
            logger.error("Unexpected error reading Excel file: " + e.getMessage(), e);
            throw new DoubleJsHouseException(ErrorCode.CANNOT_READ_EXCEL_DATA);
        } finally {
            if (sheet != null) {
                try {
                    sheet.close();
                } catch (IOException e) {
                    logger.warn("Failed to close sheet stream", e);
                }
            }
            if (pkg != null) {
                try {
                    pkg.close();
                } catch (IOException e) {
                    logger.warn("Failed to close OPC package", e);
                }
            }
        }
    }

    protected XSSFCellStyle createCellStyleString(SXSSFWorkbook workbook) {
        var font = (XSSFFont) workbook.createFont();
        font.setBold(false);
        font.setColor(new XSSFColor(Color.BLACK, null));
        font.setFontHeightInPoints((short) 9);

        var style = (XSSFCellStyle) workbook.createCellStyle();
        setCellStyle(font, style);
        return style;
    }

    private void setCellStyle(XSSFFont font, XSSFCellStyle style) {
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
    }

    protected void writeRecord(
            Row row, List<CellInfo> elements, Map<ExcelCellStyle, XSSFCellStyle> cellStyleMap) {
        int len = elements.size();
        for (int i = 0; i < len; i++) {
            var genericCell = row.createCell(i);
            var cellInfo = elements.get(i);
            var dataType = cellInfo.getDataType();
            var data = cellInfo.getValue() == null ? StringUtils.EMPTY : cellInfo.getValue();
            if (dataType == ExcelDataType.DATE) {
                genericCell.setCellStyle(cellStyleMap.get(ExcelCellStyle.DATE));
                genericCell.setCellValue(data);
            } else if (dataType == ExcelDataType.NUMERIC) {
                genericCell.setCellStyle(cellStyleMap.get(ExcelCellStyle.NUMERIC));
                try {
                    genericCell.setCellValue(Double.parseDouble(data));
                } catch (NumberFormatException p) {
                    genericCell.setCellStyle(cellStyleMap.get(ExcelCellStyle.TEXT));
                    genericCell.setCellValue(data);
                }
            } else {
                genericCell.setCellStyle(cellStyleMap.get(ExcelCellStyle.TEXT));
                genericCell.setCellValue(data);
            }
        }
    }

    protected void rowDataValidation(String currentExcelRow, List<List<String>> data) {
    }
}
