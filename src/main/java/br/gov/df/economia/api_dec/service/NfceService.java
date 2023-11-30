package br.gov.df.economia.api_dec.service;

import br.gov.df.economia.api_dec.Repository.NFCeRepository;
import br.gov.df.economia.api_dec.Entity.TbNFCe;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class NfceService {

    @Autowired
    private NFCeRepository repository;
    public TbNFCe findByNsuNsu(Long nsu) {
        Optional<TbNFCe> obj = repository.findById(nsu);
        return obj.orElse(null);
    }
    public TbNFCe findByChaveNfce(String chave) {
        return repository.findByChave(chave);
    }
}
