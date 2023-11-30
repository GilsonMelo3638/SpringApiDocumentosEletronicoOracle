package br.gov.df.economia.api_dec.Util.PrecessarArquivo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// Anotação que marca esta classe como um serviço Spring
@Service
public class ProcessarArquivoJsonCte {

    private final String baseDirectory = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\json\\";

    // Método para processar um arquivo JSON
    public void processarArquivoJson(File file, String arquivo, String nsuKey, String destinatario, String perIni, String perFim, String tipoEmissao) throws IOException {
        // Verifica e cria um diretório se ele não existir
        criarDiretorioSeNaoExistir(destinatario);

        String diretorioBase = baseDirectory + "\\" + arquivo + "\\" + File.separator + destinatario;

// Inicializa os escritores de arquivos CSV usando try-with-resources para garantir o fechamento adequado
        try (
                BufferedWriter writerInfCTe = new BufferedWriter(new FileWriter(diretorioBase + File.separator + "Inf" + arquivo + ".csv"));
                BufferedWriter writerDet = new BufferedWriter(new FileWriter(diretorioBase + File.separator + "InfNfe" + ".csv"))
        ) {
            // Resto do seu código aqui...

            // Escreve cabeçalhos nos arquivos CSV
            writerInfCTe.write("nsusvd, Id, CPF, Destinatário, fone, CNPJ, Emitente, xLgr, xCpl\n");
            writerDet.write("nsusvd, Id, chave\n");

            // Conjunto para rastrear chaves únicas do infNFe
            Set<String> uniqueInfCTeKeys = new HashSet<>();

            // Carrega o arquivo JSON
            File jsonFile = new File(baseDirectory + "\\" + arquivo + "\\" + destinatario + "\\" + tipoEmissao + "\\" + perIni.substring(0, 6) + "\\" + arquivo + perIni + perFim + ".json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonArray = objectMapper.readTree(jsonFile);

            // Verifica se o JSON é uma matriz
            if (jsonArray.isArray()) {
                Iterator<JsonNode> elements = jsonArray.elements();

                while (elements.hasNext()) {
                    JsonNode item = elements.next();
                    JsonNode infDFeNode = item.get("infDFe");
                    JsonNode infDFeChave = item.get("chave");
                    JsonNode nsusvd = item.get(nsuKey);

                    if (infDFeNode != null && !uniqueInfCTeKeys.contains(infDFeChave.asText())) {
                        uniqueInfCTeKeys.add(infDFeChave.asText());

                        // Obtém o conteúdo da tag infDFe como uma string
                        String infDFeContent = infDFeNode.asText();
                        System.out.println(infDFeChave + ":");

                        // Cria um analisador XML
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document document = builder.parse(new InputSource(new StringReader(infDFeContent)));

                        // Extrai elementos XML
                        Element infCteElement = (Element) document.getElementsByTagName("infCte").item(0);
                        Element emitElement = (Element) document.getElementsByTagName("emit").item(0);
                        Element destElement = (Element) document.getElementsByTagName("dest").item(0);
                        Element enderDestElement = (Element) infCteElement.getElementsByTagName("enderDest").item(0);

                        // Obtém valores de tags XML com fallback para um valor padrão
                        String foneValue = getTagValue(destElement, "fone", "");
                        String xLgrValue = getTagValue(enderDestElement, "xLgr", "");
                        String xCplValue = getTagValue(enderDestElement, "xCpl", "");
                        String emitxNomeValue = getTagValue(emitElement, "xNome", "");
                        String cnpjValue = getTagValue(emitElement, "CNPJ", "");
                        String destxNomeValue = getTagValue(destElement, "xNome", "");
                        String cpfValue = getTagValue(destElement, "CPF", "");

                        // Escreve os valores no arquivo CSV InfCTe
                        writerInfCTe.write(
                                nsusvd + ", " +
                                        infDFeChave + ", " +
                                        cpfValue + ", " +
                                        destxNomeValue + ", " +
                                        foneValue + ", " +
                                        cnpjValue + ", " +
                                        emitxNomeValue + ", " +
                                        xLgrValue + ", " +
                                        xCplValue + ",\n"
                        );

                        // Extrai elementos da tag "det"
                        NodeList cteList = infCteElement.getElementsByTagName("infNFe");
                        for (int i = 0; i < cteList.getLength(); i++) {
                            Element infNfeElement = (Element) cteList.item(i);
                            String chave = getTagValue(infNfeElement,"chave", "");

                            // Imprime informações na saída padrão
                            System.out.println(chave);

                            // Escreve os valores no arquivo CSV Det
                            writerDet.write(
                                    nsusvd + ", " +
                                        infDFeChave + ", " +
                                            chave + "\n"
                            );
                        }
                        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                    }
                }
            } else {
                System.out.println("O arquivo JSON não contém um array.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erro durante o processamento do arquivo.", e);
        }
    }

    // Método auxiliar para obter o valor de uma tag XML
    private String getTagValue(Element element, String tagName, String defaultValue) {
        if (element != null) {
            Element tagElement = (Element) element.getElementsByTagName(tagName).item(0);
            if (tagElement != null) {
                return tagElement.getTextContent();
            }
        }
        return defaultValue;
    }

    // Método auxiliar para criar um diretório se ele não existir
    private void criarDiretorioSeNaoExistir(String destinatario) throws IOException {
        String diretorioBase = "\\\\svmcifs\\ExtracaoXML\\ExtracaoApi\\json\\cte\\";
        String diretorioCompleto = diretorioBase + File.separator + destinatario;
        File diretorioCompletoFile = new File(diretorioCompleto);
        if (!diretorioCompletoFile.exists() && !diretorioCompletoFile.mkdirs()) {
            throw new IOException("Falha ao criar o diretório.");
        }
    }
}