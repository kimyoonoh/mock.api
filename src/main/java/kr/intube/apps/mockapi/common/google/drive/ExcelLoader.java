package kr.intube.apps.mockapi.common.google.drive;

import aidt.gla.common.tools.excel.ExcelMaker;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class ExcelLoader {
    DriveHelper driveHelper;

    ExcelMaker  excelMaker;

    public ExcelLoader(DriveHelper driveHelper) {
        this.driveHelper = driveHelper;
        this.excelMaker  = new ExcelMaker();
    }

    public XSSFWorkbook getWorkBookByName(String fileName) throws IOException {
        return new XSSFWorkbook(this.driveHelper.getFileStreamByName(fileName));
    }

    public XSSFWorkbook getWorkBookById(String fileId) throws IOException {
        return new XSSFWorkbook(getGoolgleDriveFileInputStream(fileId));
    }

    public XSSFWorkbook getWorkBook(String fileName) throws IOException {
        return new XSSFWorkbook(fileName);
    }

    public InputStream getGoolgleDriveFileInputStream(String fileId) throws IOException {
        return this.driveHelper.getFileStreamById(fileId);
    }

    public LinkedHashMap<String, List<LinkedHashMap<String, Object>>> loadGoogleSheetById(String fileId) throws IOException {
        return excelMaker.readExcel(getWorkBookById(fileId));
    }

    public LinkedHashMap<String, List<LinkedHashMap<String, Object>>> loadGoogleSheetByName(String fileName) throws IOException {
        return excelMaker.readExcel(getWorkBookByName(fileName));
    }

    public LinkedHashMap<String, List<LinkedHashMap<String, Object>>> loadExcelFile(String fileName) throws IOException {
        return excelMaker.readExcel(getWorkBook(fileName));
    }
}
