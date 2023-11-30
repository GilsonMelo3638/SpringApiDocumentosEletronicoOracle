package br.gov.df.economia.api_dec.Valicacoes;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFe;

public interface ValidadorDeConsultas {
    void validar(DadosNFe dados);
}
