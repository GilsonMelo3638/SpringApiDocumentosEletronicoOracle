package br.gov.df.economia.api_dec.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeradorDiretorioGravadorArquivo {

    public static boolean criarDiretorioEGravarArquivo(String filePath, String content) {
        File file = new File(filePath);

        // Verifica se o diretório pai do arquivo existe
        File parentDirectory = file.getParentFile();
        if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
            System.err.println("Erro ao criar o diretório: " + parentDirectory.getAbsolutePath());
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao gravar o arquivo: " + filePath);
            e.printStackTrace();
            return false;
        }
    }
}
