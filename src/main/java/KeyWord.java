import lombok.Getter;
import lombok.Setter;

/**
 * @author winston
 * @date 2020/1/10 9:16
 */
@Getter
@Setter
public class KeyWord implements Comparable<KeyWord> {
    private int showLine;
    private String firstCell;
    private String SecondCell;

    @Override
    public String toString() {
        return showLine + ">++++++" + firstCell + "------" + SecondCell;
    }

    @Override
    public int compareTo(KeyWord o) {
        return (this.showLine - o.showLine);
    }
}
