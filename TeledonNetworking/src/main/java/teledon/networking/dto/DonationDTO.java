package teledon.networking.dto;

public class DonationDTO {
    String name;
    String number;
    String address;
    Integer sum;
    Integer idCharityCase;

    public DonationDTO(String name, String number, String address, Integer sum, Integer idCharityCase) {
        this.name = name;
        this.number = number;
        this.address = address;
        this.sum = sum;
        this.idCharityCase = idCharityCase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public Integer getIdCharityCase() {
        return idCharityCase;
    }

    public void setIdCharityCase(Integer idCharityCase) {
        this.idCharityCase = idCharityCase;
    }
}
