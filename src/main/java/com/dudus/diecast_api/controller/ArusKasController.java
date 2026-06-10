package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.model.ArusKas;
import com.dudus.diecast_api.service.ArusKasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<ArusKas> getById(@PathVariable Integer id){
        return service.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ArusKas create(@RequestBody ArusKas arusKas){
        return service.save(arusKas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(){
        return ResponseEntity.ok(service.getDashboard());
    }

    @PostMapping("/reset/profit")
    public ResponseEntity<Void> resetProfit(){
            service.resetProfit();
            return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset/reseller")
    public ResponseEntity<Void> resetReseller(){
            service.resetReseller();
            return ResponseEntity.noContent().build();
    }
}
