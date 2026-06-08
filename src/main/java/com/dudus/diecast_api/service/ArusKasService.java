package com.dudus.diecast_api.service;

import com.dudus.diecast_api.model.ArusKas;
import com.dudus.diecast_api.repository.ArusKasRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArusKasService {
    private final ArusKasRepository repository;
    
    public ArusKasService(ArusKasRepository repository){
        this.repository = repository;
    }

    public List<ArusKas> getAll(){return repository.findAll();}

    public Optional<ArusKas> getById(Long id){return repository.findById(id);}

    public ArusKas save(ArusKas aruskas){return repository.save(aruskas);}

    public void delete(Long id){repository.deleteById(id);}
}
