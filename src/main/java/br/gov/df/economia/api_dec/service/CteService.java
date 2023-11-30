package br.gov.df.economia.api_dec.service;

import br.gov.df.economia.api_dec.Entity.TbCTe;
import br.gov.df.economia.api_dec.Repository.CTeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class CteService {

    @Autowired
    private CTeRepository repository;
    public TbCTe findByNsudf(Long nsudf) {
        Optional<TbCTe> obj = repository.findById(nsudf);
        return obj.orElse(null);
    }
    public TbCTe findByChave(String chave) {
        return repository.findByChave(chave);
    }
}
