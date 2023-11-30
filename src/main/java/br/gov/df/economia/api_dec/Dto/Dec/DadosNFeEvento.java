package br.gov.df.economia.api_dec.Dto.Dec;

import br.gov.df.economia.api_dec.Entity.TbNFeEvento;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigInteger;

public record DadosNFeEvento(
        @Id
        @NotNull
        BigInteger Nsudf,
        @Pattern(regexp = "\\d{44}")
        String Chave,
        BigInteger Nsuan,
        String IpTransmissor,
        String infDFe
        ) {
        public DadosNFeEvento(TbNFeEvento dados) { this(
                        dados.getNsudf(),
                        dados.getChave(),
                        dados.getNsuan(),
                        dados.getIpTransmissor(),
                        dados.getInfDFe());
        }
}
