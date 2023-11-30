package br.gov.df.economia.api_dec.Entity;

import br.gov.df.economia.api_dec.Dto.Dec.DadosCTe;
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


@Entity(name = "DecCTe")
@Table(name = "DEC_DFE_CTE_SVD", schema = "ADMDEC", catalog = "")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "nsusvd")
public class TbCTe {
    @Id
    private Long nsusvd;
    private String chave;
    private String destinatario;
    private String emitente;
    private BigInteger nsuaut;
    private String IpTransmissor;
    private Timestamp dhproc;
    @ColumnTransformer(read= "EXTRACT(XML_DOCUMENTO,'//CTe/infCte','xmlns=\"http://www.portalfiscal.inf.br/cte\"')", write = "?")
    private String infDFe;

    public TbCTe(DadosCTe dados) {
        this.nsusvd = dados.Nsusvd();
        this.dhproc = dados.dhproc();
        this.chave = dados.Chave();
        this.nsuaut = dados.nsuaut();
        this.IpTransmissor = dados.IpTransmissor();
        this.infDFe = dados.infDFe();
    }
}

