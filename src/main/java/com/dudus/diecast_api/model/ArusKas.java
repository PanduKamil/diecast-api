package com.dudus.diecast_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "arus_kas")
public class ArusKas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kas")
    private Integer idKas;

    private LocalDateTime tanggal;

    @Column(name = "tipe_kas")
    private String tipeKas;

    private String dompet;
    private BigDecimal jumlah;
    private String keterangan;
}