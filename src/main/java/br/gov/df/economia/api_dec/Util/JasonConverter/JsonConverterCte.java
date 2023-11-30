package br.gov.df.economia.api_dec.Util.JasonConverter;

import br.gov.df.economia.api_dec.Entity.TbCTe;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class JsonConverterCte {
    public String convertListToJson(List<TbCTe> tbCTeList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(tbCTeList);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}