package br.gov.df.economia.api_dec.Valicacoes;

import br.gov.df.economia.api_dec.Dto.Agenda.DadosAgendamentoPorHora;

public interface ValidadorAgendamentoPorIntervalo {
    void validar(DadosAgendamentoPorHora dados);
}