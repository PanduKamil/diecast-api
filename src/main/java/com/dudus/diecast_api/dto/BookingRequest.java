package com.dudus.diecast_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import lombok.Data;

@Data
public class BookingRequest {
    @NotNull(message = "Barang tidak boleh kosong")
    private Integer barangId;
    @NotBlank(message = "Harus diberi nama pembookingnya")
    private String namaPembooking;

    @NotNull(message = "Kasih harga booking")
    private BigDecimal hargaBooking;

    @NotNull(message = "Jangan dikosongin")
    @Min(value = 0, message = "Jangan dibawah 0")
    private Integer jumlah;

    @NotNull(message = "Kasih batas pembayaran")
    private LocalDate batasPembayaran;


}
