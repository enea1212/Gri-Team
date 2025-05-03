package com.example.demo.repository;

import com.example.demo.entity.AnuntDisponibilitate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AnuntDisponibilitateRepository extends JpaRepository<AnuntDisponibilitate, Long> {
    List<AnuntDisponibilitate> findAllByAnuntId(Long anuntId);
    Optional<AnuntDisponibilitate> findByAnuntIdAndIntervalOrar(Long anuntId, LocalTime intervalOrar);
    @Query("SELECT ad FROM AnuntDisponibilitate ad " +
            "LEFT JOIN FETCH ad.reservedBy " +
            "WHERE ad.anunt.id = :anuntId AND ad.disponibil = false")
    List<AnuntDisponibilitate> findAllByAnuntIdAndDisponibilFalse(@Param("anuntId") Long anuntId);

    boolean existsByReservedById(Long userId);
    @Modifying
    @Query("DELETE FROM AnuntDisponibilitate ad WHERE ad.anunt.id = :anuntId")
    void deleteByAnuntId(@Param("anuntId") Long anuntId);
    List<AnuntDisponibilitate> findAllByReservedById(Long userId);
}