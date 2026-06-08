package com.dudus.diecast_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaksi")
public class Transaksi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "barang_id")
    private Barang barang;

    private Integer jumlah;

    @Column(name = "harga_jual")
    private BigDecimal hargaJual;

    @Column(name = "komisi_reseller")
    private BigDecimal komisiReseller;

    @Column(name = "net_profit_owner")
    private BigDecimal netProfitOwner;

    @Column(name = "tanggal_jual")
    private LocalDateTime tanggalJual;

    @Column(name = "harga_modal_snapshot")
    private BigDecimal hargaModalSnapshot;

    @Column(name = "harga_jual_satuan")
    private BigDecimal hargaJualSatuan;
}