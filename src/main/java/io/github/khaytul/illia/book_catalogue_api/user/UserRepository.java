package io.github.khaytul.illia.book_catalogue_api.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
    
}
