package com.malvader.banco.repository;

import com.malvader.banco.models.Conta;
import com.malvader.banco.models.TipoConta;
import com.malvader.banco.models.StatusConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Integer> {

    // Buscar conta por número
    Optional<Conta> findByNumeroConta(String numeroConta);

    // Buscar contas por cliente
    List<Conta> findByClienteIdCliente(Integer idCliente);

    // Buscar contas por agência
    List<Conta> findByAgenciaIdAgencia(Integer idAgencia);

    // Buscar contas por tipo
    List<Conta> findByTipoConta(TipoConta tipoConta);

    // Buscar contas por status
    List<Conta> findByStatus(StatusConta status);

    // Buscar contas ativas por cliente
    List<Conta> findByClienteIdClienteAndStatus(Integer idCliente, StatusConta status);

    // Buscar conta com transações (otimizado)
    @Query("SELECT c FROM Conta c LEFT JOIN FETCH c.transacoesOrigem WHERE c.idConta = :idConta")
    Optional<Conta> findByIdWithTransacoes(@Param("idConta") Integer idConta);

    // Buscar conta com todas as relações (para detalhes)
    @Query("SELECT c FROM Conta c " +
            "LEFT JOIN FETCH c.cliente cl " +
            "LEFT JOIN FETCH cl.usuario " +
            "LEFT JOIN FETCH c.agencia a " +
            "LEFT JOIN FETCH a.endereco " +
            "WHERE c.idConta = :idConta")
    Optional<Conta> findByIdCompleta(@Param("idConta") Integer idConta);

    // Verificar se número de conta existe
    boolean existsByNumeroConta(String numeroConta);

    // Buscar contas com saldo negativo (inadimplentes)
    @Query("SELECT c FROM Conta c WHERE c.saldo < 0 AND c.status = 'ATIVA'")
    List<Conta> findContasComSaldoNegativo();

    // Buscar contas por faixa de saldo
    @Query("SELECT c FROM Conta c WHERE c.saldo BETWEEN :minimo AND :maximo")
    List<Conta> findBySaldoBetween(@Param("minimo") Double minimo, @Param("maximo") Double maximo);

    // Contar total de contas por tipo e status
    @Query("SELECT COUNT(c) FROM Conta c WHERE c.tipoConta = :tipo AND c.status = :status")
    Long countByTipoAndStatus(@Param("tipo") TipoConta tipo, @Param("status") StatusConta status);

    // Buscar contas para relatório de movimentação
    @Query("SELECT c FROM Conta c WHERE c.dataAbertura BETWEEN :dataInicio AND :dataFim")
    List<Conta> findContasAbertasNoPeriodo(@Param("dataInicio") String dataInicio, @Param("dataFim") String dataFim);

    // No ContaRepository - ADICIONE:

    @Query("SELECT c FROM Conta c WHERE c.numeroConta = :numeroConta AND c.cliente.idCliente = :idCliente")
    Optional<Conta> findByNumeroContaAndClienteIdCliente(@Param("numeroConta") String numeroConta,
                                                         @Param("idCliente") Integer idCliente);

    @Query("SELECT COUNT(c) > 0 FROM Conta c WHERE c.numeroConta = :numeroConta AND c.cliente.idCliente = :idCliente")
    boolean existsByNumeroContaAndClienteIdCliente(@Param("numeroConta") String numeroConta,
                                                   @Param("idCliente") Integer idCliente);
}