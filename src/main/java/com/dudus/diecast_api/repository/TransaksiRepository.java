package com.dudus.diecast_api.repository;

import com.dudus.diecast_api.model.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface TransaksiRepository extends JpaRepository<Transaksi, Integer> {
    @Query("SELECT SUM(t.hargaJual) FROM Transaksi t")
    BigDecimal sumTotalOmset();
    
} 
