package br.gov.df.economia.api_dec.Controller;

import br.gov.df.economia.api_dec.Dto.Agenda.*;
import br.gov.df.economia.api_dec.Entity.TbAgenda;
import br.gov.df.economia.api_dec.Repository.DecAgenda;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/agendar_por_hora")
public class AgendaController {
    @Autowired
    private DecAgenda repository;
    @Autowired
    private AgendamentoPorPeriodo agenda;

    @PostMapping //  * {"tipo_doc": "NFe", "par_inicio": "2023-06-04T00:00:00-03:00", "par_fim":    "2023-06-04T00:59:59-03:00", "ind_situacao": "AGENDADO"}
    @Transactional
    public ResponseEntity agendar_por_hora(@RequestBody @Valid DadosAgendamentoPorHora dados, UriComponentsBuilder uriBuilder) {
        agenda.agendar(dados);
        var porhora = new TbAgenda(dados);
        repository.save(porhora);
        var uri = uriBuilder.path("/agendar_por_hora/{cod_agenda_extracao}").buildAndExpand(porhora.getCod_agenda_extracao()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoPorHora(porhora));
    }
    @GetMapping //http://localhost:8080/agendar_por_hora
    public ResponseEntity <Page<DadosListagemAgendamento>> listar(@PageableDefault(size = 10, sort = "quantidade") Pageable paginacao) {
        var page = repository.findAll(paginacao).map(DadosListagemAgendamento::new);
        return ResponseEntity.ok(page);
    }
    @PutMapping //http://localhost:8080/agendar_por_hora {"cod_agenda_extracao": "697148", "quantidade": "0"}
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoPorHora dados) {
        var porhora = repository.getReferenceById(dados.cod_agenda_extracao());
        porhora.atualizarInformacoes(dados);
        return ResponseEntity.ok(new DadosDetalhamentoPorHora(porhora));
    }

    @DeleteMapping("/{cod_agenda_extracao}") //http://localhost:8080/agendar_por_hora/697148
    @Transactional
    public ResponseEntity exclusaoLogica(@PathVariable Long cod_agenda_extracao) {
        var porhora = repository.getReferenceById(cod_agenda_extracao);
        porhora.exclusaoLogica();
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{cod_agenda_extracao}") //http://localhost:8080/agendar_por_hora/697148

    public ResponseEntity detalhar(@PathVariable Long cod_agenda_extracao) {
        var porhora = repository.getReferenceById(cod_agenda_extracao);
        return ResponseEntity.ok(new DadosDetalhamentoPorHora(porhora));
    }
}
