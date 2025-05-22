package teledon.model;
import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
@Table(name = "charity_cases")
public class CharityCase implements teledon.model.Entity<Integer> {
    private String name;
    private Integer totalSum;
    private Integer id;
    public CharityCase() {
        id = 0;
        name = "default";
        totalSum = 0;
    }

    public CharityCase(String name, Integer totalSum) {
        this.name = name;
        this.totalSum = totalSum;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "total_sum")
    public Integer getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(Integer totalSum) {
        this.totalSum = totalSum;
    }
    @Override
    public void setId(Integer id) {
        this.id=id;
    }
    @Override
    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id_case")
    public Integer getId() {
        return id;
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