package com.malvader.banco.repository;

import com.malvader.banco.models.ContaInvestimento;
import com.malvader.banco.models.PerfilRisco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContaInvestimentoRepository extends JpaRepository<ContaInvestimento, Integer> {

    Optional<ContaInvestimento> findByContaIdConta(Integer idConta);
    Optional<ContaInvestimento> findByContaNumeroConta(String numeroConta);

    List<ContaInvestimento> findByPerfilRisco(PerfilRisco perfilRisco);
    List<ContaInvestimento> findByPerfilRiscoIn(List<PerfilRisco> perfis);

    List<ContaInvestimento> findByValorMinimoLessThanEqual(BigDecimal valorMaximo);
    List<ContaInvestimento> findByValorMinimoGreaterThanEqual(BigDecimal valorMinimo);
    List<ContaInvestimento> findByValorMinimoBetween(BigDecimal min, BigDecimal max);

    List<ContaInvestimento> findByTaxaRendimentoBaseGreaterThanEqual(BigDecimal taxaMinima);
    List<ContaInvestimento> findByTaxaRendimentoBaseLessThanEqual(BigDecimal taxaMaxima);
    List<ContaInvestimento> findByTaxaRendimentoBaseBetween(BigDecimal taxaMin, BigDecimal taxaMax);

    @Query("SELECT ci FROM ContaInvestimento ci JOIN ci.conta c WHERE c.agencia.idAgencia = :idAgencia")
    List<ContaInvestimento> findByAgenciaId(@Param("idAgencia") Integer idAgencia);

    @Query("SELECT ci FROM ContaInvestimento ci JOIN ci.conta c WHERE c.cliente.idCliente = :idCliente")
    List<ContaInvestimento> findByClienteId(@Param("idCliente") Integer idCliente);

    @Query("SELECT ci FROM ContaInvestimento ci JOIN ci.conta c JOIN c.cliente cl JOIN cl.usuario u WHERE u.cpf = :cpf")
    Optional<ContaInvestimento> findByCpfCliente(@Param("cpf") String cpf);

    @Query("SELECT AVG(ci.valorMinimo) FROM ContaInvestimento ci")
    Optional<BigDecimal> findMediaValorMinimo();

    @Query("SELECT AVG(ci.taxaRendimentoBase) FROM ContaInvestimento ci")
    Optional<BigDecimal> findMediaTaxaRendimento();

    @Query("SELECT ci.perfilRisco, AVG(ci.taxaRendimentoBase) FROM ContaInvestimento ci GROUP BY ci.perfilRisco")
    List<Object[]> findMediaTaxaRendimentoPorPerfil();

    @Query("SELECT ci.perfilRisco, COUNT(ci) FROM ContaInvestimento ci GROUP BY ci.perfilRisco")
    List<Object[]> countContasPorPerfilRisco();

    @Query("SELECT ci FROM ContaInvestimento ci JOIN ci.conta c WHERE c.status = 'ATIVA' AND c.saldo >= ci.valorMinimo")
    List<ContaInvestimento> findContasAtivasComSaldoMinimo();

    @Query("SELECT ci FROM ContaInvestimento ci JOIN ci.conta c WHERE c.status = 'ATIVA' AND c.saldo < ci.valorMinimo")
    List<ContaInvestimento> findContasAtivasAbaixoDoMinimo();

    @Query("SELECT ci FROM ContaInvestimento ci WHERE ci.perfilRisco = 'ALTO' AND ci.taxaRendimentoBase > :taxaMinima")
    List<ContaInvestimento> findContasAltoRiscoComRendimento(@Param("taxaMinima") BigDecimal taxaMinima);

    @Query("SELECT ci.perfilRisco, AVG(ci.valorMinimo) FROM ContaInvestimento ci GROUP BY ci.perfilRisco")
    List<Object[]> findMediaValorMinimoPorPerfil();

    List<ContaInvestimento> findAllByOrderByTaxaRendimentoBaseDesc();
    List<ContaInvestimento> findByPerfilRiscoOrderByTaxaRendimentoBaseDesc(PerfilRisco perfilRisco);

    boolean existsByContaIdConta(Integer idConta);

    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END FROM ContaInvestimento ci JOIN ci.conta c WHERE c.numeroConta = :numeroConta")
    boolean existsByNumeroConta(@Param("numeroConta") String numeroConta);

    @Query("SELECT ci FROM ContaInvestimento ci JOIN FETCH ci.conta c JOIN FETCH c.agencia JOIN FETCH c.cliente WHERE ci.idContaInvestimento = :idContaInvestimento")
    Optional<ContaInvestimento> findByIdWithContaAndAgenciaAndCliente(@Param("idContaInvestimento") Integer idContaInvestimento);

    @Query("SELECT ci FROM ContaInvestimento ci JOIN FETCH ci.conta c WHERE c.cliente.idCliente = :idCliente")
    List<ContaInvestimento> findByClienteWithConta(@Param("idCliente") Integer idCliente);
}