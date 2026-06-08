package com.dudus.diecast_api.service;

import com.dudus.diecast_api.model.Transaksi;
import com.dudus.diecast_api.repository.TransaksiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransaksiService {
    private final TransaksiRepository repository;

    public TransaksiService(TransaksiRepository repository){
        this.repository = repository;
    }

    public List<Transaksi> getAll(){return repository.findAll();}

    public Optional<Transaksi> getById(Long id){return repository.findById(id);}

    public Transaksi save(Transaksi transaksi){return repository.save(transaksi);}

    public void delete(Long id){repository.deleteById(id);}

}
