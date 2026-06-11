package com.dudus.diecast_api.dto;


import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BarangRequest {
    
    @NotBlank(message = "Nama barang tidak boleh kosong")
    private String namaBarang;

    @NotNull(message = "Harga modal tidak boleh kosong")
    private BigDecimal hargaModalAvg;

    @NotNull(message = "Harga jual tidak boleh kosong")
    private BigDecimal hargaJualPerkiraan;

    @NotNull(message = "Stok tidak boleh kosong")
    @Min(value = 0, message = "Stok tidak boleh negatif")
    private Integer stok;

    @NotNull(message = "Status parkir tidak boleh kosong")
    private Boolean statusParkir;
}
