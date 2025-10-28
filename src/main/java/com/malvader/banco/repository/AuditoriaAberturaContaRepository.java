package com.malvader.banco.repository;

import com.malvader.banco.models.AuditoriaAberturaConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditoriaAberturaContaRepository extends JpaRepository<AuditoriaAberturaConta, Integer> {

    List<AuditoriaAberturaConta> findByContaIdConta(Integer idConta);

    Optional<AuditoriaAberturaConta> findByContaNumeroConta(String numeroConta);

    List<AuditoriaAberturaConta> findByFuncionarioIdFuncionario(Integer idFuncionario);

    List<AuditoriaAberturaConta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    List<AuditoriaAberturaConta> findByDataHoraBefore(LocalDateTime data);

    List<AuditoriaAberturaConta> findByDataHoraAfter(LocalDateTime data);

    List<AuditoriaAberturaConta> findByObservacaoContainingIgnoreCase(String observacao);

    List<AuditoriaAberturaConta> findByFuncionarioIsNull();

    List<AuditoriaAberturaConta> findByFuncionarioIsNotNull();

    @Query("SELECT a FROM AuditoriaAberturaConta a WHERE a.conta.cliente.idCliente = :idCliente")
    List<AuditoriaAberturaConta> findByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT a FROM AuditoriaAberturaConta a WHERE a.conta.agencia.idAgencia = :idAgencia")
    List<AuditoriaAberturaConta> findByAgencia(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT a FROM AuditoriaAberturaConta a WHERE a.funcionario.agencia.idAgencia = :idAgencia")
    List<AuditoriaAberturaConta> findByAgenciaFuncionario(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT a.funcionario.usuario.nome, COUNT(a) FROM AuditoriaAberturaConta a WHERE a.funcionario IS NOT NULL GROUP BY a.funcionario.usuario.nome, a.funcionario.idFuncionario ORDER BY COUNT(a) DESC")
    List<Object[]> countAuditoriasPorFuncionario();

    @Query("SELECT a.conta.tipoConta, COUNT(a) FROM AuditoriaAberturaConta a GROUP BY a.conta.tipoConta ORDER BY COUNT(a) DESC")
    List<Object[]> countAuditoriasPorTipoConta();

    @Query("SELECT MONTH(a.dataHora), COUNT(a) FROM AuditoriaAberturaConta a WHERE YEAR(a.dataHora) = :ano GROUP BY MONTH(a.dataHora) ORDER BY MONTH(a.dataHora)")
    List<Object[]> countAuditoriasPorMes(@Param("ano") int ano);

    @Query("SELECT a FROM AuditoriaAberturaConta a WHERE a.funcionario.idFuncionario = :idFuncionario AND a.dataHora BETWEEN :inicio AND :fim")
    List<AuditoriaAberturaConta> findByFuncionarioAndPeriodo(@Param("idFuncionario") Integer idFuncionario, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    List<AuditoriaAberturaConta> findAllByOrderByDataHoraDesc();

    @Query("SELECT a FROM AuditoriaAberturaConta a WHERE a.conta.idConta = :idConta ORDER BY a.dataHora DESC")
    List<AuditoriaAberturaConta> findAuditoriasConta(@Param("idConta") Integer idConta);

    @Query("SELECT a FROM AuditoriaAberturaConta a JOIN FETCH a.conta c JOIN FETCH c.cliente WHERE a.idAuditoria = :idAuditoria")
    Optional<AuditoriaAberturaConta> findByIdWithContaAndCliente(@Param("idAuditoria") Integer idAuditoria);

    @Query("SELECT a FROM AuditoriaAberturaConta a JOIN FETCH a.funcionario f JOIN FETCH f.usuario WHERE a.funcionario IS NOT NULL AND a.dataHora BETWEEN :inicio AND :fim")
    List<AuditoriaAberturaConta> findByPeriodoWithFuncionario(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(a) FROM AuditoriaAberturaConta a WHERE a.funcionario.idFuncionario = :idFuncionario")
    Long countByFuncionario(@Param("idFuncionario") Integer idFuncionario);

    @Query("SELECT COUNT(a) FROM AuditoriaAberturaConta a WHERE a.conta.agencia.idAgencia = :idAgencia")
    Long countByAgencia(@Param("idAgencia") Integer idAgencia);
}