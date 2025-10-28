package com.malvader.banco.repository;

import com.malvader.banco.models.HistoricoEncerramento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricoEncerramentoRepository extends JpaRepository<HistoricoEncerramento, Integer> {

    List<HistoricoEncerramento> findByContaIdConta(Integer idConta);

    Optional<HistoricoEncerramento> findByContaNumeroConta(String numeroConta);

    List<HistoricoEncerramento> findByMotivoContainingIgnoreCase(String motivo);

    List<HistoricoEncerramento> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    List<HistoricoEncerramento> findByDataHoraBefore(LocalDateTime data);

    List<HistoricoEncerramento> findByDataHoraAfter(LocalDateTime data);

    @Query("SELECT h FROM HistoricoEncerramento h WHERE h.conta.cliente.idCliente = :idCliente")
    List<HistoricoEncerramento> findByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT h FROM HistoricoEncerramento h WHERE h.conta.agencia.idAgencia = :idAgencia")
    List<HistoricoEncerramento> findByAgencia(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT h.motivo, COUNT(h) FROM HistoricoEncerramento h GROUP BY h.motivo ORDER BY COUNT(h) DESC")
    List<Object[]> countEncerramentosPorMotivo();

    @Query("SELECT MONTH(h.dataHora), COUNT(h) FROM HistoricoEncerramento h WHERE YEAR(h.dataHora) = :ano GROUP BY MONTH(h.dataHora) ORDER BY MONTH(h.dataHora)")
    List<Object[]> countEncerramentosPorMes(@Param("ano") int ano);

    @Query("SELECT h.conta.tipoConta, COUNT(h) FROM HistoricoEncerramento h GROUP BY h.conta.tipoConta ORDER BY COUNT(h) DESC")
    List<Object[]> countEncerramentosPorTipoConta();

    @Query("SELECT h.conta.agencia.nome, COUNT(h) FROM HistoricoEncerramento h GROUP BY h.conta.agencia.nome, h.conta.agencia.idAgencia ORDER BY COUNT(h) DESC")
    List<Object[]> countEncerramentosPorAgencia();

    List<HistoricoEncerramento> findAllByOrderByDataHoraDesc();

    @Query("SELECT h FROM HistoricoEncerramento h WHERE h.conta.idConta = :idConta ORDER BY h.dataHora DESC")
    List<HistoricoEncerramento> findHistoricoConta(@Param("idConta") Integer idConta);

    @Query("SELECT h FROM HistoricoEncerramento h JOIN FETCH h.conta c JOIN FETCH c.cliente WHERE h.idHist = :idHist")
    Optional<HistoricoEncerramento> findByIdWithContaAndCliente(@Param("idHist") Integer idHist);

    @Query("SELECT h FROM HistoricoEncerramento h JOIN FETCH h.conta c JOIN FETCH c.cliente cl JOIN FETCH cl.usuario WHERE h.dataHora BETWEEN :inicio AND :fim")
    List<HistoricoEncerramento> findByPeriodoWithCliente(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}