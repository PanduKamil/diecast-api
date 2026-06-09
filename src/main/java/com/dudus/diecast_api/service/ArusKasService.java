package com.dudus.diecast_api.service;

import com.dudus.diecast_api.model.ArusKas;
import com.dudus.diecast_api.repository.ArusKasRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class ArusKasService {
    private final ArusKasRepository repository;
    
    public ArusKasService(ArusKasRepository repository){
        this.repository = repository;
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

        Map<String, Object> data = new HashMap<>();
        data.put("danaBelanjaModal", saldoModal);
        data.put("profitSaatIni", saldoProfit);
        data.put("komisiSaatIni", saldoReseller);
        data.put("profitAllTime", profitAllTime);
        data.put("komisiAllTime", komisiAllTime);

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
