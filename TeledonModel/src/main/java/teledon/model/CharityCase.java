package teledon.model;

import java.util.Objects;

public class CharityCase extends Entity<Integer> {
    private String name;
    private Integer totalSum;

    public CharityCase(String name, Integer totalSum) {
        this.name = name;
        this.totalSum = totalSum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(Integer totalSum) {
        this.totalSum = totalSum;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CharityCase that = (CharityCase) o;
        return Objects.equals(name, that.name) && Objects.equals(totalSum, that.totalSum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, totalSum);
    }

    @Override
    public String toString() {
        return "teledon.model.CharityCase{" +
                "name='" + name + '\'' +
                ", totalSum=" + totalSum +
                '}';
    }
}
