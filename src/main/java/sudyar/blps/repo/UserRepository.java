package sudyar.blps.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sudyar.blps.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByLogin(String login);
    boolean existsByLogin(String login);
    //void save(User user);
}