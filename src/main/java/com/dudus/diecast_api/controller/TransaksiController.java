package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.dto.TransaksiRequest;
import com.dudus.diecast_api.dto.TransaksiResponse;
import com.dudus.diecast_api.service.TransaksiService;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/transaksi")
public class TransaksiController {
    private final TransaksiService service;

    public TransaksiController(TransaksiService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity <Page<TransaksiResponse>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy){
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
            return ResponseEntity.ok(service.getAll(pageable));}

    @GetMapping("/{id}")
    public ResponseEntity<TransaksiResponse> getById(@PathVariable Integer id){
        return ResponseEntity.ok(service.getByIdOrThrow(id));
    }

    @PostMapping("/jual")
    public ResponseEntity<TransaksiResponse> jual(@Valid @RequestBody TransaksiRequest request){
        return ResponseEntity.ok(service.jual(request));
    }
    @PostMapping("/batal/{id}")
    public ResponseEntity<Void> batal(@PathVariable Integer id){
        service.batal(id);
        return ResponseEntity.noContent().build();
    }


}
