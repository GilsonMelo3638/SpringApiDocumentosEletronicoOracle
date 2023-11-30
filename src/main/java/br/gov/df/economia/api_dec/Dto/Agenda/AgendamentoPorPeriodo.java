package br.gov.df.economia.api_dec.Dto.Agenda;

import br.gov.df.economia.api_dec.Repository.DecAgenda;
import br.gov.df.economia.api_dec.Entity.TbAgenda;
import br.gov.df.economia.api_dec.Valicacoes.ValidadorAgendamentoPorIntervalo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendamentoPorPeriodo {

    @Autowired
    private DecAgenda repository;
    @Autowired
    private List<ValidadorAgendamentoPorIntervalo> validadores;
    public void agendar(DadosAgendamentoPorHora dados) {
        validadores.forEach(v -> v.validar(dados));
        var consulta =  new TbAgenda(dados);
        //repository.save(consulta);
    }

}