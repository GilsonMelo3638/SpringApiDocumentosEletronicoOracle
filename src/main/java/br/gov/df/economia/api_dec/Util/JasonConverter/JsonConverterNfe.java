package br.gov.df.economia.api_dec.Util.JasonConverter;
import br.gov.df.economia.api_dec.Entity.TbNFe;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class JsonConverterNfe {
    public String convertListToJson(List<TbNFe> tbNFeList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(tbNFeList);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}