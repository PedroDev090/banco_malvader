package com.malvader.banco.repository;

import com.malvader.banco.models.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Integer> {

    // 🔍 BUSCAS POR CONTA
    Optional<ContaCorrente> findByContaIdConta(Integer idConta);
    Optional<ContaCorrente> findByContaNumeroConta(String numeroConta);

    // 💰 BUSCAS POR LIMITE
    List<ContaCorrente> findByLimiteGreaterThanEqual(BigDecimal limiteMinimo);
    List<ContaCorrente> findByLimiteLessThanEqual(BigDecimal limiteMaximo);
    List<ContaCorrente> findByLimiteBetween(BigDecimal limiteMin, BigDecimal limiteMax);

    // 📅 BUSCAS POR DATA DE VENCIMENTO
    List<ContaCorrente> findByDataVencimento(LocalDate dataVencimento);
    List<ContaCorrente> findByDataVencimentoBefore(LocalDate data);
    List<ContaCorrente> findByDataVencimentoAfter(LocalDate data);
    List<ContaCorrente> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);

    // 💸 BUSCAS POR TAXA DE MANUTENÇÃO
    List<ContaCorrente> findByTaxaManutencaoGreaterThan(BigDecimal taxaMinima);
    List<ContaCorrente> findByTaxaManutencaoLessThan(BigDecimal taxaMaxima);
    List<ContaCorrente> findByTaxaManutencaoBetween(BigDecimal taxaMin, BigDecimal taxaMax);

    // 🏦 BUSCAS POR AGÊNCIA
    @Query("SELECT cc FROM ContaCorrente cc JOIN cc.conta c WHERE c.agencia.idAgencia = :idAgencia")
    List<ContaCorrente> findByAgenciaId(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT cc FROM ContaCorrente cc JOIN cc.conta c WHERE c.agencia.codigoAgencia = :codigoAgencia")
    List<ContaCorrente> findByCodigoAgencia(@Param("codigoAgencia") String codigoAgencia);

    // 👤 BUSCAS POR CLIENTE
    @Query("SELECT cc FROM ContaCorrente cc JOIN cc.conta c WHERE c.cliente.idCliente = :idCliente")
    List<ContaCorrente> findByClienteId(@Param("idCliente") Integer idCliente);

    @Query("SELECT cc FROM ContaCorrente cc JOIN cc.conta c JOIN c.cliente cl JOIN cl.usuario u WHERE u.cpf = :cpf")
    Optional<ContaCorrente> findByCpfCliente(@Param("cpf") String cpf);

    // 📊 CONSULTAS ESTATÍSTICAS
    @Query("SELECT AVG(cc.limite) FROM ContaCorrente cc")
    Optional<BigDecimal> findMediaLimite();

    @Query("SELECT MAX(cc.limite) FROM ContaCorrente cc")
    Optional<BigDecimal> findMaiorLimite();

    @Query("SELECT MIN(cc.limite) FROM ContaCorrente cc")
    Optional<BigDecimal> findMenorLimite();

    @Query("SELECT AVG(cc.taxaManutencao) FROM ContaCorrente cc")
    Optional<BigDecimal> findMediaTaxaManutencao();

    @Query("SELECT COUNT(cc) FROM ContaCorrente cc JOIN cc.conta c WHERE c.agencia.idAgencia = :idAgencia")
    Long countByAgencia(@Param("idAgencia") Integer idAgencia);

    // 🎯 BUSCAS COMBINADAS
    @Query("SELECT cc FROM ContaCorrente cc WHERE cc.limite >= :limite AND cc.dataVencimento > :dataAtual")
    List<ContaCorrente> findContasComLimiteValido(@Param("limite") BigDecimal limite, @Param("dataAtual") LocalDate dataAtual);

    @Query("SELECT cc FROM ContaCorrente cc JOIN cc.conta c WHERE c.status = 'ATIVA' AND cc.dataVencimento < :dataAtual")
    List<ContaCorrente> findContasVencidas(@Param("dataAtual") LocalDate dataAtual);

    @Query("SELECT cc FROM ContaCorrente cc JOIN cc.conta c WHERE c.status = 'ATIVA' AND cc.dataVencimento BETWEEN :inicio AND :fim")
    List<ContaCorrente> findContasVencendoNoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // 💳 BUSCAS PARA OPERAÇÕES BANCÁRIAS
    @Query("SELECT cc FROM ContaCorrente cc JOIN cc.conta c WHERE c.status = 'ATIVA' AND (c.saldo + cc.limite) >= :valor")
    List<ContaCorrente> findContasComSaldoSuficiente(@Param("valor") BigDecimal valor);

    @Query("SELECT cc FROM ContaCorrente cc JOIN cc.conta c WHERE c.status = 'ATIVA' AND c.saldo < 0")
    List<ContaCorrente> findContasNoChequeEspecial();

    @Query("SELECT cc FROM ContaCorrente cc JOIN cc.conta c WHERE c.status = 'ATIVA' AND (c.saldo + cc.limite) < 0")
    List<ContaCorrente> findContasComLimiteEstourado();

    // 📈 RELATÓRIOS E ANÁLISES
    @Query("SELECT cc.limite, COUNT(cc) FROM ContaCorrente cc GROUP BY cc.limite ORDER BY cc.limite")
    List<Object[]> countContasPorFaixaLimite();

    @Query("SELECT cc.taxaManutencao, COUNT(cc) FROM ContaCorrente cc GROUP BY cc.taxaManutencao ORDER BY cc.taxaManutencao")
    List<Object[]> countContasPorTaxaManutencao();

    @Query("SELECT MONTH(cc.dataVencimento), COUNT(cc) FROM ContaCorrente cc GROUP BY MONTH(cc.dataVencimento) ORDER BY MONTH(cc.dataVencimento)")
    List<Object[]> countContasPorMesVencimento();

    // 🔄 ORDENAÇÕES
    List<ContaCorrente> findAllByOrderByLimiteDesc();
    List<ContaCorrente> findByOrderByTaxaManutencaoAsc();
    List<ContaCorrente> findByOrderByDataVencimentoAsc();

    // ✅ VERIFICAÇÕES
    boolean existsByContaIdConta(Integer idConta);

    @Query("SELECT CASE WHEN COUNT(cc) > 0 THEN true ELSE false END FROM ContaCorrente cc JOIN cc.conta c WHERE c.numeroConta = :numeroConta")
    boolean existsByNumeroConta(@Param("numeroConta") String numeroConta);

    // 🚀 BUSCAS DE PERFORMANCE
    @Query("SELECT cc FROM ContaCorrente cc JOIN FETCH cc.conta c JOIN FETCH c.agencia JOIN FETCH c.cliente WHERE cc.idContaCorrente = :idContaCorrente")
    Optional<ContaCorrente> findByIdWithContaAndAgenciaAndCliente(@Param("idContaCorrente") Integer idContaCorrente);

    @Query("SELECT cc FROM ContaCorrente cc JOIN FETCH cc.conta c WHERE c.cliente.idCliente = :idCliente")
    List<ContaCorrente> findByClienteWithConta(@Param("idCliente") Integer idCliente);
}