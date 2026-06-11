package com.dudus.diecast_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransaksiResponse {
    private Integer id;
    private Integer barangId;
    private String namaBarang;
    private Integer jumlah;
    private BigDecimal hargaJual;
    private BigDecimal komisiReseller;
    // private BigDecimal netProfitOwner;  // Ini untuk Reseller dihapus
    private LocalDateTime tanggalJual;
    // private BigDecimal hargaModalSnapshot; // Ini untuk RESELLER dihapus
    private BigDecimal hargaJualSatuan;

}
