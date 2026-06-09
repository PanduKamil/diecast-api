package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.model.Barang;
import com.dudus.diecast_api.service.BarangService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import  java.util.List;

@RestController
@RequestMapping("/api/diecast")
public class BarangController {
    private final BarangService service;

    public BarangController(BarangService service){
        this.service = service;
    }

    @GetMapping
    public List<Barang> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Barang> getById(@PathVariable Long id){
        return ResponseEntity.ok(service.getByIdOrThrow(id));
    }

    @PostMapping
    public Barang create(@Valid @RequestBody Barang barang){
        return service.save(barang);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Barang> update(@PathVariable Long id, @Valid @RequestBody Barang barang){
        try {
            return ResponseEntity.ok(service.update(id, barang));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
