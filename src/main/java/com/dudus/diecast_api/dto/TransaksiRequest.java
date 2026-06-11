package com.dudus.diecast_api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransaksiRequest {
    @NotNull(message = "Barang tidak ada")
    private Integer barangId;

    @NotNull(message = "Jumlah tidak boleh kosong")
    @Min(value = 0, message = "Jumlah tidak boleh negatif")
    private Integer jumlah; 

    @NotNull(message = "harga jual tidak boleh kosong")
    private BigDecimal hargaJual;
}
