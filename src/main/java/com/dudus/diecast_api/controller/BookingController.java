package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.model.Booking;
import com.dudus.diecast_api.model.Transaksi;
import com.dudus.diecast_api.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service){
        this.service = service;
    }

    @GetMapping
    public List<Booking> getAll(){return service.getAll();}

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getById(@PathVariable Integer id){
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public Booking create(@RequestBody Booking booking){return service.save(booking);}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
            service.delete(id);
                return ResponseEntity.noContent().build();
    }

    @PostMapping("/lunas/{id}")
    public ResponseEntity<Transaksi> lunas(@PathVariable Integer id, @RequestBody Map<String, Object> body){
        BigDecimal hargaLaku = new BigDecimal(body.get("hargaLaku").toString());
        return ResponseEntity.ok(service.lunas(id, hargaLaku));
    }


}
