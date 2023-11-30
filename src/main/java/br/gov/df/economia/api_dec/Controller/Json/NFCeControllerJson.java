package br.gov.df.economia.api_dec.Controller.Json;

import br.gov.df.economia.api_dec.Dto.Dec.Consultas;
import br.gov.df.economia.api_dec.Dto.Dec.DadosNFCe;
import br.gov.df.economia.api_dec.Entity.TbNFCe;
import br.gov.df.economia.api_dec.Repository.DecNFeCancelamento;
import br.gov.df.economia.api_dec.Repository.NFCeRepository;
import br.gov.df.economia.api_dec.Util.*;
import br.gov.df.economia.api_dec.Util.JasonConverter.JsonConverterNfce;
import br.gov.df.economia.api_dec.Util.PrecessarArquivo.ProcessarArquivoJsonNfeNfce;
import br.gov.df.economia.api_dec.Valicacoes.ValidadorDeLimiteExtracao;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/consultar_nfce")
public class NFCeControllerJson {

    // Diretório base padrão
    private final String baseDirectory = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\json\\nfce\\";


    public class AnoMesDiaAtual {
        public static String obterAnoMesDiaAtual() {
            // Obtém a data atual
            Date dataAtual = new Date();

            // Define o formato desejado (yyyyMMdd)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

            // Formata a data atual para o formato desejado e retorna
            return sdf.format(dataAtual);
        }
    }

    @Autowired
    private JsonConverterNfce jsonConverter;

    @Autowired
    private DecNFeCancelamento repositoryCancelamento;

    @Autowired
    private NFCeRepository repositoryNfce;

    @Autowired
    private Consultas consultas;

    @Autowired
    private ProcessarArquivoJsonNfeNfce processarArquivoJson;

    // Endpoint para detalhar uma NFCe por NSU: http://localhost:8080/consultar_nfce/nfce/nsu/657910018
    @GetMapping("/nfce/nsu/{Nsu}")
    public ResponseEntity detalharNfce(@PathVariable Long Nsu) {
        TbNFCe dados = repositoryNfce.getReferenceById(Nsu);
        DadosNFCe dadosNFCe = new DadosNFCe(dados);
        return ResponseEntity.ok(dadosNFCe);
    }

    // Endpoint para buscar uma NFCe por chave: http://localhost:8080/consultar_nfce/nfce/chave/53150701309856000106650010000421809796801590
    @GetMapping(value = "/nfce/chave/{chave}")
    public ResponseEntity<TbNFCe> findByChave(@PathVariable String chave) {
        TbNFCe obj = repositoryNfce.findByChave(chave);
        return ResponseEntity.ok(obj);
    }

