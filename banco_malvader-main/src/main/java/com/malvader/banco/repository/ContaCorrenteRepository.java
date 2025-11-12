package com.malvader.banco.repository;

import com.malvader.banco.models.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Integer> {

    Optional<ContaCorrente> findByContaIdConta(Integer idConta);

    // Buscar contas correntes com limite alto
    @Query("SELECT cc FROM ContaCorrente cc WHERE cc.limite > :limiteMinimo")
    List<ContaCorrente> findByLimiteGreaterThan(@Param("limiteMinimo") Double limiteMinimo);

    // Buscar contas próximas do vencimento
    @Query("SELECT cc FROM ContaCorrente cc WHERE cc.dataVencimento BETWEEN :hoje AND :dataLimite")
    List<ContaCorrente> findContasProximasVencimento(@Param("hoje") String hoje,
                                                     @Param("dataLimite") String dataLimite);
}