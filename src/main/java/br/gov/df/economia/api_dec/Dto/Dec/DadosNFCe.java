package br.gov.df.economia.api_dec.Dto.Dec;

import br.gov.df.economia.api_dec.Entity.TbNFCe;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

public record DadosNFCe(
        @Id
        Long nsu,
        String destinatario,
        @Pattern(regexp = "\\d{44}")
        String Chave,
        @DateTimeFormat
        @NotNull
        @Past
        Timestamp dhproc,
        String IpTransmissor,
        String infDFe){
        public DadosNFCe(TbNFCe dados) {this(
                dados.getNsu(),
                dados.getDestinatario(),
                dados.getChave(),
                dados.getDhproc(),
                dados.getIpTransmissor(),
                dados.getInfDFe());
        }
}
