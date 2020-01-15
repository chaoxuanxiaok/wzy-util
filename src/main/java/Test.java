import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author winst
 * @date 2020/1/9 0:08
 */
public class Test {
    public static void main(String[] args) {
        // 范围单列版本
        List<String> list1 = LiuTing.readFromExcelDistinct("C:\\Users\\winston\\Desktop\\中简科技源数据.xlsx", 0, 2, true);
        print(new File("C:\\Users\\winston\\Desktop\\机构关键词1.txt"), list1);
        // 范围双列版本
        List<String> list2 = LiuTing.range2Column("C:\\Users\\winston\\Desktop\\双列中简科技源数据.xlsx", 0, 2, true);
        print(new File("C:\\Users\\winston\\Desktop\\双列机构关键词1.txt"), list2);
    }


    public static void print(File file, List<String> list) {
        FileUtil.writeFile(file,
                bufferedWriter -> {
                    for (String s : list) {
                        try {
                            bufferedWriter.write(s);
                            bufferedWriter.flush();
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 数据源去重
     */
    public static void testDis() {
        try {
            File excel = new File("C:\\Users\\winston\\Desktop\\中简科技源数据.xlsx");
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
                    return;
                }
                /*获取数据源*/
                Sheet sheetSource = wb.getSheetAt(2);
                Set<Source1> source = new TreeSet<>(); // 数据源去重
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
                List<String> sources = source.stream().map(sour -> sour.toString()).collect(Collectors.toList());
                print(new File("C:\\Users\\winston\\Desktop\\测试去重.txt"), sources);
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}