package br.gov.df.economia.api_dec.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ArquivoXMLGenerator {

    public static boolean salvarArquivoTxt(String filePath, List<String> contentList, String openingTag, String closingTag) {
        try {
            StringBuilder txtData = new StringBuilder();
            txtData.append(openingTag);

            for (String content : contentList) {
                txtData.append(content);
            }

            txtData.append(closingTag);

            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(txtData.toString());
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace(); // Ou registre um erro em logs
            return false;
        }
    }
}
