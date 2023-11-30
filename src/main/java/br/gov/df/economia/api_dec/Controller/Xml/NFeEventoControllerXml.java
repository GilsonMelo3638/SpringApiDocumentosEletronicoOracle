package br.gov.df.economia.api_dec.Controller.Xml;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFeEvento;
import br.gov.df.economia.api_dec.Entity.TbNFeEvento;
import br.gov.df.economia.api_dec.Repository.NFeEventoRepository;
import br.gov.df.economia.api_dec.Util.ArquivoUtil;
import br.gov.df.economia.api_dec.Util.ArquivoXMLGenerator;
import br.gov.df.economia.api_dec.Util.FormatadorDataHora;
import br.gov.df.economia.api_dec.Valicacoes.ValidadorDeLimiteExtracao;
import br.gov.df.economia.api_dec.service.RetProcServiceNfeEvento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gerarXml_nfe_evento")
public class NFeEventoControllerXml {

    @Autowired
    private NFeEventoRepository repositoryNfeEvento;

    private final String baseDirectory = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\xml\\nfe\\evento/";

    @GetMapping("/buscarNFeEventoPorDataXml24horas/xml")//   http://localhost:8080/gerarXml_nfe_evento/buscarNFeEventoPorDataXml24horas/xml?dhprocIni=2023-10-16%2000:00:00&dhprocfim=2023-10-16%2000:59:59
    public ResponseEntity<?> buscarNFeEventoPorDataXml24horas(
            @RequestParam String dhprocIni,
            @RequestParam String dhprocfim
    ) {
        try {
            Timestamp dhproc1 = Timestamp.valueOf(dhprocIni);
            Timestamp dhproc2 = Timestamp.valueOf(dhprocfim);

            int limiteDias = 1;
            if (!ValidadorDeLimiteExtracao.validarDiferencaDias(dhproc1, dhproc2, limiteDias)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A diferença entre as datas é maior que " + limiteDias + " dias.");
            }

            List<TbNFeEvento> nFeEventoList = repositoryNfeEvento.findByDhprocBetween(dhproc1, dhproc2);

            if (nFeEventoList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            StringBuilder txtData = new StringBuilder();
            txtData.append("<retDistNFe2><loteDistNFe>");

            for (TbNFeEvento nfeEvento : nFeEventoList) {
                txtData.append(RetProcServiceNfeEvento.montarRetProc(nfeEvento));
            }

            txtData.append("</loteDistNFe></retDistNFe2>");

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + perIni.substring(0, 6) + "/";
            String filePath = String.format("%sNFe%s%s.xml", directoryPath, perIni, perFim);

            Path directory = Paths.get(directoryPath);
            Files.createDirectories(directory);

            if (ArquivoUtil.criarArquivo(filePath, txtData.toString())) {
                return ResponseEntity.ok("Arquivo XML gerado com sucesso: " + filePath);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor.");
        }
    }
    @PostMapping("/nfeEvento/nsudf/fromfile")//        http://localhost:8080/gerarXml_nfe_evento/nfeEvento/nsudf/fromfile
    // Obs: Método post: Multpart Form, file, caminho do arquivo
    public ResponseEntity<String> buscarListaNsudfEProcessar(@RequestParam("file") MultipartFile file) {
        // Verificar se o tamanho do arquivo está dentro dos limites
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tamanho do arquivo excede o limite máximo permitido.");
        }
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            List<Long> NsudfList = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] nsudfArray = line.split(",");
                for (String nsudfStr : nsudfArray) {
                    try {
                        Long Nsudf = Long.parseLong(nsudfStr);
                        NsudfList.add(Nsudf);
                    } catch (NumberFormatException e) {
                        // Trate qualquer linha inválida ou erro de conversão
                    }
                }
            }

            List<DadosNFeEvento> dadosNFeList = new ArrayList<>();
            int arquivoAtual = 1;
            int linhasNoArquivo = 0;

            for (Long Nsudf : NsudfList) {
                Optional<TbNFeEvento> optionalDados = repositoryNfeEvento.findByNsudf(Nsudf);

                if (optionalDados.isPresent()) {
                    TbNFeEvento dados = optionalDados.get();
                    DadosNFeEvento dadosNFe = new DadosNFeEvento(dados);
                    dadosNFeList.add(dadosNFe);

                    if (linhasNoArquivo >= 150000) {
                        salvarArquivoTxt(dadosNFeList, arquivoAtual);

                        dadosNFeList = new ArrayList<>();
                        linhasNoArquivo = 0;
                        arquivoAtual++;
                    } else {
                        linhasNoArquivo++;
                    }
                } else {
                    System.out.println("Nsudf não encontrado: " + Nsudf);
                }
            }
            if (!dadosNFeList.isEmpty()) {
                salvarArquivoTxt(dadosNFeList, arquivoAtual);
            }
            return ResponseEntity.ok("Dados salvos em arquivos XML: \\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\Lista\\" + "NFe" + arquivoAtual + ".xml");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo ou salvar os dados.");
        }
    }

    private void salvarArquivoTxt(List<DadosNFeEvento> dadosNFeList, int arquivoAtual) {
        List<String> contentList = new ArrayList<>();

        for (DadosNFeEvento dadosNFeEvento : dadosNFeList) {
            contentList.add(RetProcServiceNfeEvento.montarRetProcDto(dadosNFeEvento));
        }

        String filePath = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\Lista\\" + "NFeEvento" + arquivoAtual + ".xml";

        String openingTagNFe = "<retDistNFe2>\n  <loteDistNFe>\n";
        String closingTagNFe = "  </loteDistNFe>\n</retDistNFe2>";

        if (ArquivoXMLGenerator.salvarArquivoTxt(filePath, contentList, openingTagNFe, closingTagNFe)) {
            System.out.println("Arquivo NFe salvo com sucessoo em " + filePath);
        } else {
            // Lidar com erros de gravação de arquivo
        }
    }
}
