package com.dudus.diecast_api.service;

import com.dudus.diecast_api.exception.ResourceNotFoundException;
import com.dudus.diecast_api.model.Barang;
import com.dudus.diecast_api.model.Transaksi;
import com.dudus.diecast_api.repository.BarangRepository;
import com.dudus.diecast_api.repository.TransaksiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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

    public List<Transaksi> getAll(){return transaksiRepository.findAll();}
    public Optional<Transaksi> getById(Integer id){return transaksiRepository.findById(id);}

    @Transactional
    public Transaksi jual(Integer barangId, Integer jumlah, BigDecimal hargaJual){
        // Cek Barang
        Barang barang = barangRepository.findById(barangId)
                .orElseThrow(() -> new ResourceNotFoundException("Barang tidak ditemukan: " + barangId));
        
        // Vlidasi ketersediaan stok
        if (barang.getStok() < jumlah) {
            throw new IllegalArgumentException("Stok tidak cukup. Stok tersedia hanya: " + barang.getStok());
        }

        // Harusnya gw taro di transaksi.java
        BigDecimal hargaModalSnapshot = barang.getHargaModalAvg();
        BigDecimal hargaJualSatuan = hargaJual;
        BigDecimal totalHargaJual = hargaJual.multiply(new BigDecimal(jumlah));
        BigDecimal totalModal = hargaModalSnapshot.multiply(new BigDecimal(jumlah));
        BigDecimal totalProfit = totalHargaJual.subtract(totalModal);
        BigDecimal komisiReseller = totalProfit.multiply(KOMISI_RESELLER).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netProfitOwner = totalProfit.subtract(komisiReseller);

        // menyimpan transaksi
        Transaksi transaksi = new Transaksi();
        transaksi.setBarang(barang);
        transaksi.setJumlah(jumlah);
        transaksi.setHargaJual(totalHargaJual);
        transaksi.setHargaJualSatuan(hargaJualSatuan);
        transaksi.setHargaModalSnapshot(hargaModalSnapshot);
        transaksi.setKomisiReseller(komisiReseller);
        transaksi.setNetProfitOwner(netProfitOwner);
        Transaksi saved = transaksiRepository.save(transaksi);

        // kurangi stok barang
        barang.setStok(barang.getStok() - jumlah);
        barangRepository.save(barang);

        // catat aruskas
        arusKasService.catatKas("MASUK", "MODAL", totalModal, "Modal Balik dari penjualan: " + barang.getNamaBarang());
        arusKasService.catatKas("MASUK", "RESELLER", komisiReseller, "komisi Reseller dari penjualan: " + barang.getNamaBarang());
        arusKasService.catatKas("MASUK", "PROFIT", netProfitOwner, "Laba owner dari penjualan: " + barang.getNamaBarang());
        return saved;
    }

    @Transactional
    public void batal(Integer idTransaksi){
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
    }
    
}
