package br.gov.df.economia.api_dec.Dto.Agenda;

import br.gov.df.economia.api_dec.Entity.TbAgenda;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record DadosListagemAgendamento(Long cod_agenda_extracao, Timestamp par_inicio,  Timestamp par_fim, String nome_arquivo, BigDecimal quantidade) {
    public DadosListagemAgendamento(TbAgenda porhora){
        this(porhora.getCod_agenda_extracao(), porhora.getPar_inicio(), porhora.getPar_fim(), porhora.getNome_arquivo(), porhora.getQuantidade());
    }

}
