package com.malvader.banco.repository;

import com.malvader.banco.models.Agencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgenciaRepository extends JpaRepository<Agencia, Integer> {

    Optional<Agencia> findByCodigoAgencia(String codigoAgencia);

    boolean existsByCodigoAgencia(String codigoAgencia);
}