package de.domesoft.levelupapi.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM parent WHERE user_name = ?1", nativeQuery = true)
    int parentExists(String name);
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM parent WHERE user_name = ?1 AND password = ?2", nativeQuery = true)
    int loginPassed(String name, String password);
    @Query(value = "SELECT * FROM parent WHERE user_name = ?1", nativeQuery = true)
    Parent getParentFromName(String name);
    @Query(value = "SELECT task_list FROM parent WHERE user_name = ?1", nativeQuery = true)
    String getParentTaskList(String name);
}
