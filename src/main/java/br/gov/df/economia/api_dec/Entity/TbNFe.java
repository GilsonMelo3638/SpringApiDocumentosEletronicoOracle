package br.gov.df.economia.api_dec.Entity;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFe;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity(name = "DecNFe")
@Table(name = "DEC_DFE_NFE", schema = "ADMDEC", catalog = "")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "nsudf")
public class TbNFe {
    @Id
    private Long nsudf;
    private String chave;
    private String destinatario;
    private String emitente;
    private BigInteger Nsuan;
    private String IpTransmissor;
    private Timestamp dhproc;
    @ColumnTransformer(read= "EXTRACT(XML_DOCUMENTO,'//NFe/infNFe','xmlns=\"http://www.portalfiscal.inf.br/nfe\"')", write = "?")
    private String infDFe;

    public TbNFe(DadosNFe dados) {
        this.nsudf = dados.Nsudf();
        this.dhproc = dados.dhproc();
        this.chave = dados.Chave();
        this.Nsuan = dados.Nsuan();
        this.IpTransmissor = dados.IpTransmissor();
        this.infDFe = dados.infDFe();
    }
}

