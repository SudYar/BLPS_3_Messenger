package sudyar.blps.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sudyar.blps.entity.Message;

import java.sql.Timestamp;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByToOrderByCreatedDate(String toUser);
    List<Message> findByToAndCreatedDateIsLessThan(String toUser, Timestamp createdDate);
    List<Message> findByFrom(String fromUser);

}
