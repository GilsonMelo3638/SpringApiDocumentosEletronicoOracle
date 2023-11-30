package br.gov.df.economia.api_dec.Entity;

import br.gov.df.economia.api_dec.Dto.Agenda.DadosAgendamentoPorHora;
import br.gov.df.economia.api_dec.Dto.Agenda.DadosAtualizacaoPorHora;
import br.gov.df.economia.api_dec.Valicacoes.Enun.SituacaoProcessamento;
import br.gov.df.economia.api_dec.Valicacoes.Enun.TipoDoc;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity(name = "DecAgenda")
@Table(name = "DEC_AGENDA_EXTRACAO", schema = "ADMDEC", catalog = "")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TbAgenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cod_agenda_extracao;
    @Enumerated(EnumType.STRING)
    private TipoDoc tipo_doc;
    @Enumerated(EnumType.STRING)
    private SituacaoProcessamento ind_situacao;
    private Timestamp par_inicio;
    private Timestamp par_fim;
    private String nome_arquivo;
    private BigDecimal quantidade;

    public TbAgenda(DadosAgendamentoPorHora dados) {
        this.ind_situacao = dados.ind_situacao();
        this.tipo_doc = dados.tipo_doc();
        this.par_fim = dados.par_fim();
        this.par_inicio = dados.par_inicio();
        this.cod_agenda_extracao = dados.cod_agenda_extracao();

    }
    public void atualizarInformacoes(DadosAtualizacaoPorHora dados) {
        this.cod_agenda_extracao = dados.cod_agenda_extracao();
        if (dados.quantidade() != null) {
            this.quantidade = dados.quantidade();
        }
    }
    public void exclusaoLogica() {
        this.quantidade = null;
    }
}


