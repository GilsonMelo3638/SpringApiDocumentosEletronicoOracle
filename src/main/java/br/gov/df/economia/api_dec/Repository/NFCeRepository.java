package br.gov.df.economia.api_dec.Repository;

import br.gov.df.economia.api_dec.Dto.Dec.Consultas;
import br.gov.df.economia.api_dec.Entity.TbNFCe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface NFCeRepository extends JpaRepository<TbNFCe, Long> {
  TbNFCe findByChave(String chave);
  Optional<TbNFCe> findByNsu(Long nsu);
  List<TbNFCe> findByDestinatario(String destinatario);
  List<TbNFCe> findByDestinatarioAndDhprocBetween(String destinatario, Timestamp dhproc1, Timestamp dhproc2);
  List<TbNFCe> findByEmitenteAndDhprocBetween(String emitente, Timestamp dhproc1, Timestamp dhproc2);
  List<TbNFCe> findByDhprocBetween(Timestamp dhproc1, Timestamp dhproc2);
    @Query(value = "SELECT * FROM ADMDEC.DEC_DFE_NFCE", nativeQuery = true)
    Consultas selecionarNFCe(Long nsu, String destinatario, String chave, String s, Timestamp dhproc, String s1);
}

