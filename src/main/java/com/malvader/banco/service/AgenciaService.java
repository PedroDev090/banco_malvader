package com.malvader.banco.service;

import com.malvader.banco.models.Agencia;
import com.malvader.banco.repository.AgenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AgenciaService {

    @Autowired
    private AgenciaRepository agenciaRepository;

    /**
     * Buscar agência por ID
     */
    @Transactional(readOnly = true)
    public Optional<Agencia> buscarPorId(Integer idAgencia) {
        return agenciaRepository.findById(idAgencia);
    }

    /**
     * Buscar agência por código
     */
    @Transactional(readOnly = true)
    public Optional<Agencia> buscarPorCodigo(String codigoAgencia) {
        return agenciaRepository.findByCodigoAgencia(codigoAgencia);
    }

    /**
     * Buscar todas as agências
     */
    @Transactional(readOnly = true)
    public List<Agencia> buscarTodas() {
        return agenciaRepository.findAll();
    }

    /**
     * Verificar se agência existe
     */
    @Transactional(readOnly = true)
    public boolean existeAgencia(Integer idAgencia) {
        return agenciaRepository.existsById(idAgencia);
    }
}