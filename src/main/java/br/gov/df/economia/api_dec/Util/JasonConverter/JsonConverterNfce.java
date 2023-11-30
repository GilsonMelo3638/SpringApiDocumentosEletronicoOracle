package br.gov.df.economia.api_dec.Util.JasonConverter;
import br.gov.df.economia.api_dec.Entity.TbNFCe;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class JsonConverterNfce {
    public String convertListToJson(List<TbNFCe> tbNFCeList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(tbNFCeList);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}