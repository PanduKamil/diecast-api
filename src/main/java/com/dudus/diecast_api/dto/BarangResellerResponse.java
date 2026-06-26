package com.dudus.diecast_api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BarangResellerResponse {
    private Integer id;
    private String namaBarang;
    private BigDecimal hargaJualPerkiraan;
    private Integer stok;
}