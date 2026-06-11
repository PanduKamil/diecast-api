package com.dudus.diecast_api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private String namaBarang;
    private Integer barangId;
    private Integer id;
    private String namaPembooking;
    private BigDecimal hargaBooking;
    private Integer jumlah;
    private LocalDateTime tanggalBooking;
    private LocalDate batasPembayaran;
    private String status;
}
