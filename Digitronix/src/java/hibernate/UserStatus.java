package hibernate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_status")
public class UserStatus implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "status_name", length = 50, nullable = false)
    private String status_name;

    public UserStatus() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }
}
