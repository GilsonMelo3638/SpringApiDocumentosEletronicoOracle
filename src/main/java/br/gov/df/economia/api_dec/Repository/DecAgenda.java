package br.gov.df.economia.api_dec.Repository;
import br.gov.df.economia.api_dec.Entity.TbAgenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecAgenda extends JpaRepository<TbAgenda, Long> {

}
