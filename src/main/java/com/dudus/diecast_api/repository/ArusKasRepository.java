package com.dudus.diecast_api.repository;

import com.dudus.diecast_api.model.ArusKas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArusKasRepository extends JpaRepository<ArusKas, Integer> {
    
}
