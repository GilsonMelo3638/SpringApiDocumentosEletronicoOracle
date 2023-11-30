package br.gov.df.economia.api_dec.Dto.Dec;

import br.gov.df.economia.api_dec.Entity.TbNFe;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.sql.Timestamp;

public record DadosNFe(
        @Id
        Long Nsudf,
        String destinatario,
        @Pattern(regexp = "\\d{44}")
        String Chave,
        BigInteger Nsuan,
        @DateTimeFormat
        @NotNull
        @Past
        Timestamp dhproc,
        String IpTransmissor,
        String infDFe){
         public  DadosNFe(TbNFe dados) {this(
               dados.getNsudf(),
                dados.getDestinatario(),
                dados.getChave(),
                dados.getNsuan(),
                dados.getDhproc(),
                dados.getIpTransmissor(),
                dados.getInfDFe());
        }

}
