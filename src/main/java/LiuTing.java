import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author winst
 * @date 2020/1/9 0:18
 */
public class LiuTing {
    /**
     * @param excelPath
     */
    public static List<String> readFromExcel(String excelPath, int keysheet, int sourceSheet) {
        List<String> targetList = new ArrayList<>();
        try {
            //String encoding = "GBK";
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在
                String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
                Workbook wb;
                //根据文件后缀（xls/xlsx）进行判断
                if ("xls".equals(split[1])) {
                    FileInputStream fis = new FileInputStream(excel);   //文件流对象
                    wb = new HSSFWorkbook(fis);
                } else if ("xlsx".equals(split[1])) {
                    wb = new XSSFWorkbook(excel);
                } else {
                    System.out.println("文件类型错误!");
                    return null;
                }
                // 要筛查的数据源
                Sheet sheetSource = wb.getSheetAt(sourceSheet);
                List<String> source = new ArrayList<>();
                for (int rIndex = sheetSource.getFirstRowNum(); rIndex <= sheetSource.getLastRowNum(); rIndex++) {
                    Row row = sheetSource.getRow(rIndex);
                    if (row != null) {
                        Cell cell = row.getCell(0);
                        if (cell != null) {
                            source.add(cell.toString());
                        }
                    }
                }
                //要查询的工作簿
                Sheet sheet = wb.getSheetAt(keysheet);
                int firstRowIndex = sheet.getFirstRowNum();   //第一行是列名，所以不读,我还是得读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: " + firstRowIndex);
                System.out.println("lastRowIndex: " + lastRowIndex);
                for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                    Row row = sheet.getRow(rIndex);
                    if (row != null) {
//                        int firstCellIndex = row.getFirstCellNum();
//                        int lastCellIndex = row.getLastCellNum();
//                        for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
//                            Cell cell = row.getCell(cIndex);
//                            if (cell != null) {
//                                System.out.println(cell.toString());
//                            }
//                        }
                        Cell cell1 = row.getCell(0);
                        Cell cell2 = row.getCell(1);
                        if (cell1 != null && cell2 != null) {
                            String strCell1 = cell1.toString();
                            String strCell2 = cell2.toString();
                            String keyword = rIndex + 1 + ">++++++" + strCell1 + "------" + strCell2;
                            targetList.add(keyword + "\n\t");
                            if (StringUtils.isNotBlank(strCell2)) {
                                List<String> matchList = source.stream().filter(str -> str.toUpperCase().contains(strCell2.toUpperCase()))
                                        .collect(Collectors.toList());
                                matchList.stream().forEach(s -> s.concat("\t"));
                                targetList.addAll(matchList);
                            }
                        }
                    }
                }
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetList;
    }

