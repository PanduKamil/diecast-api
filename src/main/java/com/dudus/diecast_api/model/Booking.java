package com.dudus.diecast_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "barang_id")
    private Barang barang;

    @Column(name = "nama_pembooking")
    private String namaPembooking;

    private Integer jumlah;

    @Column(name = "tanggal_booking")
    private LocalDateTime tanggalBooking;

    @Column(name = "batas_pembayaran")
    private LocalDate batasPembayaran;

    private String status;
}