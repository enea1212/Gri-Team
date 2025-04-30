package com.example.demo.repository;

import com.example.demo.entity.Anunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnuntRepository extends JpaRepository<Anunt, Long> {
    boolean existsByUserId(Long userId);
    List<Anunt> findByUserId(Long userId);

    @Query("SELECT a FROM Anunt a LEFT JOIN FETCH a.user WHERE a.user.id <> :userId")
    List<Anunt> findAllByUserIdNot(@Param("userId") Long userId);
}