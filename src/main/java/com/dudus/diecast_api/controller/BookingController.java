package com.dudus.diecast_api.controller;

import com.dudus.diecast_api.dto.BookingRequest;
import com.dudus.diecast_api.dto.BookingResponse;
import com.dudus.diecast_api.model.Transaksi;
import com.dudus.diecast_api.service.BookingService;

import jakarta.validation.Valid;

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
    public List<BookingResponse> getAll(){return service.getAll();}

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable Integer id){
        return ResponseEntity.ok(service.getByIdOrThrow(id));
    }
    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request){
        return ResponseEntity.ok(service.saveDto(request));}

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

    @PostMapping("/batal/{id}")
    public ResponseEntity<Void> batal(@PathVariable Integer id){
        service.batal(id);
        return ResponseEntity.noContent().build();
    }
}
