package br.gov.df.economia.api_dec.Controller.Xml;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFCe;
import br.gov.df.economia.api_dec.Entity.TbNFCe;
import br.gov.df.economia.api_dec.Repository.NFCeRepository;
import br.gov.df.economia.api_dec.Util.ArquivoUtil;
import br.gov.df.economia.api_dec.Util.ArquivoXMLGenerator;
import br.gov.df.economia.api_dec.Util.FormatadorDataHora;
import br.gov.df.economia.api_dec.Util.GeradorDiretorioGravadorArquivo;
import br.gov.df.economia.api_dec.Valicacoes.ValidadorDeLimiteExtracao;
import br.gov.df.economia.api_dec.service.RetProcServiceNfce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gerarXml_nfce")
public class NFCeControllerXml {

    @Autowired
    private NFCeRepository repositoryNfce;

    private final String baseDirectory = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\xml\\nfce/";

    @GetMapping(value = "/nfce/xmlDestinatario/{destinatario}")
//       http://localhost:8080/gerarXml_nfce/nfce/xmlDestinatario/25860054149
    public ResponseEntity<String> findByDestinatario(@PathVariable String destinatario) throws IOException {
        List<TbNFCe> objList = repositoryNfce.findByDestinatario(destinatario);

        if (objList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        StringBuilder txtData = new StringBuilder();
        txtData.append("<retDistNFCe2><loteDistNFCe>");

        for (TbNFCe nfce : objList) {
            txtData.append(RetProcServiceNfce.montarRetProc(nfce));
        }

        txtData.append("</loteDistNFCe></retDistNFCe2>");
        String filePath = String.format("%s%s/%s/Completo.xml", baseDirectory, destinatario, "Destinatario");

        if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, txtData.toString())) {
            return ResponseEntity.ok("Arquivo XML gerado com sucesso: " + filePath);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
        }
    }

    @GetMapping("/buscarnfcePorDestinatarioEDataXml366Dias/xml")
//   http://localhost:8080/gerarXml_nfce/buscarnfcePorDestinatarioEDataXml366Dias/xml?destinatario=25860054149&dhprocIni=2023-10-01%2000:00:00&dhprocfim=2023-10-14%2023:59:59
    public ResponseEntity<?> buscarnfcePorDestinatarioEDataXml366Dias(
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

            List<TbNFCe> nfCeList = repositoryNfce.findByDestinatarioAndDhprocBetween(destinatario, dhproc1, dhproc2);

            if (nfCeList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            StringBuilder txtData = new StringBuilder();
            txtData.append("<retDistNFCe2><loteDistNFCe>");

            for (TbNFCe nfce : nfCeList) {
                txtData.append(RetProcServiceNfce.montarRetProc(nfce));
            }

            txtData.append("</loteDistNFCe></retDistNFCe2>");

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + destinatario + "/Destinatario/" + perIni.substring(0, 6) + "/";
            String filePath = String.format("%sNFCe%s%s.xml", directoryPath, perIni, perFim);

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, txtData.toString())) {
                return ResponseEntity.ok("Arquivo XML gerado com sucesso: " + filePath);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor.");
        }
    }


    @GetMapping("/buscarnfcePorDataXml24Horas/xml")
//     http://localhost:8080/gerarXml_nfce/buscarnfcePorDataXml24Horas/xml?dhprocIni=2023-10-16%2000:00:00&dhprocfim=2023-10-16%2000:59:59
    public ResponseEntity<?> buscarnfcePorDataXml24Horas(
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

            List<TbNFCe> nFceList = repositoryNfce.findByDhprocBetween(dhproc1, dhproc2);

            if (nFceList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            StringBuilder txtData = new StringBuilder();
            txtData.append("<retDistNFCe2><loteDistNFCe>");

            for (TbNFCe nfce : nFceList) {
                txtData.append(RetProcServiceNfce.montarRetProc(nfce));
            }

            txtData.append("</loteDistNFCe></retDistNFCe2>");

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + perIni.substring(0, 6) + "/";
            String filePath = String.format("%sNFCe%s%s.xml", directoryPath, perIni, perFim);

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

    @PostMapping("/nfce/nsu/fromfile")//        http://localhost:8080/gerarXml_nfce/nfce/nsu/fromfile
    // Obs: Método post: Multpart Form, file, caminho do arquivo
    public ResponseEntity<String> buscarListaNsuEProcessar(@RequestParam("file") MultipartFile file) {
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tamanho do arquivo excede o limite máximo permitido.");
        }
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            List<Long> NsuList = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] nsuArray = line.split(",");
                for (String nsuStr : nsuArray) {
                    try {
                        Long Nsu = Long.parseLong(nsuStr);
                        NsuList.add(Nsu);
                    } catch (NumberFormatException e) {
                        // Trate qualquer linha inválida ou erro de conversão
                    }
                }
            }

            List<DadosNFCe> dadosNFCeList = new ArrayList<>();
            int arquivoAtual = 1;
            int linhasNoArquivo = 0;

            for (Long Nsu : NsuList) {
                Optional<TbNFCe> optionalDados = repositoryNfce.findByNsu(Nsu);

                if (optionalDados.isPresent()) {
                    TbNFCe dados = optionalDados.get();
                    DadosNFCe dadosNFe = new DadosNFCe(dados);
                    dadosNFCeList.add(dadosNFe);

                    if (linhasNoArquivo >= 150000) {
                        salvarArquivoTxt(dadosNFCeList, arquivoAtual);

                        dadosNFCeList = new ArrayList<>();
                        linhasNoArquivo = 0;
                        arquivoAtual++;
                    } else {
                        linhasNoArquivo++;
                    }
                } else {
                    System.out.println("Nsu não encontrado: " + Nsu);
                }
            }


            if (!dadosNFCeList.isEmpty()) {
                salvarArquivoTxt(dadosNFCeList, arquivoAtual);
            }

            return ResponseEntity.ok("Dados salvos em arquivos XML: \\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\Lista\\" + "NFCe" + arquivoAtual + ".xml");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo ou salvar os dados.");
        }
    }

    private void salvarArquivoTxt(List<DadosNFCe> dadosNFCeList, int arquivoAtual) {
        List<String> contentList = new ArrayList<>();

        for (DadosNFCe dadosNFCe : dadosNFCeList) {
            contentList.add(RetProcServiceNfce.montarRetProcDto(dadosNFCe));
        }

        String filePath = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\Lista\\" + "NFCe" + arquivoAtual + ".xml";

        String openingTagNFCe = "<retDistNFCe2>\n  <loteDistNFCe>\n";
        String closingTagNFCe = "  </loteDistNFCe>\n</retDistNFCe2>";

        if (ArquivoXMLGenerator.salvarArquivoTxt(filePath, contentList, openingTagNFCe, closingTagNFCe)) {
            System.out.println("Arquivo NFe salvo com sucessoo em " + filePath);
        } else {
            // Lidar com erros de gravação de arquivo
        }
    }
}