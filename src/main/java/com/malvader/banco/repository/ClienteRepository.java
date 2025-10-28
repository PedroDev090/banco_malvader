package com.malvader.banco.repository;

import com.malvader.banco.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByUsuarioIdUsuario(Integer idUsuario);
    Optional<Cliente> findByUsuarioCpf(String cpf);

    List<Cliente> findByScoreCreditoGreaterThanEqual(Double scoreMinimo);
    List<Cliente> findByScoreCreditoLessThanEqual(Double scoreMaximo);
    List<Cliente> findByScoreCreditoBetween(Double scoreMin, Double scoreMax);

    @Query("SELECT c FROM Cliente c WHERE c.usuario.nome LIKE %:nome%")
    List<Cliente> findByNomeContaining(@Param("nome") String nome);

    @Query("SELECT c FROM Cliente c WHERE c.usuario.telefone = :telefone")
    Optional<Cliente> findByTelefone(@Param("telefone") String telefone);

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.scoreCredito >= :scoreMinimo")
    Long countByScoreCreditoMinimo(@Param("scoreMinimo") Double scoreMinimo);

    @Query("SELECT AVG(c.scoreCredito) FROM Cliente c")
    Optional<Double> findMediaScoreCredito();

    @Query("SELECT c.scoreCredito, COUNT(c) FROM Cliente c GROUP BY c.scoreCredito ORDER BY c.scoreCredito")
    List<Object[]> countClientesPorScore();

    @Query("SELECT c FROM Cliente c WHERE SIZE(c.contas) > 0")
    List<Cliente> findClientesComContas();

    @Query("SELECT c FROM Cliente c WHERE SIZE(c.contas) = 0")
    List<Cliente> findClientesSemContas();

    @Query("SELECT c FROM Cliente c WHERE SIZE(c.contas) >= :minContas")
    List<Cliente> findClientesComMinimoContas(@Param("minContas") int minContas);

    @Query("SELECT c, COUNT(ct) FROM Cliente c LEFT JOIN c.contas ct GROUP BY c ORDER BY COUNT(ct) DESC")
    List<Object[]> findClientesComNumeroContas();

    @Query("SELECT c FROM Cliente c JOIN c.usuario u JOIN u.enderecos e WHERE e.cidade = :cidade")
    List<Cliente> findByCidade(@Param("cidade") String cidade);

    @Query("SELECT c FROM Cliente c JOIN c.usuario u JOIN u.enderecos e WHERE e.estado = :estado")
    List<Cliente> findByEstado(@Param("estado") String estado);

    @Query("SELECT c FROM Cliente c JOIN c.contas ct WHERE ct.agencia.idAgencia = :idAgencia")
    List<Cliente> findByAgencia(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT c FROM Cliente c JOIN c.contas ct WHERE ct.tipoConta = 'CORRENTE'")
    List<Cliente> findClientesComContaCorrente();

    @Query("SELECT c FROM Cliente c JOIN c.contas ct WHERE ct.tipoConta = 'POUPANCA'")
    List<Cliente> findClientesComContaPoupanca();

    @Query("SELECT c FROM Cliente c JOIN c.contas ct WHERE ct.tipoConta = 'INVESTIMENTO'")
    List<Cliente> findClientesComContaInvestimento();

    @Query("SELECT c FROM Cliente c WHERE c.scoreCredito >= 800")
    List<Cliente> findClientesScoreAlto();

    @Query("SELECT c FROM Cliente c WHERE c.scoreCredito BETWEEN 600 AND 799")
    List<Cliente> findClientesScoreMedio();

    @Query("SELECT c FROM Cliente c WHERE c.scoreCredito < 600")
    List<Cliente> findClientesScoreBaixo();

    List<Cliente> findAllByOrderByScoreCreditoDesc();
    List<Cliente> findByOrderByUsuarioNomeAsc();

    boolean existsByUsuarioCpf(String cpf);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.usuario.cpf = :cpf AND c.idCliente <> :idCliente")
    boolean existsByCpfAndIdClienteNot(@Param("cpf") String cpf, @Param("idCliente") Integer idCliente);

    @Query("SELECT c FROM Cliente c JOIN FETCH c.usuario WHERE c.idCliente = :idCliente")
    Optional<Cliente> findByIdWithUsuario(@Param("idCliente") Integer idCliente);

    @Query("SELECT c FROM Cliente c JOIN FETCH c.usuario JOIN FETCH c.contas WHERE c.idCliente = :idCliente")
    Optional<Cliente> findByIdWithUsuarioAndContas(@Param("idCliente") Integer idCliente);

    @Query("SELECT c FROM Cliente c JOIN FETCH c.usuario u WHERE u.nome LIKE %:nome%")
    List<Cliente> findByNomeWithUsuario(@Param("nome") String nome);
}