    // Endpoint para buscar NFCes por destinatário e intervalo de datas: http://localhost:8080/consultar_nfce/buscarNFCePorDestinatarioEData366Dias?destinatario=25860054149&dhprocIni=2023-10-01%2000:00:00&dhprocfim=2023-10-14%2023:59:59
    @GetMapping("/buscarNFCePorDestinatarioEData366Dias")
    public ResponseEntity<?> buscarNFCePorDestinatarioEData366Dias(
            @RequestParam String destinatario,
            @RequestParam String dhprocIni,
            @RequestParam String dhprocfim
    ) {
        try {
            Timestamp dhproc1 = Timestamp.valueOf(dhprocIni);
            Timestamp dhproc2 = Timestamp.valueOf(dhprocfim);

            int limiteDias = 366; // Limite de 366 dias
            if (!ValidadorDeLimiteExtracao.validarDiferencaDias(dhproc1, dhproc2, limiteDias)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A diferença entre as datas é maior que " + limiteDias + " dias.");
            }

            List<TbNFCe> nFceList = repositoryNfce.findByDestinatarioAndDhprocBetween(destinatario, dhproc1, dhproc2);
            String json = jsonConverter.convertListToJson(nFceList);

            if (json == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao converter para JSON.");
            }

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + destinatario + "\\Destinatario\\" + perIni.substring(0, 6) + "\\";
            String filePath = String.format("%sNFCe%s%s.json", directoryPath, perIni, perFim);

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJson.processarArquivoJson(new File(filePath), "NFCe", "nsu", destinatario, perIni, perFim, "Destinatario");
                return ResponseEntity.ok(nFceList);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint para buscar NFCes por destinatário: http://localhost:8080/consultar_nfce/nfce/destinatario/25860054149
    @GetMapping(value = "/nfce/destinatario/{destinatario}")
    public ResponseEntity<String> findByDestinatario(@PathVariable String destinatario) {
        try {
            List<TbNFCe> objList = repositoryNfce.findByDestinatario(destinatario);

            if (objList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String json = jsonConverter.convertListToJson(objList);

            if (json == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao converter para JSON.");
            }

            String perIni = NFeControllerJson.AnoMesDiaAtual.obterAnoMesDiaAtual();
            String perFim = NFeControllerJson.AnoMesDiaAtual.obterAnoMesDiaAtual();
            String directoryPath = baseDirectory + destinatario + "\\Destinatario\\" + perIni + "\\";
            File directory = new File(directoryPath);

            if (!directory.exists() && !directory.mkdirs()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório.");
            }

            String filePath = String.format(directoryPath + "NFCe" + perIni + perFim + ".json");

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJson.processarArquivoJson(new File(filePath), "NFCe", "nsu", destinatario, perIni, perFim, "Destinatario");
                return ResponseEntity.ok("Arquivo XML gerado com sucesso: " + filePath);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gravar o arquivo JSON.");
        }
    }

    // Endpoint para buscar NFce por emitente e intervalo de datas: http://localhost:8080/consultar_nfce/buscarNFCePorEmitenteEData31Dias?emitente=\72890760012026\&dhprocIni=2023-10-01%2000:00:00&dhprocfim=2023-10-14%2023:59:59
    @GetMapping("/buscarNFCePorEmitenteEData31Dias")
    public ResponseEntity<?> buscarNFCePorEmitenteEData31Dias(
            @RequestParam String emitente,
            @RequestParam String dhprocIni,
            @RequestParam String dhprocfim
    ) {
        try {
            Timestamp dhproc1 = Timestamp.valueOf(dhprocIni);
            Timestamp dhproc2 = Timestamp.valueOf(dhprocfim);

            int limiteDias = 31; // Limite de 31 dias
            if (!ValidadorDeLimiteExtracao.validarDiferencaDias(dhproc1, dhproc2, limiteDias)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A diferença entre as datas é maior que " + limiteDias + " dias.");
            }

            List<TbNFCe> nFceList = repositoryNfce.findByEmitenteAndDhprocBetween(emitente, dhproc1, dhproc2);
            String json = jsonConverter.convertListToJson(nFceList);

            if (json == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao converter para JSON.");
            }

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + emitente + "\\Emitente\\" + perIni.substring(0, 6) + "\\";
            String filePath = String.format("%sNFCe%s%s.json", directoryPath, perIni, perFim);

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJson.processarArquivoJson(new File(filePath), "NFCe", "nsu", emitente, perIni, perFim, "Emitente");
                return ResponseEntity.ok(nFceList);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Método para buscar vários Nsudf e salvar em um arquivo JSON
    @GetMapping("/nfce/nsu")
    //   http://localhost:8080/consultar_nfce/nfce/nsu?NsuList=657909633, 657909762, 657909912, 657910018, 657910186, 657910266, 657910407, 657910482, 657910708, 397191118
    public ResponseEntity<String> detalharNfe(@RequestParam List<Long> NsuList) {
        List<DadosNFCe> dadosNFCeList = new ArrayList<>();

        for (Long Nsu : NsuList) {
            TbNFCe dados = repositoryNfce.getReferenceById(Nsu);
            DadosNFCe dadosNFCe = new DadosNFCe(dados);
            dadosNFCeList.add(dadosNFCe);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonData = objectMapper.writeValueAsString(dadosNFCeList);

            File file = new File("\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\Lista\\dadosNFCe.json");

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonData);
            fileWriter.close();

            // Você também pode retornar o arquivo ou um link para download se desejar
            // return ResponseEntity.ok(file);
            return ResponseEntity.ok("Dados salvos em arquivo: " + file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar os dados em um arquivo.");
        }
    }

    // Método para buscar vários Nsudf e salvar em um arquivo JSON
    @PostMapping("/nfce/nsu/fromfile")   // http://localhost:8080/consultar_nfce/nfce/nsu/fromfile
    // Insomnia: Multpart From, file e caminho do arquivo.
    public ResponseEntity<String> buscarListaNsuEProcessar(@RequestParam("file") MultipartFile file) {
        try {
            // Lê o arquivo para obter a lista de Nsudf
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            List<Long> NsuList = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] nsuArray = line.split(","); // Divide a linha em Nsu com base na vírgula
                for (String nsuStr : nsuArray) {
                    try {
                        Long Nsu = Long.parseLong(nsuStr);
                        NsuList.add(Nsu);
                    } catch (NumberFormatException e) {
                        // Trate qualquer linha inválida ou erro de conversão
                    }
                }
            }

            // Agora você tem a lista completa de Nsudf. Aqui você pode buscar os dados associados a esses Nsudf do seu banco de dados.

            List<DadosNFCe> dadosNFCeList = new ArrayList<>();
            int arquivoAtual = 1; // Contador de arquivos
            int linhasNoArquivo = 0; // Contador de linhas no arquivo atual

            for (Long Nsu : NsuList) {
                try {
                    TbNFCe dados = repositoryNfce.getReferenceById(Nsu);
                    DadosNFCe dadosNFCe = new DadosNFCe(dados);
                    dadosNFCeList.add(dadosNFCe);

                    // Verifica se atingiu o limite de 3 linhas no arquivo atual
                    if (linhasNoArquivo >= 150000) {
                        // Salva o JSON em um novo arquivo
                        FileHandler.salvarArquivo(dadosNFCeList, arquivoAtual, baseDirectory);

                        // Reinicia a lista de dados
                        dadosNFCeList = new ArrayList<>();
                        linhasNoArquivo = 0;
                        arquivoAtual++;
                    } else {
                        linhasNoArquivo++;
                    }
                } catch (EntityNotFoundException ex) {
                    // Trate a exceção para NSUs inexistentes
                    System.out.println("NSU inexistente: " + Nsu);
                }
            }
            // Verifica se há dados restantes a serem salvos em um arquivo
            if (!dadosNFCeList.isEmpty()) {
                FileHandler.salvarArquivo(dadosNFCeList, arquivoAtual, baseDirectory);
            }

            return ResponseEntity.ok("Dados salvos em arquivos." + baseDirectory + "/" + arquivoAtual + ".json" );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo ou salvar os dados.");
        }
    }
}