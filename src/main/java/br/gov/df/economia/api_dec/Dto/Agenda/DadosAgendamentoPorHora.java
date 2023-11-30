package br.gov.df.economia.api_dec.Dto.Agenda;

import br.gov.df.economia.api_dec.Valicacoes.Enun.SituacaoProcessamento;
import br.gov.df.economia.api_dec.Valicacoes.Enun.TipoDoc;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

public record DadosAgendamentoPorHora (

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long cod_agenda_extracao,
        @NotNull
        SituacaoProcessamento ind_situacao,
        @DateTimeFormat
        @NotNull
        @Past
        Timestamp par_inicio,
        @DateTimeFormat
        @NotNull
        @Past
        Timestamp par_fim,
        @NotNull
        TipoDoc tipo_doc) {
}
