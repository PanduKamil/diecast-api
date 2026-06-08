package com.dudus.diecast_api.service;

import com.dudus.diecast_api.model.Booking;
import com.dudus.diecast_api.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class BookingService {
    private final BookingRepository repository;

    public BookingService(BookingRepository repository){
        this.repository = repository;
    }
    
    public List<Booking> getAll(){return repository.findAll();}
    
    public Optional<Booking> getById(Long id){return repository.findById(id);}

    public Booking save(Booking booking){return repository.save(booking);}

    public void delete(Long id){repository.deleteById(id);}


}
