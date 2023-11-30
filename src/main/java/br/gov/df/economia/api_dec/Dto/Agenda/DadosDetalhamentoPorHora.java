package br.gov.df.economia.api_dec.Dto.Agenda;

import br.gov.df.economia.api_dec.Valicacoes.Enun.SituacaoProcessamento;
import br.gov.df.economia.api_dec.Valicacoes.Enun.TipoDoc;
import br.gov.df.economia.api_dec.Entity.TbAgenda;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record DadosDetalhamentoPorHora(
        Long cod_agenda_extracao,
        TipoDoc tipo_doc,
        Timestamp par_inicio,
        Timestamp par_fim,
        SituacaoProcessamento ind_situacao,
        String nome_arquivo,
        BigDecimal quantidade){
        public DadosDetalhamentoPorHora(TbAgenda porhora) {
                this(
                        porhora.getCod_agenda_extracao(),
                        porhora.getTipo_doc(),
                        porhora.getPar_inicio(),
                        porhora.getPar_fim(),
                        porhora.getInd_situacao(),
                        porhora.getNome_arquivo(),
                        porhora.getQuantidade());
        }

}

