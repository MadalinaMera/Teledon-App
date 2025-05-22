package teledon.model;
import jakarta.persistence.MappedSuperclass;


public interface Entity<ID> {
    ID getId();
    void setId(ID id);
}