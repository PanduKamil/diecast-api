package com.dudus.diecast_api.service;

import com.dudus.diecast_api.model.ArusKas;
import com.dudus.diecast_api.repository.ArusKasRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ArusKasService {
    private final ArusKasRepository repository;
    
    public ArusKasService(ArusKasRepository repository){
        this.repository = repository;
    }

    public List<ArusKas> getAll(){return repository.findAll();}
    public Optional<ArusKas> getById(Integer id){return repository.findById(id);}
    public ArusKas save(ArusKas aruskas){return repository.save(aruskas);}
    public void delete(Integer id){repository.deleteById(id);}

    // Helper dari TransaksiService
    public void catatKas(String tipeKas, String dompet, BigDecimal jumlah, String keterangan){
        ArusKas kas = new ArusKas();
        kas.setTipeKas(tipeKas);
        kas.setDompet(dompet);
        kas.setJumlah(jumlah);
        kas.setKeterangan(keterangan);
        repository.save(kas);
    }
}
