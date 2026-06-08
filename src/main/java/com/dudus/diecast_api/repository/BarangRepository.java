package com.dudus.diecast_api.repository;

import com.dudus.diecast_api.model.Barang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarangRepository extends JpaRepository<Barang, Long> {

    
}
