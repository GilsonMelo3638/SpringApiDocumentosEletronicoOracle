package br.gov.df.economia.api_dec.Repository;

import br.gov.df.economia.api_dec.Dto.Dec.Consultas;
import br.gov.df.economia.api_dec.Entity.TbNFe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


public interface NFeRepository extends JpaRepository<TbNFe, Long> {
    TbNFe findByChave(String chave);
    Optional<TbNFe> findByNsudf(Long nsudf);

    List<TbNFe> findByDestinatario(String destinatario);

    List<TbNFe> findByEmitente(String emitente);

    List<TbNFe> findByDestinatarioAndDhprocBetween(String destinatario, Timestamp dhproc1, Timestamp dhproc2);

    List<TbNFe> findByEmitenteAndDhprocBetween(String destinatario, Timestamp dhproc1, Timestamp dhproc2);

    List<TbNFe> findByDhprocBetween(Timestamp dhproc1, Timestamp dhproc2);

    @Query(value = "SELECT * FROM ADMDEC.DEC_dfe_nfe", nativeQuery = true)
    Consultas selecionarNfe(Long nsudf, String chave, BigInteger nsuan, String s, Timestamp dhproc, String s1);
}