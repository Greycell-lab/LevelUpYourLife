package de.domesoft.levelupapi.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT password_hash FROM user WHERE user_name = ?1", nativeQuery = true)
    String getPasswordHash(String name);
}
