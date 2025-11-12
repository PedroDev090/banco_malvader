package com.malvader.banco.repository;

import com.malvader.banco.models.ContaInvestimento;
import com.malvader.banco.models.PerfilRisco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaInvestimentoRepository extends JpaRepository<ContaInvestimento, Integer> {

    Optional<ContaInvestimento> findByContaIdConta(Integer idConta);

    List<ContaInvestimento> findByPerfilRisco(PerfilRisco perfilRisco);
}