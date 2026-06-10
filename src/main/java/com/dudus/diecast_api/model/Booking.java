package com.dudus.diecast_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CurrentTimestamp;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "barang_id")
    private Barang barang;

    @Column(name = "nama_pembooking")
    private String namaPembooking;

    @Column(name = "harga_booking")
    private BigDecimal hargaBooking;

    private Integer jumlah;

    @CurrentTimestamp
    @Column(name = "tanggal_booking")
    private LocalDateTime tanggalBooking;

    @Column(name = "batas_pembayaran")
    private LocalDate batasPembayaran;

    private String status;
}