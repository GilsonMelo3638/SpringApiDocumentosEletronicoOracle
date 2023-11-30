package br.gov.df.economia.api_dec.Controller.Json;

import br.gov.df.economia.api_dec.Dto.Dec.DadosNFe;
import br.gov.df.economia.api_dec.Entity.TbNFe;
import br.gov.df.economia.api_dec.Repository.DecNFeCancelamento;
import br.gov.df.economia.api_dec.Repository.NFeRepository;
import br.gov.df.economia.api_dec.Util.*;
import br.gov.df.economia.api_dec.Util.JasonConverter.JsonConverterNfe;
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
@RequestMapping("/consultar_nfe")
public class NFeControllerJson {

    // Diretório base padrão
    private final String baseDirectory = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\json\\nfe\\";

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
    private JsonConverterNfe jsonConverter;

    @Autowired
    private DecNFeCancelamento repositoryCancelamento;

    @Autowired
    private NFeRepository repositoryNfe;

    @Autowired
    private ProcessarArquivoJsonNfeNfce processarArquivoJson;

    // Endpoint para detalhar uma NFe por NSU: http://localhost:8080/consultar_nfe/nfe/nsudf/561099533
    @GetMapping("/nfe/nsudf/{Nsudf}")
    public ResponseEntity detalharNfe(@PathVariable Long Nsudf) {
        TbNFe dados = repositoryNfe.getReferenceById(Nsudf);
        DadosNFe dadosNFe = new DadosNFe(dados);
        return ResponseEntity.ok(dadosNFe);
    }

    // Endpoint para buscar NFe por chave: http://localhost:8080/consultar_nfe/nfe/chave/35190123443122000158550010000095231186026301
    @GetMapping(value = "/nfe/chave/{chave}")
    public ResponseEntity<TbNFe> findByChave(@PathVariable String chave) {
        TbNFe obj = repositoryNfe.findByChave(chave);
        return ResponseEntity.ok(obj);
    }

