package br.gov.df.economia.api_dec.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileHandler {
    public static <T> void salvarArquivo(List<T> dataList, int arquivoAtual, String baseDirectory) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(dataList);

        String filePath = baseDirectory + "\\" + arquivoAtual + ".json";
        File outputFile = new File(filePath);

        FileWriter fileWriter = new FileWriter(outputFile);
        fileWriter.write(jsonData);
        fileWriter.close();
    }
}
