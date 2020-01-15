import lombok.Getter;
import lombok.Setter;

/**
 * @author winston
 * @date 2020/1/10 9:16
 */
@Getter
@Setter
public class Source2 implements Comparable<Source2> {
    private int showLine;
    private String firstCell;
    private String secondCell;
    private String sheetName;

    // ==>机构范围<18057>太证新化投资控股有限公司
    @Override
    public String toString() {
        return "==>" + firstCell + ">>>>" + secondCell;
    }

    @Override
    public int compareTo(Source2 o) {
        return (this.showLine - o.showLine);
    }
}
