package br.gov.df.economia.api_dec.Dto.Dec;

import br.gov.df.economia.api_dec.Entity.TbCTe;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.sql.Timestamp;

public record DadosCTe(
        @Id
        Long Nsusvd,
        String destinatario,
        @Pattern(regexp = "\\d{44}")
        String Chave,
        BigInteger nsuaut,
        @DateTimeFormat
        @NotNull
        @Past
        Timestamp dhproc,
        String IpTransmissor,
        String infDFe){
        public DadosCTe(TbCTe dados) {this(
                dados.getNsusvd(),
                dados.getDestinatario(),
                dados.getChave(),
                dados.getNsuaut(),
                dados.getDhproc(),
                dados.getIpTransmissor(),
                dados.getInfDFe());
        }

}
