package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.model.Booking;
import com.dudus.diecast_api.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Booking> getById(@PathVariable Long id){
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public Booking create(@RequestBody Booking booking){return service.save(booking);}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
            service.delete(id);
                return ResponseEntity.noContent().build();}


}
