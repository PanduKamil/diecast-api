package com.dudus.diecast_api.repository;

import com.dudus.diecast_api.model.Barang;

// import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BarangRepository extends JpaRepository<Barang, Integer> {
    List<Barang> findByStatusParkir(Boolean statusParkir);
    
    Barang findByNamaBarangIgnoreCase(String namaBarang);

    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    // @Query("SELECT b FROM barang b WHERE b.id = :id")
    // Optional<Barang> findByIdWithLock(@Param("id") Integer id);
    
}
