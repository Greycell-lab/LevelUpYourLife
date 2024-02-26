package de.domesoft.levelupapi.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT password_hash FROM user WHERE user_name = ?1", nativeQuery = true)
    String getPasswordHash(String name);
    @Query(value = "SELECT * FROM user WHERE user_name = ?1", nativeQuery = true)
    User getUserByName(String name);
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM user WHERE user_name = ?1 AND password = ?2", nativeQuery = true)
    int loginPassed(String name, String password);
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM user WHERE user_name = ?1", nativeQuery = true)
    int userExists(String name);
}
