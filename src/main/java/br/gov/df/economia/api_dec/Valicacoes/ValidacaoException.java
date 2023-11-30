package br.gov.df.economia.api_dec.Valicacoes;

public class ValidacaoException extends RuntimeException {
    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
