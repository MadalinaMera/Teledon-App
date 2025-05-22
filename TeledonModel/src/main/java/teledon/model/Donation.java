package teledon.model;
import java.util.Objects;

public class Donation implements Entity<Integer>{
    private Donor donor;
    private CharityCase charityCase;
    private int sum;
    private Integer id;
    public Donation(Donor donor, CharityCase charityCase, int sum) {
        this.donor = donor;
        this.charityCase = charityCase;
        this.sum = sum;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public CharityCase getCharityCase() {
        return charityCase;
    }

    public void setCharityCase(CharityCase charityCase) {
        this.charityCase = charityCase;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Donation donation = (Donation) o;
        return sum == donation.sum && Objects.equals(donor, donation.donor) && Objects.equals(charityCase, donation.charityCase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(donor, charityCase, sum);
    }

    @Override
    public String toString() {
        return "Donation{" +
                "donor=" + donor +
                ", charityCase=" + charityCase +
                ", sum=" + sum +
                '}';
    }
}
