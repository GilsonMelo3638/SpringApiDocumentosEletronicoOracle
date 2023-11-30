package br.gov.df.economia.api_dec.Repository;

import br.gov.df.economia.api_dec.Dto.Dec.Consultas;
import br.gov.df.economia.api_dec.Entity.TbNFeEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


public interface NFeEventoRepository extends JpaRepository<TbNFeEvento, Long> {
    TbNFeEvento findByChave(String chave);
    Optional<TbNFeEvento> findByNsudf(Long nsudf);
    List<TbNFeEvento> findByDhprocBetween(Timestamp dhproc1, Timestamp dhproc2);
    @Query(value = "SELECT * FROM ADMDEC.DEC_dfe_nfe_evento", nativeQuery = true)
    Consultas selecionarNfe(Long nsudf, String chave, BigInteger nsuan, String s, Timestamp dhproc, String s1);
}