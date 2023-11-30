package br.gov.df.economia.api_dec.Repository;

import br.gov.df.economia.api_dec.Dto.Dec.Consultas;
import br.gov.df.economia.api_dec.Entity.TbCTe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


public interface CTeRepository extends JpaRepository<TbCTe, Long> {
    TbCTe findByChave(String chave);
    Optional<TbCTe> findByNsusvd(Long nsusvd);

    List<TbCTe> findByDestinatario(String destinatario);

    List<TbCTe> findByEmitente(String emitente);

    List<TbCTe> findByDestinatarioAndDhprocBetween(String destinatario, Timestamp dhproc1, Timestamp dhproc2);

    List<TbCTe> findByEmitenteAndDhprocBetween(String destinatario, Timestamp dhproc1, Timestamp dhproc2);

    List<TbCTe> findByDhprocBetween(Timestamp dhproc1, Timestamp dhproc2);

    @Query(value = "SELECT * FROM ADMDEC.DEC_DFE_CTE_SVD", nativeQuery = true)
    Consultas selecionarNfe(Long nsusvd, String chave, BigInteger nsuan, String s, Timestamp dhproc, String s1);
}