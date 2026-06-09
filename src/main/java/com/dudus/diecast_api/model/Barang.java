package com.dudus.diecast_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
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

    @NotBlank(message = "Nama barang tidak boleh kosong")
    @Column(name = "nama_barang")
    private String namaBarang;

    @NotNull(message = "Harga Modal Tidak boleh kosong")
    @Column(name = "harga_modal_avg")
    private BigDecimal hargaModalAvg;
    
    @Column(name = "harga_jual_perkiraan")
    private BigDecimal hargaJualPerkiraan;

    @NotNull(message = "Stok tidak boleh kosong")
    @Min(value = 0, message = "Stok tidak boleh negatif")
    private Integer stok;

    @Column(name = "status_parkir")
    private Boolean statusParkir;

    @Column(name = "tanggal_masuk")
    @CreationTimestamp
    private LocalDateTime tanggalMasuk;
}
