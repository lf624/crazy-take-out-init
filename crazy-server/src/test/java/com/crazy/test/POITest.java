package com.crazy.test;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class POITest {

    // 使用 POI 向 Excel 文件写入内容
    @Test
    public void write() throws Exception{
        // 在内存中创建一个 excel 文件对象
        XSSFWorkbook excel = new XSSFWorkbook();
        // 创建 Sheet 页
        XSSFSheet sheet = excel.createSheet("crazy");

        // 在Sheet页中创建行，0表示第1行
        XSSFRow row1 = sheet.createRow(0);
        // 创建单元格并在单元格中设置值，单元格编号也是从0开始，1表示第2个单元格
        row1.createCell(1).setCellValue("姓名");
        row1.createCell(2).setCellValue("城市");

        XSSFRow row2 = sheet.createRow(1);
        row2.createCell(1).setCellValue("张三");
        row2.createCell(2).setCellValue("北京");

        XSSFRow row3 = sheet.createRow(2);
        row3.createCell(1).setCellValue("李四");
        row3.createCell(2).setCellValue("上海");

        FileOutputStream output = new FileOutputStream("/Users/liufei/others/crazy.xlsx");
        // 通过输出流将内存中的Excel文件写入到磁盘上
        excel.write(output);

        // 关闭资源
        output.flush();
        output.close();
        excel.close();
    }

    // 使用 POI 读取 Excel 文件
    @Test
    public void read() throws Exception {
        FileInputStream input = new FileInputStream("/Users/liufei/others/crazy.xlsx");
        // 通过输入流读取指定的Excel文件
        XSSFWorkbook excel = new XSSFWorkbook(input);
        // 获取 Excel 文件的第1个Sheet页
        XSSFSheet sheet1 = excel.getSheetAt(0);

        // 获取Sheet页中的最后一行的行号
        int lastRowNum = sheet1.getLastRowNum();
        for(int i = 0; i <= lastRowNum; i++) {
            // 获取Sheet页中的行
            XSSFRow row = sheet1.getRow(i);
            // 获取行的第2个单元格
            XSSFCell cell1 = row.getCell(1);
            // 获取单元格的文本内容
            String value1 = cell1.getStringCellValue();
            // 获取行的第3个单元格
            XSSFCell cell2 = row.getCell(2);
            String value2 = cell2.getStringCellValue();

            System.out.println(value1 + " " + value2);
        }

        input.close();
        excel.close();
    }
}
