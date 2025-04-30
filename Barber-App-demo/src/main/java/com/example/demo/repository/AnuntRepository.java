package com.example.demo.repository;

import com.example.demo.entity.Anunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AnuntRepository extends JpaRepository<Anunt, Long> {
    boolean existsByUserId(Long userId);
    List<Anunt> findByUserId(Long userId);
}