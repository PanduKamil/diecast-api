package com.dudus.diecast_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BarangResponse {
    
    private Integer id;
    private String namaBarang;
    private BigDecimal hargaJualPerkiraan;
    private BigDecimal hargaModalAvg; // nanti perlu dihilangkan untuk reseller
    private Integer stok;
    private Boolean statusParkir;
    private LocalDateTime tanggalMasuk;
}
