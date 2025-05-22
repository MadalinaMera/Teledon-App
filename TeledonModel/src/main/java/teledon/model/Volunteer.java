package teledon.model;
import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
@Table(name = "volunteers")
public class Volunteer implements teledon.model.Entity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_volunteer")
    private Integer id;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    private String username;
    private String password;
    public Volunteer() {
        id = 0;
        username = password = "default";
    }

    public Volunteer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Column(name = "username")
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Column(name = "password")
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }


    @Override
    public String toString() {
        return username;
    }
}