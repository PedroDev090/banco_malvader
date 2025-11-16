package com.malvader.banco.repository;

import com.malvader.banco.models.Funcionario;
import com.malvader.banco.models.Usuario;
import com.malvader.banco.models.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    Optional<Funcionario> findByUsuario(Usuario usuario);

    @Query("SELECT f FROM Funcionario f JOIN f.usuario u WHERE u.cpf = :cpf")
    Optional<Funcionario> findByUsuarioCpf(@Param("cpf") String cpf);

    List<Funcionario> findByCargo(Cargo cargo);

    List<Funcionario> findByAgenciaIdAgencia(Integer idAgencia);

    List<Funcionario> findBySupervisorIdFuncionario(Integer idSupervisor);

    @Query("SELECT COUNT(f) FROM Funcionario f WHERE f.agencia.idAgencia = :idAgencia")
    Long countByAgenciaId(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT f FROM Funcionario f JOIN FETCH f.agencia a JOIN FETCH a.endereco WHERE f.idFuncionario = :idFuncionario")
    Optional<Funcionario> findByIdWithAgenciaAndEndereco(@Param("idFuncionario") Integer idFuncionario);

    // ⬇⬇ ADICIONAR ESTES DOIS MÉTODOS ⬇⬇

    Optional<Funcionario> findByCodigoFuncionario(String codigoFuncionario);

    Optional<Funcionario> findByCodigoFuncionarioAndCargo(String codigoFuncionario, Cargo cargo);
}
