package com.dudus.diecast_api.service;

import com.dudus.diecast_api.model.Barang;
import com.dudus.diecast_api.repository.BarangRepository;
import com.dudus.diecast_api.dto.BarangRequest;
import com.dudus.diecast_api.dto.BarangResponse;
import com.dudus.diecast_api.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BarangService {
    private final BarangRepository repository;
    private final ArusKasService arusKasService;

    public BarangService(BarangRepository repository, ArusKasService arusKasService) {
        this.repository = repository;
        this.arusKasService = arusKasService;
    }

    private Barang toEntity(BarangRequest request){
        Barang barang = new Barang();
        barang.setNamaBarang(request.getNamaBarang());
        barang.setHargaModalAvg(request.getHargaModalAvg());
        barang.setHargaJualPerkiraan(request.getHargaJualPerkiraan());
        barang.setStok(request.getStok());
        barang.setStatusParkir(request.getStatusParkir());
        return barang;
    }
    private BarangResponse toResponse(Barang barang){
        BarangResponse response = new BarangResponse();
        response.setId(barang.getId());
        response.setNamaBarang(barang.getNamaBarang());
        response.setHargaJualPerkiraan(barang.getHargaJualPerkiraan());
        response.setHargaModalAvg(barang.getHargaModalAvg()); //Nanti di ilangkan untuk menu user
        response.setStok(barang.getStok());
        response.setStatusParkir(barang.getStatusParkir());
        response.setTanggalMasuk(barang.getTanggalMasuk());
        return response;
    }
    public Page<BarangResponse> getAll(Pageable pageable){
        return repository.findAll(pageable) 
                .map(this::toResponse);
    }

    public Optional<Barang> getById(Integer id)  {
        return repository.findById(id);
    }

    public BarangResponse getByIdOrThrow(Integer id){
        Barang barang = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("barang tifak ditemukan : " + id));
                return toResponse(barang);
    }

    public BarangResponse saveDto(BarangRequest request){
        Barang barang = toEntity(request);
        Barang saved = save(barang);
        return toResponse(saved);
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
    public List<BarangResponse> search(String nama) {
    return repository.findByNamaBarangContainingIgnoreCase(nama)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}
}
