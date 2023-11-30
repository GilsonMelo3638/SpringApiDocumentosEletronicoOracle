package br.gov.df.economia.api_dec.Controller.Xml;

import br.gov.df.economia.api_dec.Dto.Dec.DadosCTe;
import br.gov.df.economia.api_dec.Entity.TbCTe;
import br.gov.df.economia.api_dec.Repository.CTeRepository;
import br.gov.df.economia.api_dec.Util.ArquivoUtil;
import br.gov.df.economia.api_dec.Util.ArquivoXMLGenerator;
import br.gov.df.economia.api_dec.Util.FormatadorDataHora;
import br.gov.df.economia.api_dec.Util.GeradorDiretorioGravadorArquivo;
import br.gov.df.economia.api_dec.Valicacoes.ValidadorDeLimiteExtracao;
import br.gov.df.economia.api_dec.service.RetProcServiceCte;
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
@RequestMapping("/gerarXml_cte")
public class CTeControllerXml {

    @Autowired
    private CTeRepository repositoryCte;

    private final String baseDirectory = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\xml\\cte/";

    @GetMapping(value = "/cte/xmlDestinatario/{destinatario}")//     http://localhost:8080/gerarXml_cte/cte/xmlDestinatario/25860054149
    public ResponseEntity<String> findByDestinatario(@PathVariable String destinatario) throws IOException {
        List<TbCTe> objList = repositoryCte.findByDestinatario(destinatario);

        if (objList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        StringBuilder txtData = new StringBuilder();
        txtData.append("<retDistCTe2><loteDistCTe>");

        for (TbCTe cte : objList) {
            txtData.append(RetProcServiceCte.montarRetProc(cte));
        }

        txtData.append("</loteDistNFCe></retDistNFCe2>");

        String filePath = String.format("%s%s/%s/Completo.xml", baseDirectory, destinatario, "Destinatario");

        if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, txtData.toString())) {
            return ResponseEntity.ok("Arquivo XML gerado com sucesso: " + filePath);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
        }
    }

    @GetMapping("/buscarCTePorDestinatarioEDataXml366Dias/xml")//     http://localhost:8080/gerarXml_cte/buscarCTePorDestinatarioEDataXml366Dias/xml?destinatario=25860054149&dhprocIni=2023-10-01%2000:00:00&dhprocfim=2023-10-14%2023:59:59
    public ResponseEntity<?> buscarCTePorDestinatarioEDataXml366Dias(
            @RequestParam String destinatario,
            @RequestParam String dhprocIni,
            @RequestParam String dhprocfim
    ) {
        try {
            Timestamp dhproc1 = Timestamp.valueOf(dhprocIni);
            Timestamp dhproc2 = Timestamp.valueOf(dhprocfim);

            int limiteDias = 366;
            if (!ValidadorDeLimiteExtracao.validarDiferencaDias(dhproc1, dhproc2, limiteDias)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A diferença entre as datas é maior que " + limiteDias + " dias.");
            }

            List<TbCTe> nFeList = repositoryCte.findByDestinatarioAndDhprocBetween(destinatario, dhproc1, dhproc2);

            if (nFeList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            StringBuilder txtData = new StringBuilder();
            txtData.append("<retDistCTe2><loteDistCTe>");

            for (TbCTe cte : nFeList) {
                txtData.append(RetProcServiceCte.montarRetProc(cte));
            }

            txtData.append("</loteDistNFCe></retDistNFCe2>");

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + destinatario + "/Destinatario/" + perIni.substring(0, 6) + "/";
            String filePath = String.format("%sCTe%s%s.xml", directoryPath, perIni, perFim);

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, txtData.toString())) {
                return ResponseEntity.ok("Arquivo XML gerado com sucesso: " + filePath);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor.");
        }
    }
    @GetMapping("/buscarCTePorDataXml24horas/xml")//   http://localhost:8080/gerarXml_cte/buscarCTePorDataXml24horas/xml?dhprocIni=2023-10-16%2000:00:00&dhprocfim=2023-10-16%2000:59:59
    public ResponseEntity<?> buscarCTePorDataXml24horas(
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

            List<TbCTe> nFeList = repositoryCte.findByDhprocBetween(dhproc1, dhproc2);

            if (nFeList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            StringBuilder txtData = new StringBuilder();
            txtData.append("<retDistCTe2><loteDistCTe>");

            for (TbCTe cte : nFeList) {
                txtData.append(RetProcServiceCte.montarRetProc(cte));
            }

            txtData.append("</loteDistNFCe></retDistNFCe2>");

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + perIni.substring(0, 6) + "/";
            String filePath = String.format("%sCTe%s%s.xml", directoryPath, perIni, perFim);

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
    @PostMapping("/cte/nsusvd/fromfile")//        http://localhost:8080/gerarXml_cte/cte/nsusvd/fromfile
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

            List<DadosCTe> dadosCTeList = new ArrayList<>();
            int arquivoAtual = 1;
            int linhasNoArquivo = 0;

            for (Long Nsudf : NsudfList) {
                Optional<TbCTe> optionalDados = repositoryCte.findByNsusvd(Nsudf);

                if (optionalDados.isPresent()) {
                    TbCTe dados = optionalDados.get();
                    DadosCTe dadosCTe = new DadosCTe(dados);
                    dadosCTeList.add(dadosCTe);

                    if (linhasNoArquivo >= 150000) {
                        salvarArquivoTxt(dadosCTeList, arquivoAtual);

                        dadosCTeList = new ArrayList<>();
                        linhasNoArquivo = 0;
                        arquivoAtual++;
                    } else {
                        linhasNoArquivo++;
                    }
                } else {
                    System.out.println("Nsudf não encontrado: " + Nsudf);
                }
            }
            if (!dadosCTeList.isEmpty()) {
                salvarArquivoTxt(dadosCTeList, arquivoAtual);
            }
            return ResponseEntity.ok("Dados salvos em arquivos XML: \\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\Lista\\" + "CTe" + arquivoAtual + ".xml");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo ou salvar os dados.");
        }
    }

    private void salvarArquivoTxt(List<DadosCTe> dadosCTeList, int arquivoAtual) {
        List<String> contentList = new ArrayList<>();

        for (DadosCTe dadosCTe : dadosCTeList) {
            contentList.add(RetProcServiceCte.montarRetProcDto(dadosCTe));
        }

        String filePath = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\Lista\\" + "CTe" + arquivoAtual + ".xml";

        String openingTagCTe = "<retDistCTe2>\n  <loteDistCTe>\n";
        String closingTagCTe = "  </loteDistCTe>\n</retDistCTe2>";

        if (ArquivoXMLGenerator.salvarArquivoTxt(filePath, contentList, openingTagCTe, closingTagCTe)) {
            System.out.println("Arquivo CTe salvo com sucessoo em " + filePath);
        } else {
            // Lidar com erros de gravação de arquivo
        }
    }
}
