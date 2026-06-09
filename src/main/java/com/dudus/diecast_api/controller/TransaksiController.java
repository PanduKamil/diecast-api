package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.model.Transaksi;
import com.dudus.diecast_api.service.TransaksiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaksi")
public class TransaksiController {
    private final TransaksiService service;

    public TransaksiController(TransaksiService service){
        this.service = service;
    }

    @GetMapping
    public List<Transaksi> getAll(){return service.getAll();}

    @GetMapping("/{id}")
    public ResponseEntity<Transaksi> getById(@PathVariable Integer id){
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/jual")
    public ResponseEntity<Transaksi> jual(@RequestBody Map<String, Object> body){
        Integer barangId = (Integer) body.get("barangId");
        Integer jumlah = (Integer) body.get("jumlah");
        BigDecimal hargaJual = new BigDecimal(body.get("hargaJual").toString());

        Transaksi result = service.jual(barangId, jumlah, hargaJual);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/batal/{id}")
    public ResponseEntity<Void> batal(@PathVariable Integer id){
        service.batal(id);
        return ResponseEntity.noContent().build();
    }


}
