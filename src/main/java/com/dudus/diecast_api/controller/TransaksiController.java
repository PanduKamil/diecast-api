package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.model.Transaksi;
import com.dudus.diecast_api.service.TransaksiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Transaksi> getById(@PathVariable Long id){
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Transaksi create(@RequestBody Transaksi transaksi){
        return service.save(transaksi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    } 
}
