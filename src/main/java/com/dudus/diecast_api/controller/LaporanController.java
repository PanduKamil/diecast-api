package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.dto.LaporanResponse;
import com.dudus.diecast_api.service.LaporanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/laporan")
public class LaporanController {
    private final LaporanService service;

    public LaporanController(LaporanService service){
        this.service = service;
    }

    @GetMapping("/penjualan")
    public ResponseEntity<LaporanResponse> getLaporan(
                            @RequestParam(required = false) Integer bulan,
                            @RequestParam(required = false) Integer tahun){
                                return ResponseEntity.ok(service.getLaporan(bulan, tahun));
                            }

}
