package com.malvader.banco;

import com.malvader.banco.models.Cliente;
import com.malvader.banco.models.Usuario;
import com.malvader.banco.models.TipoUsuario;
import com.malvader.banco.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // Usar perfil de teste
class ClienteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void shouldSaveAndFindClienteById() {
        // Given
        Usuario usuario = new Usuario(
                "Maria Santos", "12345678901", LocalDate.of(1990, 1, 1),
                "11999999999", TipoUsuario.CLIENTE, "senha123"
        );
        entityManager.persist(usuario);

        Cliente cliente = new Cliente(usuario, 750.0);

        // When
        Cliente saved = clienteRepository.save(cliente);
        Optional<Cliente> found = clienteRepository.findById(saved.getIdCliente());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsuario().getNome()).isEqualTo("Maria Santos");
        assertThat(found.get().getScoreCredito()).isEqualTo(750.0);
    }
}