package br.gov.df.economia.api_dec.Dto.Dec;

import br.gov.df.economia.api_dec.Entity.TbNFe;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

public record DadosRetornoNFe(
        @Id
        Long Nsudf,
        @Pattern(regexp = "quantidade de números {44}")
        String Chave,
        @DateTimeFormat
        @NotNull
        @Past
        Timestamp dhproc){
    public DadosRetornoNFe(TbNFe dados) {
        this(
                dados.getNsudf(),
                dados.getChave(),
                dados.getDhproc());
    }
}
