package br.gov.df.economia.api_dec.Valicacoes;
import br.gov.df.economia.api_dec.Dto.Dec.DadosNFe;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;


@Component
public class ValidadorHoraMinutos implements  ValidadorDeConsultas {
    public void validar(DadosNFe dados) {
        var minutos = dados.dhproc().toLocalDateTime().getMinute();
        var segundos = dados.dhproc().toLocalDateTime().getSecond();
        if (minutos != 0 | segundos != 0) {
            throw new ValidationException("Deve ser informado o início da hora, por exemplo: par_inicio: 2019-06-06T22:00:00-03:00;");
        }
    }
}