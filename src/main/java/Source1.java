import lombok.Getter;
import lombok.Setter;

/**
 * @author winston
 * @date 2020/1/10 9:16
 */
@Getter
@Setter
public class Source1 implements Comparable<Source1> {
    private int showLine;
    private String firstCell;
    private String sheetName;

    // ==>机构范围<18057>太证新化投资控股有限公司
    @Override
    public String toString() {
        return "==>" + sheetName + "<" + showLine + ">" + firstCell;
    }

    @Override
    public int compareTo(Source1 o) {
        return (this.showLine - o.showLine);
    }
}
