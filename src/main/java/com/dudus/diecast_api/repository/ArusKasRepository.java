package com.dudus.diecast_api.repository;

import com.dudus.diecast_api.model.ArusKas;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArusKasRepository extends JpaRepository<ArusKas, Integer> {
    @Query("SELECT SUM(a.jumlah) FROM ArusKas a WHERE a.tipeKas = :tipeKas AND a.dompet = :dompet")
    BigDecimal sumByTipeAndDompet(@Param("tipeKas") String tipeKas, @Param("dompet")String dompet);    
}
