package org.soenke.sobott;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelUtil {

    private static final Logger LOGGER = Logger.getLogger(ExcelUtil.class.getName());

    public static Sheet generateValueCopiedSheetFromWeeklyArticlesExcel(Workbook workbook) {
        Sheet sourceSheet = workbook.getSheetAt(1);

        try (Workbook targetWorkbook = new XSSFWorkbook()) {
            Sheet targetSheet = targetWorkbook.createSheet("Copy of sourceSheet");
            for (Row sourceRow : sourceSheet) {
                Row targetRow = targetSheet.createRow(sourceRow.getRowNum());
                int columns = 23;
                for (int i = 0; i <= columns; i++) {
                    Cell sourceCell = sourceRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell targetCell = targetRow.createCell(i);
                    if (sourceCell == null) {
                        targetCell.setCellType(CellType.BLANK);
                    } else {
                        targetCell.setCellValue(getStringValueOfCell(sourceCell));
                    }
                }
            }
            return targetSheet;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, Map<String, String>> getDataFromWeeklyArticlesExcel(Sheet sheet) {
        return getDataFromExcelUrlGeneric(sheet, getColumnNamesForWeeklyArticles, 3, 13);
    }

    protected static Map<Integer, Map<String, String>> getDataFromExcelUrlGeneric(Sheet sheet,
                                                                                  Function<Sheet, List<String>> getColumnNames,
                                                                                  Integer rowsToRemove, Integer amountColumns) {
        Map<Integer, Map<String, String>> data = new HashMap<>();
        List<String> columnNames = getColumnNames.apply(sheet);
        for (int i = 0; i < rowsToRemove; i++) {
            sheet.removeRow(sheet.getRow(i));
        }
        int rowIndex = 0;
        for (Row row : sheet) {
            Map<String, String> columnValuesWithNames = new HashMap<>();
            for (int i = 0; i < amountColumns; i++) {
                Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (cell == null) {
                    columnValuesWithNames.put(columnNames.get(i), null);
                } else {
                    columnValuesWithNames.put(columnNames.get(i), getStringValueOfCell(cell));
                }
            }
            data.put(rowIndex, columnValuesWithNames);
            rowIndex++;
        }
        return data;
    }

    protected static Function<Sheet, List<String>> getColumnNamesForWeeklyArticles = sheet -> {
        List<String> rowList = new ArrayList<>();
        Row row = sheet.getRow(2);
        for (int i = 0; i < 13; i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null) {
                LOGGER.log(Level.SEVERE, "Cell is null in getArticlesColumnNamesTwo");
            } else {
                rowList.add(getStringValueOfCell(cell));
            }
        }
        return rowList;
    };

    protected static String getStringValueOfCell(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getRichStringCellValue().getString().trim();
            case NUMERIC:
                return getNumericValueAsString(cell);
            case BOOLEAN:
                return cell.getBooleanCellValue() + "".trim();
            case FORMULA:
                return getFormulaValueAsString(cell);
            default:
                return null;
        }
    }

    protected static String getFormulaValueAsString(Cell cell) {
        switch (cell.getCachedFormulaResultType()) {
            case NUMERIC:
                return getNumericValueAsString(cell).trim();
            case STRING:
                return cell.getRichStringCellValue().getString().trim();
            default:
                return null;
        }
    }

    protected static String getNumericValueAsString(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue() + "".trim();
        } else {
            Double doubleValue = cell.getNumericCellValue();
            String[] splits = String.valueOf(doubleValue).split("\\.");
            if (splits.length > 1 && splits[1].equals("0")) {
                return splits[0].trim();
            }
            return doubleValue + "".trim();
        }
    }

}
