package com.dudus.diecast_api.service;

import com.dudus.diecast_api.dto.LaporanResponse;
import com.dudus.diecast_api.repository.TransaksiRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class LaporanService {
    private final TransaksiRepository transaksiRepository;

    public LaporanService(TransaksiRepository transaksiRepository){
        this.transaksiRepository = transaksiRepository;
    }

    public LaporanResponse getLaporan(Integer bulan, Integer tahun){
        Object[] raw;
        String periode;

        if (bulan != null && tahun != null) {
            raw = (Object[])transaksiRepository.getLaporanBulanan(bulan, tahun)[0];
            periode = bulan + "/" + tahun;
        }else{
            raw = (Object[])transaksiRepository.getLaporanSemuaPeriode()[0];
            periode = "SEMUA PERIODE";
        }

        LaporanResponse response = new LaporanResponse();
        response.setPeriode(periode);
        response.setTotalOmset(raw[0] != null ? (BigDecimal) raw[0] : BigDecimal.ZERO);
        response.setTotalKomisi(raw[1] != null ? (BigDecimal) raw[1] : BigDecimal.ZERO);
        response.setTotalBersih(raw[2] != null ? (BigDecimal) raw[2] : BigDecimal.ZERO);
        response.setTotalModal(raw[3] != null ? (BigDecimal) raw[3] : BigDecimal.ZERO);
        return response;
    }
}
