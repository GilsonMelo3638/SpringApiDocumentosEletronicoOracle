package br.gov.df.economia.api_dec.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ArquivoUtil {
    public static boolean criarDiretorioEGravarArquivo(String filePath, String json) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        try {
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                return false;
            }

            ArquivoUtil.escreverArquivo(filePath, json);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void escreverArquivo(String filePath, String content) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(content);
        bufferedWriter.close();
    }


    public static boolean criarArquivo(String filePath, String txtData) {
        try (BufferedWriter writerTxt = new BufferedWriter(new FileWriter(filePath))) {
            writerTxt.write(txtData);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}