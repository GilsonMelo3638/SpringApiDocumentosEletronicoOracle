package br.gov.df.economia.api_dec.Dto.Dec;

import br.gov.df.economia.api_dec.Entity.TbNFCe;
import br.gov.df.economia.api_dec.Entity.TbNFe;
import br.gov.df.economia.api_dec.Repository.DecNFeCancelamento;
import br.gov.df.economia.api_dec.Repository.NFCeRepository;
import br.gov.df.economia.api_dec.Repository.NFeRepository;
import br.gov.df.economia.api_dec.Valicacoes.ValidadorDeConsultas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Consultas {
    @Autowired
    private NFeRepository repositoryNFe;
    @Autowired
    private NFCeRepository repositoryNFCe;
    @Autowired
    private DecNFeCancelamento repositoryNFeCancelamento;
    @Autowired
    private List<ValidadorDeConsultas> validadores;

    public void consultar (DadosNFe dados) {
        var selecionar = selecionarDocumento(dados);
        var par_inicio = dados.dhproc();
        var par_fim = dados.dhproc().toLocalDateTime().plusHours(1);
        var id = dados.Chave();
        var consulta = new TbNFe(null, null, null, null, null, null, null, null);
        validadores.forEach(v -> v.validar(dados));

    }
    private Consultas selecionarDocumento(DadosNFe dados) {

    return  repositoryNFe.selecionarNfe(dados.Nsudf(), dados.Chave(), dados.Nsuan(), dados.IpTransmissor(), dados.dhproc(), dados.infDFe());
    }

   public void consultarNFCe (DadosNFCe dados) {
        var selecionar = selecionarDocumentoNFCe(dados);
        var par_inicio = dados.dhproc();
        var par_fim = dados.dhproc().toLocalDateTime().plusHours(1);
        var id = dados.Chave();
        var consulta = new TbNFCe(null, null, null, null, null, null,null);
         }
    private Consultas selecionarDocumentoNFCe(DadosNFCe dados) {

        return  repositoryNFCe.selecionarNFCe(dados.nsu(), dados.destinatario(), dados.Chave(), dados.IpTransmissor(), dados.dhproc(), dados.infDFe());
    }
}