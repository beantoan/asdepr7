package it.unical.asde.pr7.repository;

import it.unical.asde.pr7.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username IN ?1")
    List<User> findAllByUsernames(List<String> usernames);
}
