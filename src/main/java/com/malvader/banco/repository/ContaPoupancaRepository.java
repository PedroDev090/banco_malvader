package com.malvader.banco.repository;

import com.malvader.banco.models.ContaPoupanca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContaPoupancaRepository extends JpaRepository<ContaPoupanca, Integer> {

    Optional<ContaPoupanca> findByContaIdConta(Integer idConta);
    Optional<ContaPoupanca> findByContaNumeroConta(String numeroConta);

    List<ContaPoupanca> findByTaxaRendimentoGreaterThanEqual(BigDecimal taxaMinima);
    List<ContaPoupanca> findByTaxaRendimentoLessThanEqual(BigDecimal taxaMaxima);
    List<ContaPoupanca> findByTaxaRendimentoBetween(BigDecimal taxaMin, BigDecimal taxaMax);

    List<ContaPoupanca> findByUltimoRendimentoBefore(LocalDateTime data);
    List<ContaPoupanca> findByUltimoRendimentoAfter(LocalDateTime data);
    List<ContaPoupanca> findByUltimoRendimentoBetween(LocalDateTime inicio, LocalDateTime fim);
    List<ContaPoupanca> findByUltimoRendimentoIsNull();

    @Query("SELECT cp FROM ContaPoupanca cp JOIN cp.conta c WHERE c.agencia.idAgencia = :idAgencia")
    List<ContaPoupanca> findByAgenciaId(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT cp FROM ContaPoupanca cp JOIN cp.conta c WHERE c.cliente.idCliente = :idCliente")
    List<ContaPoupanca> findByClienteId(@Param("idCliente") Integer idCliente);

    @Query("SELECT cp FROM ContaPoupanca cp JOIN cp.conta c JOIN c.cliente cl JOIN cl.usuario u WHERE u.cpf = :cpf")
    Optional<ContaPoupanca> findByCpfCliente(@Param("cpf") String cpf);

    @Query("SELECT AVG(cp.taxaRendimento) FROM ContaPoupanca cp")
    Optional<BigDecimal> findMediaTaxaRendimento();

    @Query("SELECT MAX(cp.taxaRendimento) FROM ContaPoupanca cp")
    Optional<BigDecimal> findMaiorTaxaRendimento();

    @Query("SELECT MIN(cp.taxaRendimento) FROM ContaPoupanca cp")
    Optional<BigDecimal> findMenorTaxaRendimento();

    @Query("SELECT COUNT(cp) FROM ContaPoupanca cp JOIN cp.conta c WHERE c.agencia.idAgencia = :idAgencia")
    Long countByAgencia(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT cp FROM ContaPoupanca cp JOIN cp.conta c WHERE c.status = 'ATIVA' AND cp.ultimoRendimento IS NULL")
    List<ContaPoupanca> findContasAtivasSemRendimento();

    @Query("SELECT cp FROM ContaPoupanca cp JOIN cp.conta c WHERE c.status = 'ATIVA' AND cp.ultimoRendimento < :dataLimite")
    List<ContaPoupanca> findContasAtivasComRendimentoAtrasado(@Param("dataLimite") LocalDateTime dataLimite);

    @Query("SELECT cp FROM ContaPoupanca cp JOIN cp.conta c WHERE c.status = 'ATIVA' AND (c.saldo * cp.taxaRendimento / 100) > :valorMinimoRendimento")
    List<ContaPoupanca> findContasComRendimentoProjetadoMaiorQue(@Param("valorMinimoRendimento") BigDecimal valorMinimoRendimento);

    @Query("SELECT cp.taxaRendimento, COUNT(cp) FROM ContaPoupanca cp GROUP BY cp.taxaRendimento ORDER BY cp.taxaRendimento")
    List<Object[]> countContasPorTaxaRendimento();

    @Query("SELECT MONTH(cp.ultimoRendimento), COUNT(cp) FROM ContaPoupanca cp WHERE cp.ultimoRendimento IS NOT NULL GROUP BY MONTH(cp.ultimoRendimento) ORDER BY MONTH(cp.ultimoRendimento)")
    List<Object[]> countRendimentosPorMes();

    List<ContaPoupanca> findAllByOrderByTaxaRendimentoDesc();
    List<ContaPoupanca> findByOrderByUltimoRendimentoDesc();

    boolean existsByContaIdConta(Integer idConta);

    @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END FROM ContaPoupanca cp JOIN cp.conta c WHERE c.numeroConta = :numeroConta")
    boolean existsByNumeroConta(@Param("numeroConta") String numeroConta);

    @Query("SELECT cp FROM ContaPoupanca cp JOIN FETCH cp.conta c JOIN FETCH c.agencia JOIN FETCH c.cliente WHERE cp.idContaPoupanca = :idContaPoupanca")
    Optional<ContaPoupanca> findByIdWithContaAndAgenciaAndCliente(@Param("idContaPoupanca") Integer idContaPoupanca);

    @Query("SELECT cp FROM ContaPoupanca cp JOIN FETCH cp.conta c WHERE c.cliente.idCliente = :idCliente")
    List<ContaPoupanca> findByClienteWithConta(@Param("idCliente") Integer idCliente);
}