    // Endpoint para buscar NFe por destinatário e intervalo de datas: http://localhost:8080/consultar_nfe/buscarNFePorDestinatarioEData366Dias?destinatario=25860054149&dhprocIni=2023-10-01%2000:00:00&dhprocfim=2023-10-14%2023:59:59
    @GetMapping("/buscarNFePorDestinatarioEData366Dias")
    public ResponseEntity<?> buscarNFePorDestinatarioEData360Dias(
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

            List<TbNFe> nFeList = repositoryNfe.findByDestinatarioAndDhprocBetween(destinatario, dhproc1, dhproc2);
            String json = jsonConverter.convertListToJson(nFeList);

            if (json == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao converter para JSON.");
            }

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + destinatario + "\\Destinatario\\" + perIni.substring(0, 6) + "\\";
            String filePath = String.format("%sNFe%s%s.json", directoryPath, perIni, perFim);

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJson.processarArquivoJson(new File(filePath), "NFe", "nsudf", destinatario, perIni, perFim, "Destinatario");
                return ResponseEntity.ok(nFeList);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint para buscar NFe por destinatário: http://localhost:8080/consultar_nfe/nfe/destinatario/25860054149
    @GetMapping(value = "/nfe/destinatario/{destinatario}")
    public ResponseEntity<String> findByDestinatario(@PathVariable String destinatario) {
        try {
            List<TbNFe> objList = repositoryNfe.findByDestinatario(destinatario);

            if (objList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String json = jsonConverter.convertListToJson(objList);

            if (json == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao converter para JSON.");
            }

            String perIni = AnoMesDiaAtual.obterAnoMesDiaAtual();
            String perFim = AnoMesDiaAtual.obterAnoMesDiaAtual();
            String directoryPath = baseDirectory + destinatario + "\\Destinatario\\" + perIni + "\\";
            File directory = new File(directoryPath);

            if (!directory.exists() && !directory.mkdirs()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório.");
            }

            String filePath = String.format(directoryPath + "NFe" + perIni + perFim + ".json");

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJson.processarArquivoJson(new File(filePath), "NFe", "nsudf", destinatario, perIni, perFim, "Destinatario");
                return ResponseEntity.ok("Arquivo XML gerado com sucesso: " + filePath);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gravar o arquivo JSON.");
        }
    }


    // Endpoint para buscar NFe por emitente: http://localhost:8080/consultar_nfe/nfe/emitente/01612795000151
    @GetMapping(value = "/nfe/emitente/{emitente}")
    public ResponseEntity<String> findByEmitente(@PathVariable String emitente) {
        try {
            List<TbNFe> objList = repositoryNfe.findByEmitente(emitente);

            if (objList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String json = jsonConverter.convertListToJson(objList);

            if (json == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao converter para JSON.");
            }

            String perIni = AnoMesDiaAtual.obterAnoMesDiaAtual();
            String perFim = AnoMesDiaAtual.obterAnoMesDiaAtual();
            String directoryPath = baseDirectory + emitente + "\\Emitente\\" + perIni + "\\";
            File directory = new File(directoryPath);

            if (!directory.exists() && !directory.mkdirs()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório.");
            }

            String filePath = String.format(directoryPath + "NFe" + perIni + perFim + ".json");

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJson.processarArquivoJson(new File(filePath), "NFe", "nsudf", emitente, perIni, perFim, "Emitente");
                return ResponseEntity.ok("Arquivo XML gerado com sucesso: " + filePath);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gravar o arquivo JSON.");
        }
    }

    // Endpoint para buscar NFe por emitente e intervalo de datas: http://localhost:8080/consultar_nfe/buscarNFePorEmitenteEData366Dias?emitente=01612795000151&dhprocIni=2023-10-01%2000:00:00&dhprocfim=2023-10-14%2023:59:59
    @GetMapping("/buscarNFePorEmitenteEData366Dias")
    public ResponseEntity<?> buscarNFePorEmitenteEData366Dias(
            @RequestParam String emitente,
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

            List<TbNFe> nFeList = repositoryNfe.findByEmitenteAndDhprocBetween(emitente, dhproc1, dhproc2);
            String json = jsonConverter.convertListToJson(nFeList);

            if (json == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao converter para JSON.");
            }

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + emitente + "\\Emitente\\" + perIni.substring(0, 6) + "\\";
            String filePath = String.format("%sNFe%s%s.json", directoryPath, perIni, perFim);

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJson.processarArquivoJson(new File(filePath), "NFe", "nsudf", emitente, perIni, perFim, "Emitente");
                return ResponseEntity.ok(nFeList);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Método para buscar vários Nsudf e salvar em um arquivo JSON
    @GetMapping("/nfe/nsudf")
    //   http://localhost:8080/consultar_nfe/nfe/nsudf?NsudfList=9468519,9470311,9476181,9479067,9485440,9490126,9494218
    public ResponseEntity<String> detalharNfe(@RequestParam List<Long> NsudfList) {
        List<DadosNFe> dadosNFeList = new ArrayList<>();

        for (Long Nsudf : NsudfList) {
            TbNFe dados = repositoryNfe.getReferenceById(Nsudf);
            DadosNFe dadosNFe = new DadosNFe(dados);
            dadosNFeList.add(dadosNFe);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonData = objectMapper.writeValueAsString(dadosNFeList);

            File file = new File("\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\Lista\\dadosNFe.json");

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

    @PostMapping("/nfe/nsudf/fromfile")   // http://localhost:8080/consultar_nfe/nfe/nsudf/fromfile
    // Insomnia: Multpart From, file e caminho do arquivo.
    public ResponseEntity<String> buscarListaNsudfEProcessar(@RequestParam("file") MultipartFile file) {
        try {
            // Lê o arquivo para obter a lista de Nsudf
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            List<Long> NsudfList = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] nsudfArray = line.split(","); // Divide a linha em Nsudf com base na vírgula
                for (String nsudfStr : nsudfArray) {
                    try {
                        Long Nsudf = Long.parseLong(nsudfStr);
                        NsudfList.add(Nsudf);
                    } catch (NumberFormatException e) {
                        // Trate qualquer linha inválida ou erro de conversão
                    }
                }
            }

            // Agora você tem a lista completa de Nsudf. Aqui você pode buscar os dados associados a esses Nsudf do seu banco de dados.

            List<DadosNFe> dadosNFeList = new ArrayList<>();
            int arquivoAtual = 1; // Contador de arquivos
            int linhasNoArquivo = 0; // Contador de linhas no arquivo atual

            for (Long Nsudf : NsudfList) {
                try {
                    TbNFe dados = repositoryNfe.getReferenceById(Nsudf);
                    DadosNFe dadosNFe = new DadosNFe(dados);
                    dadosNFeList.add(dadosNFe);

                    // Verifica se atingiu o limite de 3 linhas no arquivo atual
                    if (linhasNoArquivo >= 150000) {
                        // Salva o JSON em um novo arquivo
                        FileHandler.salvarArquivo(dadosNFeList, arquivoAtual, baseDirectory);

                        // Reinicia a lista de dados
                        dadosNFeList = new ArrayList<>();
                        linhasNoArquivo = 0;
                        arquivoAtual++;
                    } else {
                        linhasNoArquivo++;
                    }
                } catch (EntityNotFoundException ex) {
                    // Trate a exceção para NSUs inexistentes
                    System.out.println("NSUDF inexistente: " + Nsudf);
                }
            }

            // Verifica se há dados restantes a serem salvos em um arquivo
            if (!dadosNFeList.isEmpty()) {
                FileHandler.salvarArquivo(dadosNFeList, arquivoAtual, baseDirectory);
            }
            return ResponseEntity.ok("Dados salvos em arquivos." + baseDirectory + "/" + arquivoAtual + ".json");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo ou salvar os dados.");
        }
    }

    // Método para salvar dados em um arquivo
}
