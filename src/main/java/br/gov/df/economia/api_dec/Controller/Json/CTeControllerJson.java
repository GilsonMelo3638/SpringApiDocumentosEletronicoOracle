package br.gov.df.economia.api_dec.Controller.Json;

import br.gov.df.economia.api_dec.Dto.Dec.DadosCTe;
import br.gov.df.economia.api_dec.Entity.TbCTe;
import br.gov.df.economia.api_dec.Repository.CTeRepository;
import br.gov.df.economia.api_dec.Util.*;
import br.gov.df.economia.api_dec.Util.JasonConverter.JsonConverterCte;
import br.gov.df.economia.api_dec.Util.PrecessarArquivo.ProcessarArquivoJsonCte;
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
@RequestMapping("/consultar_cte")
public class CTeControllerJson {

    // Diretório base padrão
    private final String baseDirectory = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\json\\cte\\";

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
    private JsonConverterCte jsonConverter;



    @Autowired
    private CTeRepository repositoryCte;

    @Autowired
    private ProcessarArquivoJsonCte processarArquivoJsonCte;

    // Endpoint para detalhar uma CTe por NSUSVD: http://localhost:8080/consultar_cte/cte/nsusvd/320797953
    @GetMapping("/cte/nsusvd/{Nsusvd}")
    public ResponseEntity detalharCte(@PathVariable Long Nsusvd) {
        TbCTe dados = repositoryCte.getReferenceById(Nsusvd);
        DadosCTe dadosCTe = new DadosCTe(dados);
        return ResponseEntity.ok(dadosCTe);
    }

    // Endpoint para buscar CTe por chave: http://localhost:8080/consultar_cte/cte/chave/42231003007331012077571570336513811733666443
    @GetMapping(value = "/cte/chave/{chave}")
    public ResponseEntity<TbCTe> findByChave(@PathVariable String chave) {
        TbCTe obj = repositoryCte.findByChave(chave);
        return ResponseEntity.ok(obj);
    }

    // Endpoint para buscar CTe por destinatário: http://localhost:8080/consultar_cte/cte/destinatario/25860054149
    @GetMapping(value = "/cte/destinatario/{destinatario}")
    public ResponseEntity<String> findByDestinatario(@PathVariable String destinatario) {
        try {
            List<TbCTe> objList = repositoryCte.findByDestinatario(destinatario);

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

            String filePath = String.format(directoryPath + "CTe" + perIni + perFim + ".json");

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJsonCte.processarArquivoJson(new File(filePath), "CTe", "nsusvd", destinatario, perIni, perFim, "Destinatario");
                return ResponseEntity.ok("Arquivo XML gerado com sucesso: " + filePath);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gravar o arquivo JSON.");
        }
    }

