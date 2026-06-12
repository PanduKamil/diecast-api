package com.dudus.diecast_api.service;

import com.dudus.diecast_api.dto.TransaksiRequest;
import com.dudus.diecast_api.dto.TransaksiResponse;
import com.dudus.diecast_api.exception.ResourceNotFoundException;
import com.dudus.diecast_api.model.Barang;
import com.dudus.diecast_api.model.Transaksi;
import com.dudus.diecast_api.repository.BarangRepository;
import com.dudus.diecast_api.repository.TransaksiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransaksiService {
    private static final BigDecimal KOMISI_RESELLER = new BigDecimal("0.35");

    private final TransaksiRepository transaksiRepository;
    private final BarangRepository barangRepository;
    private final ArusKasService arusKasService;


    public TransaksiService(TransaksiRepository transaksiRepository, BarangRepository barangRepository, ArusKasService arusKasService){
        this.transaksiRepository = transaksiRepository;
        this.barangRepository = barangRepository;
        this.arusKasService = arusKasService;
    }

    private TransaksiResponse toResponse(Transaksi transaksi){
        TransaksiResponse response = new TransaksiResponse();
        response.setId(transaksi.getId());
        response.setBarangId(transaksi.getBarang().getId());
        response.setNamaBarang(transaksi.getBarang().getNamaBarang());
        response.setJumlah(transaksi.getJumlah());
        response.setHargaJual(transaksi.getHargaJual());
        response.setHargaJualSatuan(transaksi.getHargaJualSatuan());
        response.setKomisiReseller(transaksi.getKomisiReseller());
        response.setTanggalJual(transaksi.getTanggalJual());
        return response; 
    }
    public Page<TransaksiResponse> getAll(Pageable pageable){
        return transaksiRepository.findAll(pageable)
                .map(this::toResponse);
    }
    public TransaksiResponse getByIdOrThrow(Integer id){
        Transaksi transaksi = transaksiRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaksi tidak ditemukan" + id));
        return toResponse(transaksi);
    }

    @Transactional
    public TransaksiResponse jual(TransaksiRequest request){
        // Info2 logg barang kejual
        log.info("Proses penjualan barangId: {}, jumlah: {}",
                request.getBarangId(), request.getJumlah());
        // Cek Barang
        Barang barang = barangRepository.findById(request.getBarangId())
                .orElseThrow(() -> new ResourceNotFoundException("Barang tidak ditemukan: " + request.getBarangId()));
        
        // Vlidasi ketersediaan stok
        if (barang.getStok() < request.getJumlah()) {
            throw new IllegalArgumentException("Stok tidak cukup. Stok tersedia hanya: " + barang.getStok());
        }

        // Harusnya gw taro di transaksi.java
        BigDecimal hargaModalSnapshot = barang.getHargaModalAvg();
        BigDecimal hargaJualSatuan = request.getHargaJual();
        BigDecimal totalHargaJual = request.getHargaJual().multiply(new BigDecimal(request.getJumlah()));
        BigDecimal totalModal = hargaModalSnapshot.multiply(new BigDecimal(request.getJumlah()));
        BigDecimal totalProfit = totalHargaJual.subtract(totalModal);
        BigDecimal komisiReseller = totalProfit.multiply(KOMISI_RESELLER).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netProfitOwner = totalProfit.subtract(komisiReseller);

        // menyimpan transaksi
        Transaksi transaksi = new Transaksi();
        transaksi.setBarang(barang);
        transaksi.setJumlah(request.getJumlah());
        transaksi.setHargaJual(totalHargaJual);
        transaksi.setHargaJualSatuan(hargaJualSatuan);
        transaksi.setHargaModalSnapshot(hargaModalSnapshot);
        transaksi.setKomisiReseller(komisiReseller);
        transaksi.setNetProfitOwner(netProfitOwner);
        Transaksi saved = transaksiRepository.save(transaksi);

        // kurangi stok barang
        barang.setStok(barang.getStok() - request.getJumlah());
        barangRepository.save(barang);

        // catat aruskas
        arusKasService.catatKas("MASUK", "MODAL", totalModal, "Modal Balik dari penjualan: " + barang.getNamaBarang());
        arusKasService.catatKas("MASUK", "RESELLER", komisiReseller, "komisi Reseller dari penjualan: " + barang.getNamaBarang());
        arusKasService.catatKas("MASUK", "PROFIT", netProfitOwner, "Laba owner dari penjualan: " + barang.getNamaBarang());
        //log berhasil
        log.info("Transaksi Berhasil barang id: {}, total: {}",
                saved.getId(), saved.getHargaJual()
        );
        return toResponse(saved);
    }

    @Transactional
    public void batal(Integer idTransaksi){
        log.info("Membatal transaksi id: {}", idTransaksi);
        // cek validasi adanya transaksi
        Transaksi transaksi = transaksiRepository.findById(idTransaksi)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaksi tidak ditemukan " + idTransaksi));
    Barang barang = transaksi.getBarang();
    
    // reverse aruskas
    arusKasService.catatKas("KELUAR", "MODAL", transaksi.getHargaModalSnapshot(), 
                    "Pembatalan Transaksi #" + idTransaksi + ": " + barang.getNamaBarang());
    arusKasService.catatKas("KELUAR", "PROFIT", transaksi.getNetProfitOwner(), 
                    "Pembatalan Transaksi #" + idTransaksi + ": " + barang.getNamaBarang());
    arusKasService.catatKas("KELUAR", "RESELLER", transaksi.getKomisiReseller(), 
                    "Pembatalan Transaksi #" + idTransaksi + ": " + barang.getNamaBarang());

    // balikin stok
    barang.setStok(barang.getStok() + transaksi.getJumlah());
    barangRepository.save(barang);

    // hapus transaksi
    transaksiRepository.deleteById(idTransaksi);
            log.info("Transaksi {} berhasil dibatalkan", idTransaksi);

    }
    
}
