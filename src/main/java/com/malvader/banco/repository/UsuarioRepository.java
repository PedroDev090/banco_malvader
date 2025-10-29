package com.malvader.banco.repository;

import com.malvader.banco.models.Usuario;
import com.malvader.banco.models.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {


    Optional<Usuario> findByCpf(String cpf);


    boolean existsByCpf(String cpf);


    @Query("SELECT u FROM Usuario u WHERE u.cpf = :cpf AND u.tipoUsuario = :tipoUsuario")
    Optional<Usuario> findByCpfAndTipoUsuario(@Param("cpf") String cpf,
                                              @Param("tipoUsuario") TipoUsuario tipoUsuario);


    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.enderecos WHERE u.idUsuario = :idUsuario")
    Optional<Usuario> findByIdWithEnderecos(@Param("idUsuario") Integer idUsuario);
}