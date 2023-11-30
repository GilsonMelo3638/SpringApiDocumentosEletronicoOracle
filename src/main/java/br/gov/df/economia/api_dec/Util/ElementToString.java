package br.gov.df.economia.api_dec.Util;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class ElementToString {

    public static String elementToString(org.w3c.dom.Element element) {
        StringWriter stringWriter = new StringWriter();
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(element), new StreamResult(stringWriter));
        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    public static void main(String[] args) {
        // Exemplo de uso
        org.w3c.dom.Element exampleElement = null /* Seu elemento XML */;
        String xmlString = elementToString(exampleElement);
        System.out.println(xmlString);
    }
}