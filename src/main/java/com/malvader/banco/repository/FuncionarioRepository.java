package com.malvader.banco.repository;

import com.malvader.banco.models.Funcionario;
import com.malvader.banco.models.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {


    Optional<Funcionario> findByCodigoFuncionario(String codigoFuncionario);
    boolean existsByCodigoFuncionario(String codigoFuncionario);


    List<Funcionario> findByCargo(Cargo cargo);
    List<Funcionario> findByCargoIn(List<Cargo> cargos);


    List<Funcionario> findByAgenciaIdAgencia(Integer idAgencia);
    List<Funcionario> findByAgenciaCodigoAgencia(String codigoAgencia);


    List<Funcionario> findBySupervisorIdFuncionario(Integer idSupervisor);
    List<Funcionario> findBySupervisorIsNull();
    List<Funcionario> findBySupervisorIsNotNull();


    Optional<Funcionario> findByUsuarioIdUsuario(Integer idUsuario);
    Optional<Funcionario> findByUsuarioCpf(String cpf);

    @Query("SELECT f FROM Funcionario f WHERE f.usuario.nome LIKE %:nome%")
    List<Funcionario> findByNomeUsuario(@Param("nome") String nome);


    @Query("SELECT COUNT(f) FROM Funcionario f WHERE f.agencia.idAgencia = :idAgencia")
    Long countByAgencia(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT f.cargo, COUNT(f) FROM Funcionario f GROUP BY f.cargo")
    List<Object[]> countFuncionariosPorCargo();

    @Query("SELECT a.nome, COUNT(f) FROM Funcionario f JOIN f.agencia a GROUP BY a.nome, a.idAgencia")
    List<Object[]> countFuncionariosPorAgencia();

    @Query("SELECT f.cargo, a.nome, COUNT(f) FROM Funcionario f JOIN f.agencia a GROUP BY f.cargo, a.nome, a.idAgencia")
    List<Object[]> countFuncionariosPorCargoEAgencia();


    List<Funcionario> findByCargoAndAgenciaIdAgencia(Cargo cargo, Integer idAgencia);

    @Query("SELECT f FROM Funcionario f WHERE f.cargo = :cargo AND f.agencia.codigoAgencia = :codigoAgencia")
    List<Funcionario> findByCargoAndCodigoAgencia(@Param("cargo") Cargo cargo, @Param("codigoAgencia") String codigoAgencia);


    @Query("SELECT f FROM Funcionario f WHERE f.supervisor.idFuncionario = :idSupervisor AND f.cargo = :cargo")
    List<Funcionario> findSupervisionadosPorCargo(@Param("idSupervisor") Integer idSupervisor, @Param("cargo") Cargo cargo);

    @Query("SELECT f FROM Funcionario f WHERE f.supervisor IS NULL AND f.cargo = 'GERENTE'")
    List<Funcionario> findGerentesSemSupervisor();

    @Query(value = "WITH RECURSIVE FuncionarioHierarchy AS (" +
            "  SELECT f1.id_funcionario, f1.id_supervisor, 1 as level " +
            "  FROM funcionario f1 WHERE f1.id_funcionario = :idFuncionario " +
            "  UNION ALL " +
            "  SELECT f2.id_funcionario, f2.id_supervisor, fh.level + 1 " +
            "  FROM funcionario f2 " +
            "  INNER JOIN FuncionarioHierarchy fh ON f2.id_supervisor = fh.id_funcionario" +
            ") SELECT * FROM FuncionarioHierarchy", nativeQuery = true)
    List<Object[]> findHierarquiaFuncionario(@Param("idFuncionario") Integer idFuncionario);


    @Query("SELECT f FROM Funcionario f JOIN f.agencia a JOIN a.endereco e WHERE e.cidade = :cidade")
    List<Funcionario> findByCidadeAgencia(@Param("cidade") String cidade);

    @Query("SELECT f FROM Funcionario f JOIN f.agencia a JOIN a.endereco e WHERE e.estado = :estado")
    List<Funcionario> findByEstadoAgencia(@Param("estado") String estado);

    @Query("SELECT f FROM Funcionario f JOIN f.agencia a JOIN a.endereco e WHERE e.cidade = :cidade AND e.estado = :estado")
    List<Funcionario> findByCidadeAndEstadoAgencia(@Param("cidade") String cidade, @Param("estado") String estado);


    @Query("SELECT f FROM Funcionario f WHERE f IN (SELECT a.funcionario FROM AuditoriaAberturaConta a WHERE a.dataHora BETWEEN :dataInicio AND :dataFim)")
    List<Funcionario> findFuncionariosComAuditoriaNoPeriodo(@Param("dataInicio") java.time.LocalDateTime dataInicio, @Param("dataFim") java.time.LocalDateTime dataFim);

    @Query("SELECT f FROM Funcionario f WHERE SIZE(f.relatorios) > :minRelatorios")
    List<Funcionario> findFuncionariosComMaisRelatoriosQue(@Param("minRelatorios") int minRelatorios);

    @Query("SELECT f FROM Funcionario f WHERE SIZE(f.auditorias) > :minAuditorias")
    List<Funcionario> findFuncionariosComMaisAuditoriasQue(@Param("minAuditorias") int minAuditorias);


    List<Funcionario> findAllByOrderByUsuarioNomeAsc();
    List<Funcionario> findByCargoOrderByUsuarioNomeAsc(Cargo cargo);
    List<Funcionario> findByAgenciaIdAgenciaOrderByCargoAscUsuarioNomeAsc(Integer idAgencia);


    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Funcionario f WHERE f.codigoFuncionario = :codigoFuncionario AND f.idFuncionario <> :idFuncionario")
    boolean existsByCodigoFuncionarioAndIdFuncionarioNot(@Param("codigoFuncionario") String codigoFuncionario, @Param("idFuncionario") Integer idFuncionario);


    @Query("SELECT f FROM Funcionario f JOIN FETCH f.usuario JOIN FETCH f.agencia WHERE f.idFuncionario = :idFuncionario")
    Optional<Funcionario> findByIdWithUsuarioAndAgencia(@Param("idFuncionario") Integer idFuncionario);

    @Query("SELECT f FROM Funcionario f JOIN FETCH f.usuario WHERE f.agencia.idAgencia = :idAgencia")
    List<Funcionario> findByAgenciaWithUsuario(@Param("idAgencia") Integer idAgencia);


    @Query("SELECT f FROM Funcionario f WHERE f.cargo = 'GERENTE' AND f.agencia.idAgencia = :idAgencia")
    Optional<Funcionario> findGerenteByAgencia(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT f FROM Funcionario f WHERE f.cargo IN ('GERENTE', 'ATENDENTE') ORDER BY f.cargo, f.usuario.nome")
    List<Funcionario> findGerentesEAtendentes();
}