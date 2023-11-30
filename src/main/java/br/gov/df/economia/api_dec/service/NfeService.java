package br.gov.df.economia.api_dec.service;

import br.gov.df.economia.api_dec.Repository.NFeRepository;
import br.gov.df.economia.api_dec.Entity.TbNFe;
//import br.gov.df.economia.agendador_extracao_avulsa.api_agenda.Repository.NfeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class NfeService {

    @Autowired
    private NFeRepository repository;
    public TbNFe findByNsudf(Long nsudf) {
        Optional<TbNFe> obj = repository.findById(nsudf);
        return obj.orElse(null);
    }
    public TbNFe findByChave(String chave) {
        return repository.findByChave(chave);
    }
}