    // Endpoint para buscar CTe por destinatário e intervalo de datas: http://localhost:8080/consultar_cte/buscarCTePorDestinatarioEData366Dias?destinatario=25860054149&dhprocIni=2023-10-01%2000:00:00&dhprocfim=2023-10-14%2023:59:59
    @GetMapping("/buscarCTePorDestinatarioEData366Dias")
    public ResponseEntity<?> buscarCTePorDestinatarioEData360Dias(
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

            List<TbCTe> cTeList = repositoryCte.findByDestinatarioAndDhprocBetween(destinatario, dhproc1, dhproc2);
            String json = jsonConverter.convertListToJson(cTeList);

            if (json == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao converter para JSON.");
            }

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + destinatario + "\\Destinatario\\" + perIni.substring(0, 6) + "\\";
            String filePath = String.format("%sCTe%s%s.json", directoryPath, perIni, perFim);

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJsonCte.processarArquivoJson(new File(filePath), "CTe", "nsusvd", destinatario, perIni, perFim, "Destinatario");
                return ResponseEntity.ok(cTeList);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint para buscar CTe por emitente: http://localhost:8080/consultar_cte/cte/emitente/02964147000470
    @GetMapping(value = "/cte/emitente/{emitente}")
    public ResponseEntity<String> findByEmitente(@PathVariable String emitente) {
        try {
            List<TbCTe> objList = repositoryCte.findByEmitente(emitente);

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

            String filePath = String.format(directoryPath + "CTe" + perIni + perFim + ".json");

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJsonCte.processarArquivoJson(new File(filePath), "CTe", "nsusvd", emitente, perIni, perFim, "Emitente");
                return ResponseEntity.ok("Arquivo XML gerado com sucesso: " + filePath);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gravar o arquivo JSON.");
        }
    }

    // Endpoint para buscar CTe por emitente e intervalo de datas: http://localhost:8080/consultar_cte/buscarCTePorEmitenteEData366Dias?emitente=03608196000270&dhprocIni=2023-10-01%2000:00:00&dhprocfim=2023-10-29%2013:59:59
    @GetMapping("/buscarCTePorEmitenteEData366Dias")
    public ResponseEntity<?> buscarCTePorEmitenteEData366Dias(
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

            List<TbCTe> cTeList = repositoryCte.findByEmitenteAndDhprocBetween(emitente, dhproc1, dhproc2);
            String json = jsonConverter.convertListToJson(cTeList);

            if (json == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao converter para JSON.");
            }

            String perIni = FormatadorDataHora.formatarDataHora(dhproc1);
            String perFim = FormatadorDataHora.formatarDataHora(dhproc2);
            String directoryPath = baseDirectory + emitente + "\\Emitente\\" + perIni.substring(0, 6) + "\\";
            String filePath = String.format("%sCTe%s%s.json", directoryPath, perIni, perFim);

            if (GeradorDiretorioGravadorArquivo.criarDiretorioEGravarArquivo(filePath, json)) {
                processarArquivoJsonCte.processarArquivoJson(new File(filePath), "CTe", "nsusvd", emitente, perIni, perFim, "Emitente");
                return ResponseEntity.ok(cTeList);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar diretório ou gravar o arquivo.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Método para buscar vários Nsudf e salvar em um arquivo JSON
    @GetMapping("/cte/nsusvd")
    //   http://localhost:8080/consultar_cte/cte/nsusvd?NsusvdList=320797955,320797956,320797957,320797959,320797962,320797963,320797964
    public ResponseEntity<String> detalharCte(@RequestParam List<Long> NsusvdList) {
        List<DadosCTe> dadosCTeList = new ArrayList<>();

        for (Long Nsusvd : NsusvdList) {
            TbCTe dados = repositoryCte.getReferenceById(Nsusvd);
            DadosCTe dadosCTe = new DadosCTe(dados);
            dadosCTeList.add(dadosCTe);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonData = objectMapper.writeValueAsString(dadosCTeList);

            File file = new File("\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\Lista\\dadosCTe.json");

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

    @PostMapping("/cte/nsusvd/fromfile")   // http://localhost:8080/consultar_cte/cte/nsusvd/fromfile
    // Insomnia: Multpart From, file e caminho do arquivo.
    public ResponseEntity<String> buscarListaNsusvdEProcessar(@RequestParam("file") MultipartFile file) {
        try {
            // Lê o arquivo para obter a lista de Nsudf
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            List<Long> NsusvdList = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] nsusvdArray = line.split(","); // Divide a linha em Nsudf com base na vírgula
                for (String nsusvdStr : nsusvdArray) {
                    try {
                        Long Nsusvd = Long.parseLong(nsusvdStr);
                        NsusvdList.add(Nsusvd);
                    } catch (NumberFormatException e) {
                        // Trate qualquer linha inválida ou erro de conversão
                    }
                }
            }

            // Agora você tem a lista completa de Nsudf. Aqui você pode buscar os dados associados a esses Nsudf do seu banco de dados.

            List<DadosCTe> dadosCTeList = new ArrayList<>();
            int arquivoAtual = 1; // Contador de arquivos
            int linhasNoArquivo = 0; // Contador de linhas no arquivo atual

            for (Long Nsudf : NsusvdList) {
                try {
                    TbCTe dados = repositoryCte.getReferenceById(Nsudf);
                    DadosCTe dadosCTe = new DadosCTe(dados);
                    dadosCTeList.add(dadosCTe);

                    // Verifica se atingiu o limite de 3 linhas no arquivo atual
                    if (linhasNoArquivo >= 150000) {
                        // Salva o JSON em um novo arquivo
                        FileHandler.salvarArquivo(dadosCTeList, arquivoAtual, baseDirectory);

                        // Reinicia a lista de dados
                        dadosCTeList = new ArrayList<>();
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
            if (!dadosCTeList.isEmpty()) {
                FileHandler.salvarArquivo(dadosCTeList, arquivoAtual, baseDirectory);
            }
            return ResponseEntity.ok("Dados salvos em arquivos." + baseDirectory + arquivoAtual + ".json");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo ou salvar os dados.");
        }
    }

    // Método para salvar dados em um arquivo
}
