package com.malvader.banco.repository;

import com.malvader.banco.models.EnderecoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoUsuarioRepository extends JpaRepository<EnderecoUsuario, Integer> {

    List<EnderecoUsuario> findByUsuarioIdUsuario(Integer idUsuario);

    List<EnderecoUsuario> findByCep(String cep);

    List<EnderecoUsuario> findByCidade(String cidade);

    List<EnderecoUsuario> findByEstado(String estado);

    List<EnderecoUsuario> findByBairro(String bairro);

    List<EnderecoUsuario> findByCidadeAndEstado(String cidade, String estado);

    List<EnderecoUsuario> findByLocalContainingIgnoreCase(String local);

    List<EnderecoUsuario> findByComplementoIsNotNull();

    List<EnderecoUsuario> findByComplementoIsNull();

    List<EnderecoUsuario> findByNumeroCasa(Integer numeroCasa);

    List<EnderecoUsuario> findByNumeroCasaBetween(Integer numeroInicio, Integer numeroFim);

    Optional<EnderecoUsuario> findByCepAndNumeroCasaAndComplemento(String cep, Integer numeroCasa, String complemento);

    boolean existsByCepAndNumeroCasaAndComplemento(String cep, Integer numeroCasa, String complemento);

    List<EnderecoUsuario> findByCidadeOrderByBairroAsc(String cidade);

    List<EnderecoUsuario> findByEstadoOrderByCidadeAscBairroAsc(String estado);

    @Query("SELECT eu FROM EnderecoUsuario eu WHERE eu.usuario.idUsuario = :idUsuario ORDER BY eu.idEndereco")
    List<EnderecoUsuario> findEnderecosPrincipalFirst(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT DISTINCT eu.cidade FROM EnderecoUsuario eu ORDER BY eu.cidade")
    List<String> findCidadesDistintas();

    @Query("SELECT DISTINCT eu.estado FROM EnderecoUsuario eu ORDER BY eu.estado")
    List<String> findEstadosDistintos();

    @Query("SELECT DISTINCT eu.bairro FROM EnderecoUsuario eu WHERE eu.cidade = :cidade ORDER BY eu.bairro")
    List<String> findBairrosPorCidade(@Param("cidade") String cidade);

    @Query("SELECT eu FROM EnderecoUsuario eu WHERE eu.cep LIKE %:parteCep%")
    List<EnderecoUsuario> findByParteCep(@Param("parteCep") String parteCep);

    @Query("SELECT eu.estado, COUNT(eu) FROM EnderecoUsuario eu GROUP BY eu.estado ORDER BY eu.estado")
    List<Object[]> countEnderecosPorEstado();

    @Query("SELECT eu.cidade, eu.estado, COUNT(eu) FROM EnderecoUsuario eu GROUP BY eu.cidade, eu.estado ORDER BY eu.estado, eu.cidade")
    List<Object[]> countEnderecosPorCidade();

    @Query("SELECT eu.bairro, eu.cidade, COUNT(eu) FROM EnderecoUsuario eu WHERE eu.cidade = :cidade GROUP BY eu.bairro, eu.cidade ORDER BY eu.bairro")
    List<Object[]> countEnderecosPorBairro(@Param("cidade") String cidade);

    @Query("SELECT eu FROM EnderecoUsuario eu JOIN eu.usuario u WHERE u.cpf = :cpf")
    List<EnderecoUsuario> findByCpfUsuario(@Param("cpf") String cpf);

    @Query("SELECT eu FROM EnderecoUsuario eu WHERE eu.usuario.idUsuario = :idUsuario AND eu.complemento IS NOT NULL")
    List<EnderecoUsuario> findEnderecosComComplementoByUsuario(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT COUNT(eu) FROM EnderecoUsuario eu WHERE eu.usuario.idUsuario = :idUsuario")
    Long countEnderecosByUsuario(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT eu FROM EnderecoUsuario eu JOIN FETCH eu.usuario WHERE eu.idEndereco = :idEndereco")
    Optional<EnderecoUsuario> findByIdWithUsuario(@Param("idEndereco") Integer idEndereco);

    @Query("SELECT eu FROM EnderecoUsuario eu JOIN FETCH eu.usuario WHERE eu.usuario.idUsuario = :idUsuario")
    List<EnderecoUsuario> findByUsuarioWithUsuario(@Param("idUsuario") Integer idUsuario);
}