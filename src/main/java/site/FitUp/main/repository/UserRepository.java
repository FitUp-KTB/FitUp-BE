package site.FitUp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.FitUp.main.model.User;

public interface UserRepository extends JpaRepository<User,String> {
}
