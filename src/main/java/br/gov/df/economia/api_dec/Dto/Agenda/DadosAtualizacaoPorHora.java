package br.gov.df.economia.api_dec.Dto.Agenda;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DadosAtualizacaoPorHora(
        @NotNull
        Long cod_agenda_extracao,
        BigDecimal quantidade) {

}
