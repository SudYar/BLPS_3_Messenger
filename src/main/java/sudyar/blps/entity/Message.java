package sudyar.blps.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NonNull
    @Column(name = "toUser")
    private String to;

    @Column(name = "from_user")
    private String from;

    @Column(name = "message")
    private String message;

    @Column(name = "isRead")
    private boolean isRead;

    public void setIsRead(boolean bool){
        this.isRead = bool;
    }

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createdDate;

}
