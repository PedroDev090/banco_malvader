package com.malvader.banco.repository;

import com.malvader.banco.models.Cliente;
import com.malvader.banco.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {


    Optional<Cliente> findByUsuario(Usuario usuario);


    @Query("SELECT c FROM Cliente c JOIN c.usuario u WHERE u.cpf = :cpf")
    Optional<Cliente> findByUsuarioCpf(@Param("cpf") String cpf);


    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.contas WHERE c.idCliente = :idCliente")
    Optional<Cliente> findByIdWithContas(@Param("idCliente") Integer idCliente);


    List<Cliente> findByScoreCreditoGreaterThanEqual(Double scoreMinimo);
    List<Cliente> findByScoreCreditoLessThan(Double scoreMaximo);

    @Query("SELECT c FROM Cliente c JOIN FETCH c.usuario u LEFT JOIN FETCH u.enderecos WHERE c.idCliente = :idCliente")
    Optional<Cliente> findByIdWithUsuarioCompleto(@Param("idCliente") Integer idCliente);
}