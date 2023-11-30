package br.gov.df.economia.api_dec.Entity;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFeEvento;
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

@Entity(name = "DecNFeEvento")
@Table(name = "DEC_DFE_NFE_EVENTO", schema = "ADMDEC", catalog = "")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "chave")
public class TbNFeEvento {
    @Id
    private BigInteger nsudf;
    private String chave;
    private Integer Seq_evento;
    private BigInteger Tp_evento;
    private BigInteger nsuan;
    private String IpTransmissor;
    private Timestamp dhproc;
    private Timestamp dhevento;
    @ColumnTransformer(read= "EXTRACT(XML_DOCUMENTO,'//evento/infEvento','xmlns=\"http://www.portalfiscal.inf.br/nfe\"')", write = "?")
    private String infDFe;
    public TbNFeEvento(DadosNFeEvento dados) {
        this.nsudf = dados.Nsudf();
        this.chave = dados.Chave();
        this.nsuan = dados.Nsuan();
        this.IpTransmissor = dados.IpTransmissor();
        this.infDFe = dados.infDFe();
    }
}