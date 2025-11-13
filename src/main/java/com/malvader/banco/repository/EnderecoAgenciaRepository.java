package com.malvader.banco.repository;

import com.malvader.banco.models.EnderecoAgencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoAgenciaRepository extends JpaRepository<EnderecoAgencia, Integer> {

    // Buscar endereços por CEP
    List<EnderecoAgencia> findByCep(String cep);

    // Buscar endereços por cidade
    List<EnderecoAgencia> findByCidade(String cidade);

    // Buscar endereços por estado
    List<EnderecoAgencia> findByEstado(String estado);

    // Buscar endereços por bairro
    List<EnderecoAgencia> findByBairro(String bairro);

    // Buscar endereços por cidade e estado
    List<EnderecoAgencia> findByCidadeAndEstado(String cidade, String estado);

    // Buscar endereço por local (rua/avenida)
    List<EnderecoAgencia> findByLocalContainingIgnoreCase(String local);

    // Buscar endereços que contenham complemento
    List<EnderecoAgencia> findByComplementoIsNotNull();

    // Buscar endereços por número
    List<EnderecoAgencia> findByNumero(Integer numero);

    // Buscar endereços por faixa de números
    List<EnderecoAgencia> findByNumeroBetween(Integer numeroInicio, Integer numeroFim);

    // Buscar endereço específico por CEP, número e complemento
    Optional<EnderecoAgencia> findByCepAndNumeroAndComplemento(String cep, Integer numero, String complemento);

    // Verificar se existe endereço com os mesmos dados
    boolean existsByCepAndNumeroAndComplemento(String cep, Integer numero, String complemento);

    // Buscar endereços por cidade ordenados por bairro
    List<EnderecoAgencia> findByCidadeOrderByBairroAsc(String cidade);

    // Buscar endereços por estado ordenados por cidade
    List<EnderecoAgencia> findByEstadoOrderByCidadeAscBairroAsc(String estado);

    // Buscar endereços que não tenham agência associada
    @Query("SELECT e FROM EnderecoAgencia e WHERE e.agencia IS NULL")
    List<EnderecoAgencia> findEnderecosSemAgencia();

    // Buscar cidades distintas com agências
    @Query("SELECT DISTINCT e.cidade FROM EnderecoAgencia e ORDER BY e.cidade")
    List<String> findCidadesDistintas();

    // Buscar estados distintos com agências
    @Query("SELECT DISTINCT e.estado FROM EnderecoAgencia e ORDER BY e.estado")
    List<String> findEstadosDistintos();

    // Buscar endereços por parte do CEP
    @Query("SELECT e FROM EnderecoAgencia e WHERE e.cep LIKE %:parteCep%")
    List<EnderecoAgencia> findByParteCep(@Param("parteCep") String parteCep);

    // Contar agências por estado
    @Query("SELECT e.estado, COUNT(e) FROM EnderecoAgencia e GROUP BY e.estado ORDER BY e.estado")
    List<Object[]> countAgenciasPorEstado();

    // Contar agências por cidade
    @Query("SELECT e.cidade, e.estado, COUNT(e) FROM EnderecoAgencia e GROUP BY e.cidade, e.estado ORDER BY e.estado, e.cidade")
    List<Object[]> countAgenciasPorCidade();

    // Buscar endereços com agência ativa
    @Query("SELECT e FROM EnderecoAgencia e WHERE e.agencia IS NOT NULL")
    List<EnderecoAgencia> findEnderecosComAgencia();
}