package com.dudus.diecast_api.service;

import com.dudus.diecast_api.model.ArusKas;
import com.dudus.diecast_api.repository.ArusKasRepository;
import com.dudus.diecast_api.repository.TransaksiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class ArusKasService {
    private final ArusKasRepository repository;
    private final TransaksiRepository transaksiRepository;
    
    public ArusKasService(ArusKasRepository repository, TransaksiRepository transaksiRepository){
        this.repository = repository;
        this.transaksiRepository = transaksiRepository;
    }

    public List<ArusKas> getAll(){return repository.findAll();}
    public Optional<ArusKas> getById(Integer id){return repository.findById(id);}
    public ArusKas save(ArusKas aruskas){return repository.save(aruskas);}
    public void delete(Integer id){repository.deleteById(id);}

    public Map<String, Object> getDashboard(){
        BigDecimal saldoModal = hitungSaldo("MODAL");
        BigDecimal saldoProfit = hitungSaldo("PROFIT");
        BigDecimal saldoReseller = hitungSaldo("RESELLER");

        BigDecimal profitAllTime = hitungTotalMasuk("PROFIT");
        BigDecimal komisiAllTime = hitungTotalMasuk("RESELLER");
        BigDecimal totalModalKeluar = hitungTotalKeluar("MODAL");

        BigDecimal totalOmset = transaksiRepository.sumTotalOmset();
        if (totalOmset == null) totalOmset = BigDecimal.ZERO;

        BigDecimal roi = BigDecimal.ZERO;
        if (totalModalKeluar.compareTo(BigDecimal.ZERO) > 0) {
            roi = profitAllTime
                    .divide(totalModalKeluar, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("danaBelanjaModal", saldoModal);
        data.put("profitSaatIni", saldoProfit);
        data.put("komisiSaatIni", saldoReseller);
        data.put("profitAllTime", profitAllTime);
        data.put("komisiAllTime", komisiAllTime);
        data.put("totalOmset", totalOmset);
        data.put("roiPersen", roi);

        return data;
    }

    public BigDecimal hitungSaldo(String dompet){
        BigDecimal masuk = repository.sumByTipeAndDompet("MASUK", dompet);
        BigDecimal keluar = repository.sumByTipeAndDompet("KELUAR", dompet);

        masuk = masuk != null ? masuk : BigDecimal.ZERO;
        keluar = keluar != null ? keluar : BigDecimal.ZERO;
        return masuk.subtract(keluar);
    }

    public BigDecimal hitungTotalMasuk(String dompet){
        BigDecimal total = repository.sumByTipeAndDompet("MASUK", dompet);
        return total != null ? total : BigDecimal.ZERO;
    }
    public BigDecimal hitungTotalKeluar(String dompet){
        BigDecimal total = repository.sumByTipeAndDompet("KELUAR", dompet);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional  
    public void resetProfit(){
        BigDecimal saldo = hitungSaldo("PROFIT");
        if (saldo.compareTo(BigDecimal.ZERO)<= 0) {
            throw new IllegalArgumentException("Tidak ada profit yang bisa dicairkan! ");
        }
        catatKas("KELUAR", "PROFIT", saldo, "Penarikan Profit Owner");
    }

    @Transactional  
    public void resetReseller(){
        BigDecimal saldo = hitungSaldo("RESELLER");
        if (saldo.compareTo(BigDecimal.ZERO)<= 0) {
            throw new IllegalArgumentException("Tidak ada komisi yang bisa dicairkan! ");
        }
        catatKas("KELUAR", "RESELLER", saldo, "Penarikan Komisi Reseller");
    }
    // Suntikan Modal
    @Transactional
    public void suntikModal(BigDecimal jumlah, String keterangan){
        if (jumlah.compareTo(BigDecimal.ZERO)<= 0) {
            throw new IllegalArgumentException("Jumlah suntikan modal harus lebih dari 0! ");
        }
        catatKas("MASUK", "MODAL", jumlah, keterangan);
    }
    // Helper dari TransaksiService
    public void catatKas(String tipeKas, String dompet, BigDecimal jumlah, String keterangan){
        ArusKas kas = new ArusKas();
        kas.setTipeKas(tipeKas);
        kas.setDompet(dompet);
        kas.setJumlah(jumlah);
        kas.setKeterangan(keterangan);
        repository.save(kas);
    }
}
