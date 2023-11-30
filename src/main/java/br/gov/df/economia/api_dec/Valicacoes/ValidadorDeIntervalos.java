package br.gov.df.economia.api_dec.Valicacoes;

import br.gov.df.economia.api_dec.Entity.TbAgenda;
import br.gov.df.economia.api_dec.Dto.Agenda.DadosAgendamentoPorHora;
import br.gov.df.economia.api_dec.Repository.DecAgenda;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component ("ValidadorIntervaloDeMinutos")
public class ValidadorDeIntervalos implements ValidadorAgendamentoPorIntervalo {
    @Autowired
    private DecAgenda repository;
    public void validar(DadosAgendamentoPorHora dados) {
        var mesmahora = dados.par_fim().toLocalDateTime().getHour() - dados.par_inicio().toLocalDateTime().getHour();
        var diferencaEmMinutos = Duration.between(dados.par_fim().toLocalDateTime(), dados.par_inicio().toLocalDateTime()).toMinutes();
        var diferencaEmDias = Duration.between(dados.par_fim().toLocalDateTime(), dados.par_inicio().toLocalDateTime()).toDays();
        var porhora = new TbAgenda(dados);
        if (diferencaEmMinutos != -59 | diferencaEmDias != 0 | mesmahora != 0)
        {
            throw new ValidationException("O intervalo deve ser uma hora exata. Exemplo de intervalo correto: par_inicio: 2019-06-06T22:00:00-03:00; par_fim:2019-06-06T22:59:59-03:00;");
        }
    }
}
