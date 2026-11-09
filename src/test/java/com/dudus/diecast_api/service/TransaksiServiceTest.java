package com.dudus.diecast_api.service;

import com.dudus.diecast_api.dto.TransaksiRequest;
import com.dudus.diecast_api.dto.TransaksiResponse;
import com.dudus.diecast_api.exception.ResourceNotFoundException;
import com.dudus.diecast_api.model.Barang;
import com.dudus.diecast_api.model.Transaksi;
import com.dudus.diecast_api.repository.BarangRepository;
import com.dudus.diecast_api.repository.TransaksiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransaksiServiceTest {

    @Mock
    private BarangRepository barangRepository;

    @Mock
    private TransaksiRepository transaksiRepository;

    @Mock
    private ArusKasService arusKasService;

    @InjectMocks
    private TransaksiService transaksiService;

    // Test 1: Jual berhasil
    @Test
    void jual_berhasil() {
        // Setup barang palsu
        Barang barang = new Barang();
        barang.setId(1);
        barang.setNamaBarang("Hot Wheels Ferrari");
        barang.setStok(5);
        barang.setHargaModalAvg(new BigDecimal("50000"));

        // Setup request
        TransaksiRequest request = new TransaksiRequest();
        request.setBarangId(1);
        request.setJumlah(1);
        request.setHargaJual(new BigDecimal("75000"));

        // Mock: pura-pura repository return barang di atas
        when(barangRepository.findById(1)).thenReturn(Optional.of(barang));
        when(transaksiRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Jalankan
        TransaksiResponse result = transaksiService.jual(request);

        // Cek hasilnya
        assertNotNull(result);
        assertEquals(4, barang.getStok()); // stok berkurang 1
        verify(arusKasService, times(3)).catatKas(any(), any(), any(), any()); // kas dicatat 3x
    }

    // Test 2: Jual gagal karena stok kosong
    @Test
    void jual_gagal_stok_kurang() {
        Barang barang = new Barang();
        barang.setId(1);
        barang.setStok(0); // stok kosong

        TransaksiRequest request = new TransaksiRequest();
        request.setBarangId(1);
        request.setJumlah(1);
        request.setHargaJual(new BigDecimal("75000"));

        when(barangRepository.findById(1)).thenReturn(Optional.of(barang));

        // Harus throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            transaksiService.jual(request);
        });
    }

    // Test 3: Jual gagal karena barang tidak ada
    @Test
    void jual_gagal_barang_tidak_ada() {
        TransaksiRequest request = new TransaksiRequest();
        request.setBarangId(999); // id tidak ada
        request.setJumlah(1);
        request.setHargaJual(new BigDecimal("75000"));

        when(barangRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            transaksiService.jual(request);
        });
    }
    @Test
    void jual_hitung_komisi_dan_profit_benar() {
        Barang barang = new Barang();
        barang.setId(1);
        barang.setNamaBarang("Hot Wheels Ferrari");
        barang.setStok(5);
        barang.setHargaModalAvg(new BigDecimal("50000"));

        TransaksiRequest request = new TransaksiRequest();
        request.setBarangId(1);
        request.setJumlah(2);
        request.setHargaJual(new BigDecimal("75000"));

        when(barangRepository.findById(1)).thenReturn(Optional.of(barang));
        when(transaksiRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        transaksiService.jual(request);

        ArgumentCaptor<BigDecimal> nominal = ArgumentCaptor.forClass(BigDecimal.class);
        verify(arusKasService, times(3)).catatKas(any(), any(), nominal.capture(), any());

        // urutan: MODAL, RESELLER, PROFIT
        assertEquals(0, new BigDecimal("100000").compareTo(nominal.getAllValues().get(0)));
        assertEquals(0, new BigDecimal("17500").compareTo(nominal.getAllValues().get(1)));
        assertEquals(0, new BigDecimal("32500").compareTo(nominal.getAllValues().get(2)));
        assertEquals(3, barang.getStok());
    }

    @Test
    void batal_balikkan_kas_dan_stok_dengan_nominal_benar() {
        Barang barang = new Barang();
        barang.setId(1);
        barang.setNamaBarang("Hot Wheels Ferrari");
        barang.setStok(3);

        Transaksi t = new Transaksi();
        t.setBarang(barang);
        t.setJumlah(2);
        t.setHargaModalSnapshot(new BigDecimal("50000"));
        t.setKomisiReseller(new BigDecimal("17500"));
        t.setNetProfitOwner(new BigDecimal("32500"));

        when(transaksiRepository.findById(1)).thenReturn(Optional.of(t));

        transaksiService.batal(1);

        ArgumentCaptor<BigDecimal> nominal = ArgumentCaptor.forClass(BigDecimal.class);
        verify(arusKasService, times(3)).catatKas(eq("KELUAR"), any(), nominal.capture(), any());

        // MODAL harus 100000 (50000 x 2), bukan 50000
        assertEquals(0, new BigDecimal("100000").compareTo(nominal.getAllValues().get(0)));
        assertEquals(5, barang.getStok());
        verify(transaksiRepository).deleteById(1);
    }
}