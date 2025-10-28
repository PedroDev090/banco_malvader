package com.malvader.banco.repository;

import com.malvader.banco.models.Agencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgenciaRepository extends JpaRepository<Agencia, Integer> {

    Optional<Agencia> findByCodigoAgencia(String codigoAgencia);

    boolean existsByCodigoAgencia(String codigoAgencia);

    List<Agencia> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT a FROM Agencia a JOIN a.endereco e WHERE e.cidade = :cidade")
    List<Agencia> findByCidade(@Param("cidade") String cidade);

    @Query("SELECT a FROM Agencia a JOIN a.endereco e WHERE e.estado = :estado")
    List<Agencia> findByEstado(@Param("estado") String estado);

    @Query("SELECT a FROM Agencia a JOIN a.endereco e WHERE e.cep = :cep")
    Optional<Agencia> findByCep(@Param("cep") String cep);

    @Query("SELECT a FROM Agencia a JOIN a.endereco e WHERE e.bairro = :bairro")
    List<Agencia> findByBairro(@Param("bairro") String bairro);

    @Query("SELECT COUNT(f) FROM Funcionario f WHERE f.agencia.idAgencia = :idAgencia")
    Long countFuncionariosByAgencia(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT COUNT(c) FROM Conta c WHERE c.agencia.idAgencia = :idAgencia")
    Long countContasByAgencia(@Param("idAgencia") Integer idAgencia);

    List<Agencia> findAllByOrderByNomeAsc();

    @Query("SELECT a FROM Agencia a WHERE SIZE(a.funcionarios) > :minFuncionarios")
    List<Agencia> findAgenciasComMaisFuncionariosQue(@Param("minFuncionarios") int minFuncionarios);

    @Query("SELECT a FROM Agencia a WHERE SIZE(a.contas) > :minContas")
    List<Agencia> findAgenciasComMaisContasQue(@Param("minContas") int minContas);

    @Query("SELECT a.nome, COUNT(f) FROM Agencia a LEFT JOIN a.funcionarios f GROUP BY a.nome, a.idAgencia ORDER BY COUNT(f) DESC")
    List<Object[]> countFuncionariosPorAgencia();

    @Query("SELECT a.nome, COUNT(c) FROM Agencia a LEFT JOIN a.contas c GROUP BY a.nome, a.idAgencia ORDER BY COUNT(c) DESC")
    List<Object[]> countContasPorAgencia();

    @Query("SELECT a FROM Agencia a JOIN FETCH a.endereco WHERE a.idAgencia = :idAgencia")
    Optional<Agencia> findByIdWithEndereco(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT a FROM Agencia a JOIN FETCH a.endereco JOIN FETCH a.funcionarios WHERE a.idAgencia = :idAgencia")
    Optional<Agencia> findByIdWithEnderecoAndFuncionarios(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Agencia a WHERE a.codigoAgencia = :codigoAgencia AND a.idAgencia <> :idAgencia")
    boolean existsByCodigoAgenciaAndIdAgenciaNot(@Param("codigoAgencia") String codigoAgencia, @Param("idAgencia") Integer idAgencia);
}