package com.malvader.banco.repository;

import com.malvader.banco.models.Transacao;
import com.malvader.banco.models.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {

    // Buscar transações por conta de origem
    List<Transacao> findByContaOrigemIdConta(Integer idContaOrigem);

    // Buscar transações por conta de destino
    List<Transacao> findByContaDestinoIdConta(Integer idContaDestino);

    // Buscar transações por tipo
    List<Transacao> findByTipoTransacao(TipoTransacao tipoTransacao);

    // Buscar transações por período
    List<Transacao> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // Buscar transações de uma conta por período
    @Query("SELECT t FROM Transacao t WHERE " +
            "(t.contaOrigem.idConta = :idConta OR t.contaDestino.idConta = :idConta) " +
            "AND t.dataHora BETWEEN :inicio AND :fim " +
            "ORDER BY t.dataHora DESC")
    List<Transacao> findExtratoPorPeriodo(@Param("idConta") Integer idConta,
                                          @Param("inicio") LocalDateTime inicio,
                                          @Param("fim") LocalDateTime fim);

    // Buscar últimas transações de uma conta (para dashboard)
    @Query("SELECT t FROM Transacao t WHERE " +
            "(t.contaOrigem.idConta = :idConta OR t.contaDestino.idConta = :idConta) " +
            "ORDER BY t.dataHora DESC LIMIT :limite")
    List<Transacao> findUltimasTransacoes(@Param("idConta") Integer idConta,
                                          @Param("limite") int limite);

    // Calcular saldo total movimentado por período
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t WHERE " +
            "t.dataHora BETWEEN :inicio AND :fim AND t.tipoTransacao = :tipo")
    Double calcularTotalMovimentadoPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                              @Param("fim") LocalDateTime fim,
                                              @Param("tipo") TipoTransacao tipo);

    // Buscar transações com detalhes completos (para relatórios)
    @Query("SELECT t FROM Transacao t " +
            "LEFT JOIN FETCH t.contaOrigem co " +
            "LEFT JOIN FETCH t.contaDestino cd " +
            "LEFT JOIN FETCH co.cliente cliOrigem " +
            "LEFT JOIN FETCH cd.cliente cliDestino " +
            "WHERE t.dataHora BETWEEN :inicio AND :fim")
    List<Transacao> findTransacoesComDetalhes(@Param("inicio") LocalDateTime inicio,
                                              @Param("fim") LocalDateTime fim);

    // Buscar transações suspeitas (valores altos)
    @Query("SELECT t FROM Transacao t WHERE t.valor > :valorMinimo " +
            "AND t.dataHora BETWEEN :inicio AND :fim " +
            "ORDER BY t.valor DESC")
    List<Transacao> findTransacoesSuspeitas(@Param("valorMinimo") Double valorMinimo,
                                            @Param("inicio") LocalDateTime inicio,
                                            @Param("fim") LocalDateTime fim);

    // Contar transações por tipo e período
    @Query("SELECT COUNT(t) FROM Transacao t WHERE " +
            "t.tipoTransacao = :tipo AND t.dataHora BETWEEN :inicio AND :fim")
    Long countByTipoAndPeriodo(@Param("tipo") TipoTransacao tipo,
                               @Param("inicio") LocalDateTime inicio,
                               @Param("fim") LocalDateTime fim);
}