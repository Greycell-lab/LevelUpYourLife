package de.domesoft.levelupapi.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LevelRepository extends JpaRepository<Level, Long> {
    @Query(value = "SELECT * FROM level WHERE user_id = (SELECT id FROM user WHERE user_name = ?1)", nativeQuery = true)
    Level getLevel(String name);

}
