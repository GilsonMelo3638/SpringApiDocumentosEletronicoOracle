package br.gov.df.economia.api_dec.Dto.Dec;

import br.gov.df.economia.api_dec.Repository.NFeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NFe {

    @Autowired
    private NFeRepository repositoryNfe;
}