package com.dudus.diecast_api.service;

import com.dudus.diecast_api.model.Barang;
import com.dudus.diecast_api.repository.BarangRepository;
import com.dudus.diecast_api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class BarangService {
    private final BarangRepository repository;
    private final ArusKasService arusKasService;

    public BarangService(BarangRepository repository, ArusKasService arusKasService) {
        this.repository = repository;
        this.arusKasService = arusKasService;
    }

    public List<Barang> getAll(){
        return repository.findAll();
    }

    public Optional<Barang> getById(Integer id)  {
        return repository.findById(id);
    }

    public Barang getByIdOrThrow(Integer id){
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barang tidak ditemukan" + id));
    }

    @Transactional
    public Barang save(Barang barangBaru) {
        Barang existing = repository.findByNamaBarangIgnoreCase(barangBaru.getNamaBarang());

        BigDecimal totalPengeluaran = barangBaru.getHargaModalAvg().multiply(new BigDecimal(barangBaru.getStok()));

        if (existing != null) {
            int stokLama = existing.getStok();
            int stokBaru = barangBaru.getStok();
            int totalStok = stokLama + stokBaru;

            BigDecimal modalBaru = (existing.getHargaModalAvg()
                                .multiply(new BigDecimal(stokLama))
                                .add(barangBaru.getHargaModalAvg()
                                .multiply(new BigDecimal(stokBaru))))
                                .divide(new BigDecimal(totalStok), 5, RoundingMode.HALF_UP);
            
            existing.setHargaModalAvg(modalBaru);
            existing.setStok(totalStok);
            Barang saved = repository.save(existing);
            
            arusKasService.catatKas("KELUAR", "MODAL", totalPengeluaran,
                             "Restok Barang: " + existing.getNamaBarang() + " (x " + barangBaru.getStok() + ")");
            return saved;
        }else{
            Barang saved = repository.save(barangBaru);
            arusKasService.catatKas("KELUAR", "MODAL", totalPengeluaran,
                             "kulakan Barang: " + barangBaru.getNamaBarang() + barangBaru.getStok());
            return saved;
        }
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Barang update(Integer id, Barang barangBaru){
        return repository.findById(id)
                .map(barangLama ->{
                    barangLama.setNamaBarang(barangBaru.getNamaBarang());
                    barangLama.setHargaModalAvg(barangBaru.getHargaModalAvg());
                    barangLama.setHargaJualPerkiraan(barangBaru.getHargaJualPerkiraan());
                    barangLama.setStok(barangBaru.getStok());
                    barangLama.setStatusParkir(barangBaru.getStatusParkir());
                    return repository.save(barangLama);
                })
            .orElseThrow(() -> new ResourceNotFoundException("Barang tidak ditemukan" + id));
    }

    public List<Barang> getByStatusParkir(Boolean statusParkir){
        return repository.findByStatusParkir(statusParkir);
    }
}
