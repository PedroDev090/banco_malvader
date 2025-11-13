package com.malvader.banco.repository;

import com.malvader.banco.models.ContaPoupanca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaPoupancaRepository extends JpaRepository<ContaPoupanca, Integer> {

    Optional<ContaPoupanca> findByContaIdConta(Integer idConta);
}