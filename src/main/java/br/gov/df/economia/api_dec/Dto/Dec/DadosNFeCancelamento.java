package br.gov.df.economia.api_dec.Dto.Dec;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;
import java.sql.Timestamp;

public record DadosNFeCancelamento(
        @Id
        @NotNull
        BigInteger Nsudf,
        @NotNull
        String Chave,
        BigInteger Tp_evento,
        BigInteger Nsuan,
        String IpTransmissor,
        Timestamp Dhproc,
        Timestamp Dhevento)
{

}
