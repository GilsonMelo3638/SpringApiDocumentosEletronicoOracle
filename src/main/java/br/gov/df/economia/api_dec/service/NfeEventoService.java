package br.gov.df.economia.api_dec.service;
import br.gov.df.economia.api_dec.Entity.TbNFeEvento;
import br.gov.df.economia.api_dec.Repository.NFeEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class NfeEventoService {

    @Autowired
    private NFeEventoRepository repository;
    public TbNFeEvento findByNsudf(Long nsudf) {
        Optional<TbNFeEvento> obj = repository.findById(nsudf);
        return obj.orElse(null);
    }
    public TbNFeEvento findByChave(String chave) {
        return repository.findByChave(chave);
    }
}
