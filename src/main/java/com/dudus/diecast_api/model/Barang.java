package com.dudus.diecast_api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime; 

@Data
@Entity
@Table(name = "barang")
public class Barang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nama_barang")
    private String namaBarang;

    @Column(name = "harga_modal_avg")
    private BigDecimal hargaModalAvg;
    
    @Column(name = "harga_jual_perkiraan")
    private BigDecimal hargaJualPerkiraan;

    private Integer stok;

    @Column(name = "status_parkir")
    private Boolean statusParkir;

    @Column(name = "tanggal_masuk")
    @CreationTimestamp
    private LocalDateTime tanggalMasuk;
}
