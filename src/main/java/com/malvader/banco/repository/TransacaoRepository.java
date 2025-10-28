package com.malvader.banco.repository;

import com.malvader.banco.models.Transacao;
import com.malvader.banco.models.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {

    List<Transacao> findByContaOrigemIdConta(Integer idContaOrigem);
    List<Transacao> findByContaDestinoIdConta(Integer idContaDestino);

    List<Transacao> findByTipoTransacao(TipoTransacao tipoTransacao);
    List<Transacao> findByTipoTransacaoIn(List<TipoTransacao> tipos);

    List<Transacao> findByValorGreaterThanEqual(BigDecimal valorMinimo);
    List<Transacao> findByValorLessThanEqual(BigDecimal valorMaximo);
    List<Transacao> findByValorBetween(BigDecimal valorMin, BigDecimal valorMax);

    List<Transacao> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Transacao> findByDataHoraBefore(LocalDateTime data);
    List<Transacao> findByDataHoraAfter(LocalDateTime data);

    List<Transacao> findByDescricaoContainingIgnoreCase(String descricao);

    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem.idConta = :idConta OR t.contaDestino.idConta = :idConta")
    List<Transacao> findByContaEnvolvida(@Param("idConta") Integer idConta);

    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem.numeroConta = :numeroConta OR t.contaDestino.numeroConta = :numeroConta")
    List<Transacao> findByNumeroContaEnvolvida(@Param("numeroConta") String numeroConta);

    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem.cliente.idCliente = :idCliente OR t.contaDestino.cliente.idCliente = :idCliente")
    List<Transacao> findByClienteEnvolvido(@Param("idCliente") Integer idCliente);

    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem.agencia.idAgencia = :idAgencia OR t.contaDestino.agencia.idAgencia = :idAgencia")
    List<Transacao> findByAgenciaEnvolvida(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipoTransacao = :tipo AND t.dataHora BETWEEN :inicio AND :fim")
    Optional<BigDecimal> sumValorByTipoAndPeriodo(@Param("tipo") TipoTransacao tipo, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(t) FROM Transacao t WHERE t.tipoTransacao = :tipo AND t.dataHora BETWEEN :inicio AND :fim")
    Long countByTipoAndPeriodo(@Param("tipo") TipoTransacao tipo, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT t.tipoTransacao, SUM(t.valor) FROM Transacao t WHERE t.dataHora BETWEEN :inicio AND :fim GROUP BY t.tipoTransacao")
    List<Object[]> sumValorPorTipoNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT t.tipoTransacao, COUNT(t) FROM Transacao t WHERE t.dataHora BETWEEN :inicio AND :fim GROUP BY t.tipoTransacao")
    List<Object[]> countTransacoesPorTipoNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT DATE(t.dataHora), COUNT(t) FROM Transacao t WHERE t.dataHora BETWEEN :inicio AND :fim GROUP BY DATE(t.dataHora) ORDER BY DATE(t.dataHora)")
    List<Object[]> countTransacoesPorDia(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT MONTH(t.dataHora), COUNT(t) FROM Transacao t WHERE YEAR(t.dataHora) = :ano GROUP BY MONTH(t.dataHora) ORDER BY MONTH(t.dataHora)")
    List<Object[]> countTransacoesPorMes(@Param("ano") int ano);

    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem IS NULL AND t.tipoTransacao = 'DEPOSITO'")
    List<Transacao> findDepositosSemOrigem();

    @Query("SELECT t FROM Transacao t WHERE t.contaDestino IS NULL AND t.tipoTransacao = 'SAQUE'")
    List<Transacao> findSaquesSemDestino();

    @Query("SELECT t FROM Transacao t WHERE t.valor > :valorLimite ORDER BY t.valor DESC")
    List<Transacao> findTransacoesAcimaValor(@Param("valorLimite") BigDecimal valorLimite);

    @Query("SELECT t.contaOrigem.agencia.nome, SUM(t.valor) FROM Transacao t WHERE t.tipoTransacao = 'TRANSFERENCIA' AND t.dataHora BETWEEN :inicio AND :fim GROUP BY t.contaOrigem.agencia.nome, t.contaOrigem.agencia.idAgencia")
    List<Object[]> sumTransferenciasPorAgenciaOrigem(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT t.contaDestino.agencia.nome, SUM(t.valor) FROM Transacao t WHERE t.tipoTransacao = 'TRANSFERENCIA' AND t.dataHora BETWEEN :inicio AND :fim GROUP BY t.contaDestino.agencia.nome, t.contaDestino.agencia.idAgencia")
    List<Object[]> sumTransferenciasPorAgenciaDestino(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    List<Transacao> findAllByOrderByDataHoraDesc();
    List<Transacao> findByOrderByValorDesc();

    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem.idConta = :idConta ORDER BY t.dataHora DESC")
    List<Transacao> findUltimasTransacoesOrigem(@Param("idConta") Integer idConta);

    @Query("SELECT t FROM Transacao t WHERE t.contaDestino.idConta = :idConta ORDER BY t.dataHora DESC")
    List<Transacao> findUltimasTransacoesDestino(@Param("idConta") Integer idConta);

    @Query("SELECT t FROM Transacao t JOIN FETCH t.contaOrigem JOIN FETCH t.contaDestino WHERE t.idTransacao = :idTransacao")
    Optional<Transacao> findByIdWithContas(@Param("idTransacao") Integer idTransacao);

    @Query("SELECT t FROM Transacao t JOIN FETCH t.contaOrigem co JOIN FETCH t.contaDestino cd JOIN FETCH co.cliente JOIN FETCH cd.cliente WHERE t.dataHora BETWEEN :inicio AND :fim")
    List<Transacao> findByPeriodoWithClientes(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}