package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.model.ArusKas;
import com.dudus.diecast_api.service.ArusKasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arus-kas")
public class ArusKasController {
    private final ArusKasService service;

    public ArusKasController(ArusKasService service){
        this.service = service;
    }

    @GetMapping
    public List<ArusKas> getAll(){return service.getAll();}

    @GetMapping("/{id}")
    public ResponseEntity<ArusKas> getById(@PathVariable Long id){
        return service.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ArusKas create(@RequestBody ArusKas arusKas){
        return service.save(arusKas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
