package com.dudus.diecast_api.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class LaporanResponse {
    private String periode;
    private BigDecimal totalOmset;
    private BigDecimal totalKomisi;
    private BigDecimal totalBersih;
    private BigDecimal totalModal;
}
