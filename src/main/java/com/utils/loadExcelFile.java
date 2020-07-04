package com.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jxl.read.biff.BiffException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class loadExcelFile {
    /**
     * read excel
     *
     * @param
     * @return
     */
    public static JSONArray getExcuteJsonArray(String filePath, String sheetName) throws Exception {
        Workbook wb = null;
        Sheet sheet = null;
        Row row = null;
        JSONArray jsonArray = new JSONArray();

        String cellData = null;
        wb = readExcel(getPath(filePath));
        if (wb != null) {
            //用来存放表中数据
            List<String> keyData = new ArrayList<>();

            //获取sheet
            //sheet = wb.getSheetAt(0);//按索引获取
            sheet = wb.getSheet(sheetName);//按sheet名称获取

            int rownum = sheet.getPhysicalNumberOfRows();//获取数据行数
            row = sheet.getRow(0);
            int colnum = row.getPhysicalNumberOfCells();//获取数据列数

            //判断数据文件是否有效
            if (rownum <=1 ) return jsonArray;

            //读取并装载数据
            for (int i = 0; i < rownum; i++) {
                //获取excel第一行的key
                if ( i == 0)
                {
                    for (int j = 0; j < colnum; j++)
                    {
                        cellData = (String) getCellFormatValue(row.getCell(j));
                        //cellData = row.getCell(j).toString();
                        keyData.add(cellData);
                    }
                    continue;
                }

                //将key和value拼接一起放到jsonObject中
                row = sheet.getRow(i);
                if (row != null) {
                    JSONObject jsonObject = new JSONObject();
                    for (int j = 0; j < colnum; j++) {
                        cellData =  getCellFormatValue(row.getCell(j)).toString();
                        //cellData = row.getCell(j).toString();
                        jsonObject.put(keyData.get(j), cellData);
                    }
                    jsonArray.add(jsonObject);

                } else {
                    break;
                }

            }
        }
        return jsonArray;
    }

    /**
     * read excel
     *
     * @param
     * @return
     */
    public static List<Map<String, String>> getExcuteListMap(String filePath,String sheetName) throws Exception {
        Workbook wb = null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String, String>> list = null;
        String cellData = null;
        wb = readExcel(getPath(filePath));
        if (wb != null) {
            //用来存放表中数据
            list = new ArrayList<Map<String, String>>();
            List<String> keyData = new ArrayList<>();

            //获取sheet
            //sheet = wb.getSheetAt(0);//按索引获取
            sheet = wb.getSheet(sheetName);//按sheet名称获取
            System.out.println("sheet"+sheet);
            int rownum = sheet.getPhysicalNumberOfRows();//获取数据行数
            row = sheet.getRow(0);
            int colnum = row.getPhysicalNumberOfCells();//获取数据列数

            //判断数据文件是否有效
            if (rownum <=1 ) return list;;

            //读取并装载数据
            for (int i = 0; i < rownum; i++) {
                //获取excel第一行的key
                if ( i == 0)
                {
                    for (int j = 0; j < colnum; j++)
                    {
                        cellData = (String) getCellFormatValue(row.getCell(j));
                        //cellData = row.getCell(j).toString();
                        keyData.add(cellData);
                    }
                    continue;
                }

                //将key和value拼接一起放到list中
                Map<String, String> map = new LinkedHashMap<String, String>();
                row = sheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < colnum; j++) {
                            cellData =  getCellFormatValue(row.getCell(j)).toString();
                            //cellData = row.getCell(j).toString();
                            map.put(keyData.get(j), cellData);
                    }
                    list.add(map);
                } else {
                    break;
                }

            }
        }
        return list;
    }

    /**
     * 判断excel文件的类型
     *
     * @param filePath
     * @return
     */
    public static Workbook readExcel(String filePath) throws BiffException, IOException{
        Workbook wb = null;
        if (filePath == null) {
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if (".xls".equals(extString)) {
                return wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                return wb = new XSSFWorkbook(is);
            } else {
                return wb = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    /**
     * 获得excel文件的路径
     * @return
     * @throws IOException
     */
    public static String getPath(String filePath) throws IOException {
        File directory = new File(".");
        String sourceFile = directory.getCanonicalPath() + "\\src\\main\\resources"
                + filePath;
        return sourceFile;
    }

    public static Object getCellFormatValue(Cell cell) {
        Object cellValue = null;
        if (cell != null) {

            //判断cell类型
            switch (cell.getCellType()) {
                case NUMERIC: {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
                case FORMULA: {
                    //判断cell是否为日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        //转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    } else {
                        //数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case STRING: {
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }
}
