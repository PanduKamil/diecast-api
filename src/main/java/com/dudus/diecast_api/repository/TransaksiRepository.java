package com.dudus.diecast_api.repository;

import com.dudus.diecast_api.model.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

@Repository
public interface TransaksiRepository extends JpaRepository<Transaksi, Integer> {
    @Query("SELECT SUM(t.hargaJual) FROM Transaksi t")
    BigDecimal sumTotalOmset();

        // Query semua periode
    @Query("SELECT " +
        "SUM(t.hargaJual) as totalOmset, " +
        "SUM(t.komisiReseller) as totalKomisi, " +
        "SUM(t.netProfitOwner) as totalBersih, " +
        "SUM(t.hargaModalSnapshot * t.jumlah) as totalModal " +
        "FROM Transaksi t")
    Object[] getLaporanSemuaPeriode();

    // Query filter bulan & tahun
    @Query("SELECT " +
        "SUM(t.hargaJual) as totalOmset, " +
        "SUM(t.komisiReseller) as totalKomisi, " +
        "SUM(t.netProfitOwner) as totalBersih, " +
        "SUM(t.hargaModalSnapshot * t.jumlah) as totalModal " +
        "FROM Transaksi t " +
        "WHERE EXTRACT(MONTH FROM t.tanggalJual) = :bulan " +
        "AND EXTRACT(YEAR FROM t.tanggalJual) = :tahun")
    Object[] getLaporanBulanan(@Param("bulan") int bulan, @Param("tahun") int tahun);
    
} 
