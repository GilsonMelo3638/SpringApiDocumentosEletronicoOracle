package br.gov.df.economia.api_dec.Entity;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFCe;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import java.sql.Timestamp;


@Entity(name = "DecNFCe")
@Table(name = "DEC_DFE_NFCE", schema = "ADMDEC", catalog = "")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "nsu")
public class TbNFCe {
    @Id
    private Long nsu;
    private String destinatario;
    private String emitente;
    private String chave;
    private String IpTransmissor;
    private Timestamp dhproc;
    @ColumnTransformer(read = "EXTRACT(XML_DOCUMENTO,'//NFe/infNFe','xmlns=\"http://www.portalfiscal.inf.br/nfe\"')", write = "?")
    private String infDFe;

    public TbNFCe(DadosNFCe dados) {
        this.nsu = dados.nsu();
        this.chave = dados.Chave();
        this.dhproc = dados.dhproc();
        this.IpTransmissor = dados.IpTransmissor();
        this.infDFe = dados.infDFe();
    }
}

