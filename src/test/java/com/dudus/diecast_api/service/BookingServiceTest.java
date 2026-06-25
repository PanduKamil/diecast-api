package com.dudus.diecast_api.service;

import com.dudus.diecast_api.dto.BookingRequest;
import com.dudus.diecast_api.dto.BookingResponse;
import com.dudus.diecast_api.model.Barang;
import com.dudus.diecast_api.model.Booking;
import com.dudus.diecast_api.repository.BarangRepository;
import com.dudus.diecast_api.repository.BookingRepository;
import com.dudus.diecast_api.repository.TransaksiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private BarangRepository barangRepository;

    @Mock
    private TransaksiRepository transaksiRepository;

    @Mock
    private ArusKasService arusKasService;

    @InjectMocks
    private BookingService bookingService;

    // Test 1: Booking berhasil
    @Test
    void booking_berhasil() {
        Barang barang = new Barang();
        barang.setId(1);
        barang.setNamaBarang("Hot Wheels BMW");
        barang.setStok(3);
        barang.setHargaModalAvg(new BigDecimal("50000"));

        BookingRequest request = new BookingRequest();
        request.setBarangId(1);
        request.setJumlah(1);
        request.setNamaPembooking("Budi");
        request.setHargaBooking(new BigDecimal("75000"));

        when(barangRepository.findById(1)).thenReturn(Optional.of(barang));
        when(repository.save(any())).thenAnswer(i -> {
            Booking b = i.getArgument(0);
            b.setBarang(barang);
            return b;
        });

        BookingResponse result = bookingService.saveDto(request);

        assertNotNull(result);
        assertEquals(2, barang.getStok()); // stok berkurang 1
        assertEquals("ACTIVE", result.getStatus());
    }

    // Test 2: Booking gagal stok kurang
    @Test
    void booking_gagal_stok_kurang() {
        Barang barang = new Barang();
        barang.setId(1);
        barang.setStok(0);

        BookingRequest request = new BookingRequest();
        request.setBarangId(1);
        request.setJumlah(1);
        request.setNamaPembooking("Budi");
        request.setHargaBooking(new BigDecimal("75000"));

        when(barangRepository.findById(1)).thenReturn(Optional.of(barang));

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.saveDto(request);
        });
    }

    // Test 3: Batal booking berhasil — stok balik
    @Test
    void batal_booking_berhasil() {
        Barang barang = new Barang();
        barang.setId(1);
        barang.setStok(2);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setJumlah(1);
        booking.setStatus("ACTIVE");
        booking.setBarang(barang);

        when(repository.findById(1)).thenReturn(Optional.of(booking));

        bookingService.batal(1);

        assertEquals(3, barang.getStok()); // stok balik
        assertEquals("CANCELLED", booking.getStatus());
    }

    // Test 4: Batal booking gagal karena sudah COMPLETED
    @Test
    void batal_booking_gagal_sudah_completed() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStatus("COMPLETED");

        when(repository.findById(1)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.batal(1);
        });
    }
}