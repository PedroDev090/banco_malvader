package com.malvader.banco.repository;

import com.malvader.banco.models.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Integer> {

    List<Relatorio> findByFuncionarioIdFuncionario(Integer idFuncionario);

    List<Relatorio> findByTipoRelatorio(String tipoRelatorio);

    List<Relatorio> findByTipoRelatorioContainingIgnoreCase(String tipoRelatorio);

    List<Relatorio> findByDataGeracaoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Relatorio> findByDataGeracaoBefore(LocalDateTime data);

    List<Relatorio> findByDataGeracaoAfter(LocalDateTime data);

    @Query("SELECT r FROM Relatorio r WHERE r.funcionario.usuario.nome LIKE %:nomeFuncionario%")
    List<Relatorio> findByNomeFuncionario(@Param("nomeFuncionario") String nomeFuncionario);

    @Query("SELECT r FROM Relatorio r WHERE r.funcionario.agencia.idAgencia = :idAgencia")
    List<Relatorio> findByAgencia(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT r FROM Relatorio r WHERE r.funcionario.cargo = 'GERENTE'")
    List<Relatorio> findByGerentes();

    @Query("SELECT r.tipoRelatorio, COUNT(r) FROM Relatorio r GROUP BY r.tipoRelatorio ORDER BY COUNT(r) DESC")
    List<Object[]> countRelatoriosPorTipo();

    @Query("SELECT r.funcionario.usuario.nome, COUNT(r) FROM Relatorio r GROUP BY r.funcionario.usuario.nome, r.funcionario.idFuncionario ORDER BY COUNT(r) DESC")
    List<Object[]> countRelatoriosPorFuncionario();

    @Query("SELECT r.funcionario.agencia.nome, COUNT(r) FROM Relatorio r GROUP BY r.funcionario.agencia.nome, r.funcionario.agencia.idAgencia ORDER BY COUNT(r) DESC")
    List<Object[]> countRelatoriosPorAgencia();

    @Query("SELECT MONTH(r.dataGeracao), COUNT(r) FROM Relatorio r WHERE YEAR(r.dataGeracao) = :ano GROUP BY MONTH(r.dataGeracao) ORDER BY MONTH(r.dataGeracao)")
    List<Object[]> countRelatoriosPorMes(@Param("ano") int ano);

    @Query("SELECT r FROM Relatorio r WHERE r.dataGeracao = (SELECT MAX(r2.dataGeracao) FROM Relatorio r2 WHERE r2.tipoRelatorio = r.tipoRelatorio)")
    List<Relatorio> findUltimosRelatoriosPorTipo();

    @Query("SELECT r FROM Relatorio r WHERE r.conteudo LIKE %:termo%")
    List<Relatorio> findByConteudoContaining(@Param("termo") String termo);

    @Query("SELECT r FROM Relatorio r WHERE r.funcionario.idFuncionario = :idFuncionario AND r.dataGeracao BETWEEN :inicio AND :fim")
    List<Relatorio> findByFuncionarioAndPeriodo(@Param("idFuncionario") Integer idFuncionario, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT r FROM Relatorio r WHERE r.tipoRelatorio = :tipo AND r.dataGeracao BETWEEN :inicio AND :fim")
    List<Relatorio> findByTipoAndPeriodo(@Param("tipo") String tipo, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    List<Relatorio> findAllByOrderByDataGeracaoDesc();

    List<Relatorio> findByTipoRelatorioOrderByDataGeracaoDesc(String tipoRelatorio);

    @Query("SELECT r FROM Relatorio r WHERE r.funcionario.idFuncionario = :idFuncionario ORDER BY r.dataGeracao DESC")
    List<Relatorio> findUltimosRelatoriosFuncionario(@Param("idFuncionario") Integer idFuncionario);

    @Query("SELECT DISTINCT r.tipoRelatorio FROM Relatorio r ORDER BY r.tipoRelatorio")
    List<String> findTiposRelatorioDistintos();

    @Query("SELECT r FROM Relatorio r JOIN FETCH r.funcionario f JOIN FETCH f.usuario WHERE r.idRelatorio = :idRelatorio")
    Optional<Relatorio> findByIdWithFuncionario(@Param("idRelatorio") Integer idRelatorio);

    @Query("SELECT r FROM Relatorio r JOIN FETCH r.funcionario f JOIN FETCH f.usuario JOIN FETCH f.agencia WHERE r.dataGeracao BETWEEN :inicio AND :fim")
    List<Relatorio> findByPeriodoWithFuncionarioAndAgencia(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(r) FROM Relatorio r WHERE r.funcionario.idFuncionario = :idFuncionario")
    Long countByFuncionario(@Param("idFuncionario") Integer idFuncionario);

    @Query("SELECT COUNT(r) FROM Relatorio r WHERE r.funcionario.agencia.idAgencia = :idAgencia")
    Long countByAgencia(@Param("idAgencia") Integer idAgencia);
}