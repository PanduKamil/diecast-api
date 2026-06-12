package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.dto.BarangRequest;
import com.dudus.diecast_api.dto.BarangResponse;
import com.dudus.diecast_api.model.Barang;
import com.dudus.diecast_api.service.BarangService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import  java.util.List;

@RestController
@RequestMapping("/api/diecast")
public class BarangController {
    private final BarangService service;

    public BarangController(BarangService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<BarangResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy){
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BarangResponse> getById(@PathVariable Integer id){
        return ResponseEntity.ok(service.getByIdOrThrow(id));
    }

    @PostMapping
    public ResponseEntity<BarangResponse> create(@Valid @RequestBody BarangRequest request){
        return ResponseEntity.ok(service.saveDto(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Barang> update(@PathVariable Integer id, @Valid @RequestBody Barang barang){
        try {
            return ResponseEntity.ok(service.update(id, barang));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Barang>> getByStatusParkir(@RequestParam Boolean statusParkir){
        return ResponseEntity.ok(service.getByStatusParkir(statusParkir));
    }

}