    /**
     * 关键词有两列
     * 范围有两列
     * @param excelPath
     * @param keysheet
     * @param sourceSheet
     * @return
     */
    public static List<String> range2Column(String excelPath, int keysheet, int sourceSheet, boolean difSource) {
        List<String> targetList = new ArrayList<>();
        try {
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {
                String[] split = excel.getName().split("\\.");
                Workbook wb;
                if ("xls".equals(split[1])) {
                    FileInputStream fis = new FileInputStream(excel);
                    wb = new HSSFWorkbook(fis);
                } else if ("xlsx".equals(split[1])) {
                    wb = new XSSFWorkbook(excel);
                } else {
                    System.out.println("文件类型错误!");
                    return null;
                }
                /*获取数据源*/
                Sheet sheetSource = wb.getSheetAt(sourceSheet);
                Collection<Source2> source;
                if (difSource) {
                    source = new TreeSet<>(); // 数据源去重
                } else {
                    source = new ArrayList<>(); // 数据源不去重
                }
                for (int rIndex = sheetSource.getFirstRowNum(); rIndex <= sheetSource.getLastRowNum(); rIndex++) {
                    Row row = sheetSource.getRow(rIndex);
                    int showIndex = rIndex + 1;
                    if (row != null) {
                        Cell sourCell1 = row.getCell(0);
                        Cell sourCell2 = row.getCell(1);
                        if (sourCell1 != null && sourCell2 != null) {
                            Source2 sour = new Source2();
                            sour.setShowLine(showIndex);
                            sour.setFirstCell(sourCell1.toString());
                            sour.setSecondCell(sourCell2.toString());
                            source.add(sour);
                        }
                    }
                }
                // 去重
                Map<String, Source2> stringSourceMap = source.stream().collect(Collectors.toMap(
                        Source2::getSecondCell, data -> data, (v1, v2) -> v2));
                // 排序并转字符串
                List<Source2> source2s = stringSourceMap.values().stream().sorted(
                        Comparator.comparing(Source2::getShowLine)).collect(Collectors.toList());
                /*获取关键词*/
                Sheet sheet = wb.getSheetAt(keysheet);
                int firstRowIndex = sheet.getFirstRowNum();
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: " + firstRowIndex);
                System.out.println("lastRowIndex: " + lastRowIndex);
                Set<KeyWord> keywords = new TreeSet<>();
                for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {
                    Row row = sheet.getRow(rIndex);
                    if (row != null) {
                        Cell cell1 = row.getCell(0);
                        Cell cell2 = row.getCell(1);
                        if (cell1 != null && cell2 != null) {
                            KeyWord keyWord = new KeyWord();
                            keyWord.setShowLine(rIndex + 1);
                            keyWord.setFirstCell(cell1.toString());
                            keyWord.setSecondCell(cell2.toString());
                            keywords.add(keyWord);
                        }
                    }
                }
                Map<String, KeyWord> stringKeyWordMap = keywords.stream().collect(Collectors.toMap(
                        keyword -> keyword.getFirstCell() + keyword.getSecondCell(), data -> data, (v1, v2) -> v2
                ));
                List<KeyWord> sortedKeyWords = stringKeyWordMap.values().stream().sorted(
                        Comparator.comparing(KeyWord::getShowLine)).collect(Collectors.toList());
                /*进行筛选*/
                sortedKeyWords.forEach(
                        keyWord -> {
                            String secondCell = keyWord.getSecondCell();
                            if (StringUtils.isNotBlank(secondCell)) {
                                List<String> matchList = source2s.stream().filter(source2 -> source2.getSecondCell().toUpperCase().contains(secondCell.toUpperCase())).map(Source2::toString)
                                        .collect(Collectors.toList());
                                if (CollectionUtils.isNotEmpty(matchList)) {
                                    targetList.add(keyWord.toString());
                                    targetList.addAll(matchList);
                                }
                            }
                        }
                );
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetList;
    }

    /**
     * 关键词去重
     * 数据源去重+数据源行号+数据源sheet名
     * @param excelPath
     * @param keysheet
     * @param sourceSheet
     * @return
     */
    public static List<String> readFromExcelDistinct(String excelPath, int keysheet, int sourceSheet, boolean difSource) {
        List<String> targetList = new ArrayList<>();
        try {
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {
                String[] split = excel.getName().split("\\.");
                Workbook wb;
                if ("xls".equals(split[1])) {
                    FileInputStream fis = new FileInputStream(excel);
                    wb = new HSSFWorkbook(fis);
                } else if ("xlsx".equals(split[1])) {
                    wb = new XSSFWorkbook(excel);
                } else {
                    System.out.println("文件类型错误!");
                    return null;
                }
                /*获取数据源*/
                Sheet sheetSource = wb.getSheetAt(sourceSheet);
                Collection<Source1> source;
                if (difSource) {
                    source = new TreeSet<>(); // 数据源去重
                } else {
                    source = new ArrayList<>(); // 数据源不去重
                }
                String sheetName = sheetSource.getSheetName();
                for (int rIndex = sheetSource.getFirstRowNum(); rIndex <= sheetSource.getLastRowNum(); rIndex++) {
                    Row row = sheetSource.getRow(rIndex);
                    int showIndex = rIndex + 1;
                    if (row != null) {
                        Cell cell = row.getCell(0);
                        if (cell != null) {
                            Source1 sour = new Source1();
                            sour.setSheetName(sheetName);
                            sour.setShowLine(showIndex);
                            sour.setFirstCell(cell.toString());
                            source.add(sour);
                        }
                    }
                }
                // 去重
                Map<String, Source1> stringSourceMap = source.stream().collect(Collectors.toMap(
                        Source1::getFirstCell, data -> data, (v1, v2) -> v2));
                // 排序并转字符串
                List<String> sources = stringSourceMap.values().stream().sorted(
                        Comparator.comparing(Source1::getShowLine))
                        .map(source1 -> source1.toString()).collect(Collectors.toList());
                /*获取关键词*/
                Sheet sheet = wb.getSheetAt(keysheet);
                int firstRowIndex = sheet.getFirstRowNum();
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: " + firstRowIndex);
                System.out.println("lastRowIndex: " + lastRowIndex);
                Set<KeyWord> keywords = new TreeSet<>();
                for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {
                    Row row = sheet.getRow(rIndex);
                    if (row != null) {
                        Cell cell1 = row.getCell(0);
                        Cell cell2 = row.getCell(1);
                        if (cell1 != null && cell2 != null) {
                            KeyWord keyWord = new KeyWord();
                            keyWord.setShowLine(rIndex + 1);
                            keyWord.setFirstCell(cell1.toString());
                            keyWord.setSecondCell(cell2.toString());
                            keywords.add(keyWord);
                        }
                    }
                }
                Map<String, KeyWord> stringKeyWordMap = keywords.stream().collect(Collectors.toMap(
                        keyword -> keyword.getFirstCell() + keyword.getSecondCell(), data -> data, (v1, v2) -> v2
                ));
                List<KeyWord> sortedKeyWords = stringKeyWordMap.values().stream().sorted(
                        Comparator.comparing(KeyWord::getShowLine)).collect(Collectors.toList());
                /*进行筛选*/
                sortedKeyWords.forEach(
                        keyWord -> {
                            String secondCell = keyWord.getSecondCell();
                            if (StringUtils.isNotBlank(secondCell)) {
                                List<String> matchList = sources.stream().filter(str -> str.toUpperCase().contains(secondCell.toUpperCase()))
                                        .collect(Collectors.toList());
                                if (CollectionUtils.isNotEmpty(matchList)) {
                                    targetList.add(keyWord.toString());
                                    targetList.addAll(matchList);
                                }
                            }
                        }
                );
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetList;
    }
}