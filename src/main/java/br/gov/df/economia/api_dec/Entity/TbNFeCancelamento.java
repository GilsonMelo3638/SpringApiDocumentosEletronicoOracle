package br.gov.df.economia.api_dec.Entity;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFeCancelamento;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity(name = "DecNFeCancelamento")
@Table(name = "DEC_DFE_NFE_CANCELAMENTO", schema = "ADMDEC", catalog = "")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "chave")
public class TbNFeCancelamento {
    @Id
    private BigInteger Nsudf;
    private String Chave;
    private BigInteger Tp_evento;
    private BigInteger Nsuan;
    private String IpTransmissor;
    private Timestamp Dhproc;
    private Timestamp Dhevento;

    public TbNFeCancelamento(DadosNFeCancelamento dados) {
        this.Nsudf = dados.Nsudf();
        this.Chave = dados.Chave();
        this.Tp_evento = dados.Tp_evento();
        this.Nsuan = dados.Nsuan();
        this.IpTransmissor = dados.IpTransmissor();
        this.Dhproc = dados.Dhproc();
        this.Dhevento = dados.Dhevento();
    }
}
