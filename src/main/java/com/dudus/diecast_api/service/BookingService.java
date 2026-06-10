package com.dudus.diecast_api.service;

import com.dudus.diecast_api.model.Booking;
import com.dudus.diecast_api.model.Transaksi;
import com.dudus.diecast_api.model.Barang;
import com.dudus.diecast_api.repository.BarangRepository;
import com.dudus.diecast_api.repository.BookingRepository;
import com.dudus.diecast_api.repository.TransaksiRepository;
import com.dudus.diecast_api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Service
public class BookingService {
    private final BookingRepository repository;
    private final BarangRepository barangRepository;
    private final TransaksiRepository transaksiRepository;
    private final ArusKasService arusKasService;

    public BookingService(BookingRepository repository, 
                            BarangRepository barangRepository, 
                            TransaksiRepository transaksiRepository,
                                        ArusKasService arusKasService){
        this.repository = repository;
        this.barangRepository = barangRepository;
        this.transaksiRepository = transaksiRepository;
        this.arusKasService = arusKasService;
    }
    
    public List<Booking> getAll(){return repository.findAll();}
    
    public Optional<Booking> getById(Integer id){return repository.findById(id);}

    @Transactional
    public Booking save(Booking bookingBaru){

        Barang barang = barangRepository.findById(bookingBaru.getBarang().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Barang tidak ada!"));
        if (barang.getStok() < bookingBaru.getJumlah()) {
            throw new IllegalArgumentException("Stok tidak cukup. hanya tersedia: " + barang.getStok());
        }
        // potong stok
        barang.setStok(barang.getStok() - bookingBaru.getJumlah());
        barangRepository.save(barang);
        return repository.save(bookingBaru);}

    public void delete(Integer id){repository.deleteById(id);
    }

    @Transactional
    public Transaksi lunas(Integer bookingId, BigDecimal hargaLaku){
        Booking booking = repository.findById(bookingId)
            .orElseThrow(()-> new ResourceNotFoundException("Booking tidak ada" + bookingId));
        if (!booking.getStatus().equals("ACTIVE")) {
            throw new IllegalArgumentException("Booking sudah" + booking.getStatus());
        }

        // ambil barang dari booking
        Barang barang = booking.getBarang();

        BigDecimal hargaModalSnapshot = barang.getHargaModalAvg();
        BigDecimal totalModal = hargaModalSnapshot.multiply(new BigDecimal(booking.getJumlah()));
        BigDecimal totalHargaJual = hargaLaku.multiply(new BigDecimal(booking.getJumlah()));
        BigDecimal totalProfit = totalHargaJual.subtract(totalModal);
        BigDecimal komisiReseller = totalProfit.multiply(new BigDecimal("0.35")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netProfitOwner = totalProfit.subtract(komisiReseller);

        // menyimpan transaksi
        Transaksi transaksi = new Transaksi();
        transaksi.setBarang(barang);
        transaksi.setJumlah(booking.getJumlah());
        transaksi.setHargaJual(totalHargaJual);
        transaksi.setHargaJualSatuan(hargaLaku);
        transaksi.setHargaModalSnapshot(hargaModalSnapshot);
        transaksi.setKomisiReseller(komisiReseller);
        transaksi.setNetProfitOwner(netProfitOwner);
        Transaksi saved = transaksiRepository.save(transaksi);

        // Status Booking
        booking.setStatus("COMPLETED");
        repository.save(booking);

        arusKasService.catatKas("MASUK", "MODAL", totalModal, "Modal Balik dari pelunasan Booking: " + barang.getNamaBarang());
        arusKasService.catatKas("MASUK", "RESELLER", komisiReseller, "komisi Reseller dari pelunasan Booking: " + barang.getNamaBarang());
        arusKasService.catatKas("MASUK", "PROFIT", netProfitOwner, "Laba owner dari pelunasan Booking: " + barang.getNamaBarang());
        
        return saved;
    }


}
