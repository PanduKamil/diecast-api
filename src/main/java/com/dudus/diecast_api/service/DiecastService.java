package com.dudus.diecast_api.service;

import com.dudus.diecast_api.model.Barang;
import com.dudus.diecast_api.repository.DiecastRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiecastService {
    private final DiecastRepository repository;

    public DiecastService(DiecastRepository repository) {
        this.repository = repository;
    }

    public List<Barang> getAll(){
        return repository.findAll();
    }

    public Optional<Barang> getById(Long id)  {
        return repository.findById(id);
    }

    public Barang save(Barang diecast) {
        return repository.save(diecast);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Barang update(Long id, Barang barangBaru){
        return repository.findById(id)
                .map(barangLama ->{
                    barangLama.setNamaBarang(barangBaru.getNamaBarang());
                    barangLama.setHargaModalAvg(barangBaru.getHargaModalAvg());
                    barangLama.setHargaJualPerkiraan(barangBaru.getHargaJualPerkiraan());
                    barangLama.setStok(barangBaru.getStok());
                    barangLama.setStatusParkir(barangBaru.getStatusParkir());
                    return repository.save(barangLama);
                })
            .orElseThrow(() -> new RuntimeException("Barang tidak ditemukan" + id));
                
    }
}
