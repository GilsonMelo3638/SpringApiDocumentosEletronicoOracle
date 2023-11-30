package br.gov.df.economia.api_dec.Valicacoes;

import java.sql.Timestamp;
import java.time.Duration;

public class ValidadorDeLimiteExtracao {
    public static boolean validarDiferencaDias(Timestamp data1, Timestamp data2, int limiteDias) {
        Duration diff = Duration.between(data1.toInstant(), data2.toInstant());
        return diff.toDays() <= limiteDias;
    }
}
