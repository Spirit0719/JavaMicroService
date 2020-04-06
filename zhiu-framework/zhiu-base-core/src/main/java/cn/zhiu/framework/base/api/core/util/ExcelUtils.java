package cn.zhiu.framework.base.api.core.util;

import cn.zhiu.framework.base.api.core.bean.Column;
import cn.zhiu.framework.base.api.core.bean.Table;
import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExcelUtils {


    /**
     * Read excel list.
     *
     * @param excelPath the excel path
     * @return the list
     * @throws IOException            the io exception
     * @throws InvalidFormatException the invalid format exception
     * @author zhuzz
     * @time 2019 /12/18 14:51:33
     */
    public static List<Table> readExcel(String excelPath) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(new File(excelPath));
        List<Table> tableList = Lists.newArrayListWithCapacity(workbook.getNumberOfSheets());
        for (int h = 0; h < workbook.getNumberOfSheets(); h++) {
            Table table = getTable(workbook, h);
            tableList.add(table);
        }
        return tableList;
    }

    /**
     * Read excel at table.
     *
     * @param excelPath the excel path
     * @param index     the index
     * @return the table
     * @throws IOException            the io exception
     * @throws InvalidFormatException the invalid format exception
     * @author zhuzz
     * @time 2019 /12/18 14:54:47
     */
    public static Table readExcelAt(String excelPath, int index) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(new File(excelPath));
        return getTable(workbook, index);
    }


    /**
     * Gets table.
     *
     * @param workbook the workbook
     * @param h        the h
     * @return the table
     * @author zhuzz
     * @time 2019 /12/18 14:55:01
     */
    private static Table getTable(Workbook workbook, int h) {
        Sheet sheet = workbook.getSheetAt(h);
        int rowCount = sheet.getLastRowNum();
        if (rowCount < 1) {
            throw new RuntimeException("excel file row count is 0!");
        }
        int columnCount = sheet.getRow(0).getLastCellNum();
        if (columnCount < 1) {
            throw new RuntimeException("excel file column count is 0!");
        }
        Table table = new Table();
        Row titleRow = sheet.getRow(0);
        for (int i = 0; i < columnCount; i++) {
            table.addColumn(getCellVal(titleRow.getCell(i)) + "");
        }
        int startRowNum = sheet.getFirstRowNum() + 1;
        for (int i = startRowNum; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            if (Objects.isNull(row)) {
                table.addRows(new Object[columnCount]);
                continue;
            }
            List<Object> cellList = Lists.newArrayListWithCapacity(columnCount);
            for (int j = 0; j < row.getFirstCellNum(); j++) {
                cellList.add(null);
            }
            for (int j = 0; j < columnCount; j++) {
                cellList.add(getCellVal(row.getCell(j)));
            }
            table.addRows(cellList);
        }
        return table;
    }

    private static Object getCellVal(Cell cell) {
        try {
            int cellType = cell.getCellType();
            switch (cellType) {
                case 0:
                    return customExcelNumericFormat(cell.getNumericCellValue());
                case 1:
                    return cell.getStringCellValue();
                case 4:
                    return cell.getBooleanCellValue();
            }
        } catch (Exception ex) {
        }
        try {
            String cellValue = cell.getStringCellValue();
            return cellValue;
        } catch (Exception ex) {
        }

        try {
            double numericCellValue = cell.getNumericCellValue();
            return numericCellValue;
        } catch (Exception ex) {
        }

        try {
            boolean booleanCellValue = cell.getBooleanCellValue();
            return booleanCellValue;
        } catch (Exception ex) {
        }

        try {
            Date dateCellValue = cell.getDateCellValue();
            return dateCellValue;
        } catch (Exception ex) {
        }

        return null;
    }


    public static Workbook generateExcelSheet(Table table) {
        Workbook wb = new HSSFWorkbook();  // or new XSSFWorkbook();
        generateExcelSheet(wb, table);
        return wb;
    }

    public static void generateExcelSheet(Workbook wb, Table table) {
        Sheet sheet = wb.createSheet(table.getTableName());
        int rowIndex = 0;

        if (table.getColumns().size() > 0) {
            Row row0 = sheet.createRow((short) rowIndex++);
            int cellIndex = 0;

            for (Column column : table.getColumns()) {
                row0.createCell(cellIndex++).setCellValue(column.getName());
            }
        }

        for (int i = 0; i < table.getRows().size(); i++) {

            Row row = sheet.createRow((short) rowIndex++);
            Object[] objects = table.getRows().get(i);
            int valCellIndex = 0;
            for (Object obj : objects) {
                if (Objects.nonNull(obj)) {
                    row.createCell(valCellIndex++).setCellValue(obj + "");
                } else {
                    row.createCell(valCellIndex++).setCellValue("");
                }
            }
        }

    }

    public static HttpServletResponse writeForDownload(HttpServletResponse httpServletResponse, Workbook wb, String excelName) {


        try {
            excelName = new String(excelName.getBytes("gb2312"), "ISO8859-1");
        } catch (Exception ex) {
        }
        excelName = excelName + ".xls";
        /**
         * 设置相关的头信息
         */
        httpServletResponse.setContentType("application/vnd.ms-excel");
        httpServletResponse.setHeader("Content-disposition", "attachment;filename=" + excelName);

        try (OutputStream ouputStream = httpServletResponse.getOutputStream()) {
            wb.write(ouputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return httpServletResponse;
    }

    private static String customExcelNumericFormat(double d) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        String temp = d + "";
        // 科学计数法中的n（10的n次方）
        int n = 0;
        // 判断有多少位有效小数
        int a = 0;
        // 如果该数字使用了科学计数法
        if (temp.indexOf("E") >= 0) {
            // 判断出要移多少位
            String auxiliaryStr = temp.split("E")[1];
            String realStr = temp.split("E")[0];
            n = Integer.parseInt(auxiliaryStr);
            // 有多少位有效小数（科学计数法）
            a = (realStr).length() - (realStr).indexOf(".") - 1 - n;
        } else {
            // 有多少位有效小数（非科学计数法）
            a = (d + "").length() - (d + "").indexOf(".") - 1;
        }
        if (a == 1 && (d + "").endsWith(".0")) {
            // 如果excel里本无小数是java读取时自动加的.0那就直接将小数位数设0
            nf.setMinimumFractionDigits(0);
        } else {
            // 有小数的按小数位数设置
            nf.setMinimumFractionDigits(a);
        }
        String s = nf.format(d);
        if (s.indexOf(",") >= 0) {
            s = s.replace(",", "");
        }
        return s;
    }
}
