package com.malvader.banco.controller;

import com.malvader.banco.dto.OperacaoDTO;
import com.malvader.banco.dto.SaldoResponseDTO;
import com.malvader.banco.models.*;
import com.malvader.banco.service.OperacaoService;
import com.malvader.banco.service.ContaService;
import com.malvader.banco.service.TransacaoService;
import com.malvader.banco.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private OperacaoService operacaoService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Método auxiliar para obter o cliente logado
     */
    private Optional<Cliente> obterClienteLogado(HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return Optional.empty();
        }
        Integer idUsuarioLogado = (Integer) session.getAttribute("idUsuario");
        return clienteService.buscarPorIdComUsuario(idUsuarioLogado);
    }

    /**
     * Verificar se conta pertence ao cliente logado
     */
    private boolean contaPertenceAoClienteLogado(String numeroConta, HttpSession session) {
        Optional<Cliente> clienteOpt = obterClienteLogado(session);
        return clienteOpt.isPresent() &&
                contaService.contaPertenceAoCliente(numeroConta, clienteOpt.get().getIdCliente());
    }

    @GetMapping("/dashboard")
    public String dashboardCliente(HttpSession session, Model model) {
        // Verificar autenticação
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Adicionar dados do usuário
        String nomeUsuario = (String) session.getAttribute("nomeUsuario");
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        model.addAttribute("nomeUsuario", nomeUsuario);
        model.addAttribute("paginaAtiva", "dashboard");

        return "clientes/menuCliente";
    }

    //  DEPÓSITO
    @GetMapping("/deposito")
    public String paginaDeposito(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }
        return "clientes/deposito";
    }

    @PostMapping("/deposito")
    public String processarDeposito(@RequestParam String numeroConta,
                                    @RequestParam BigDecimal valor,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        try {
            // VERIFICAR SE CONTA PERTENCE AO CLIENTE LOGADO
            if (!contaPertenceAoClienteLogado(numeroConta, session)) {
                redirectAttributes.addFlashAttribute("erro", "Conta não pertence ao cliente ou acesso não autorizado");
                return "redirect:/cliente/deposito";
            }

            // Buscar conta pelo número (já validado que pertence ao cliente)
            Optional<Conta> contaOpt = contaService.buscarPorNumero(numeroConta);
            if (contaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Conta não encontrada: " + numeroConta);
                return "redirect:/cliente/deposito";
            }

            Conta conta = contaOpt.get();

            // Verificar se a conta está ativa
            if (conta.getStatus() != StatusConta.ATIVA) {
                redirectAttributes.addFlashAttribute("erro", "Conta não está ativa");
                return "redirect:/cliente/deposito";
            }

            // Realizar depósito
            Transacao transacao = transacaoService.realizarDeposito(
                    conta.getIdConta(),
                    valor,
                    "Depósito realizado via sistema"
            );

            redirectAttributes.addFlashAttribute("sucesso",
                    "Depósito de R$ " + valor + " realizado com sucesso! ID: " + transacao.getIdTransacao());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao realizar depósito: " + e.getMessage());
        }

        return "redirect:/cliente/deposito";
    }

    // ========== SAQUE ==========
    @GetMapping("/saque")
    public String paginaSaque(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }
        return "clientes/saque";
    }

    @PostMapping("/saque")
    public String processarSaque(@RequestParam String numeroConta,
                                 @RequestParam BigDecimal valor,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        try {
            // VERIFICAR SE CONTA PERTENCE AO CLIENTE LOGADO
            if (!contaPertenceAoClienteLogado(numeroConta, session)) {
                redirectAttributes.addFlashAttribute("erro", "Conta não pertence ao cliente ou acesso não autorizado");
                return "redirect:/cliente/saque";
            }

            // Buscar conta pelo número (já validado que pertence ao cliente)
            Optional<Conta> contaOpt = contaService.buscarPorNumero(numeroConta);
            if (contaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Conta não encontrada: " + numeroConta);
                return "redirect:/cliente/saque";
            }

            Conta conta = contaOpt.get();

            // Verificar se a conta está ativa
            if (conta.getStatus() != StatusConta.ATIVA) {
                redirectAttributes.addFlashAttribute("erro", "Conta não está ativa");
                return "redirect:/cliente/saque";
            }

            // Realizar saque
            Transacao transacao = transacaoService.realizarSaque(
                    conta.getIdConta(),
                    valor,
                    "Saque realizado via sistema"
            );

            redirectAttributes.addFlashAttribute("sucesso",
                    "Saque de R$ " + valor + " realizado com sucesso! ID: " + transacao.getIdTransacao());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao realizar saque: " + e.getMessage());
        }

        return "redirect:/cliente/saque";
    }

    // ========== TRANSFERÊNCIA ==========
    @GetMapping("/transferencia")
    public String paginaTransferencia(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }
        return "clientes/transferencia";
    }

    @PostMapping("/transferencia")
    public String processarTransferencia(@RequestParam String contaOrigem,
                                         @RequestParam String contaDestino,
                                         @RequestParam BigDecimal valor,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        try {
            // VERIFICAR SE CONTA ORIGEM PERTENCE AO CLIENTE LOGADO
            if (!contaPertenceAoClienteLogado(contaOrigem, session)) {
                redirectAttributes.addFlashAttribute("erro", "Conta origem não pertence ao cliente ou acesso não autorizado");
                return "redirect:/cliente/transferencia";
            }

            // Buscar contas pelos números
            Optional<Conta> contaOrigemOpt = contaService.buscarPorNumero(contaOrigem);
            Optional<Conta> contaDestinoOpt = contaService.buscarPorNumero(contaDestino);

            if (contaOrigemOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Conta origem não encontrada: " + contaOrigem);
                return "redirect:/cliente/transferencia";
            }

            if (contaDestinoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Conta destino não encontrada: " + contaDestino);
                return "redirect:/cliente/transferencia";
            }

            Conta contaOrigemObj = contaOrigemOpt.get();
            Conta contaDestinoObj = contaDestinoOpt.get();

            // Verificar se as contas estão ativas
            if (contaOrigemObj.getStatus() != StatusConta.ATIVA) {
                redirectAttributes.addFlashAttribute("erro", "Conta origem não está ativa");
                return "redirect:/cliente/transferencia";
            }

            if (contaDestinoObj.getStatus() != StatusConta.ATIVA) {
                redirectAttributes.addFlashAttribute("erro", "Conta destino não está ativa");
                return "redirect:/cliente/transferencia";
            }

            // Realizar transferência
            Transacao transacao = transacaoService.realizarTransferencia(
                    contaOrigemObj.getIdConta(),
                    contaDestinoObj.getIdConta(),
                    valor,
                    "Transferência realizada via sistema"
            );

            redirectAttributes.addFlashAttribute("sucesso",
                    "Transferência de R$ " + valor + " realizada com sucesso! ID: " + transacao.getIdTransacao());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao realizar transferência: " + e.getMessage());
        }

        return "redirect:/cliente/transferencia";
    }

    // ========== SALDO ==========
    @GetMapping("/saldo")
    public String paginaSaldo(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }
        return "clientes/saldo";
    }

    @PostMapping("/saldo")
    public String processarConsultaSaldo(@RequestParam String numeroConta,
                                         HttpSession session,
                                         Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        try {
            // BUSCAR CLIENTE ASSOCIADO AO USUÁRIO
            Optional<Cliente> clienteOpt = obterClienteLogado(session);
            if (clienteOpt.isEmpty()) {
                model.addAttribute("erro", "Cliente não encontrado");
                return "clientes/saldo";
            }

            Cliente cliente = clienteOpt.get();

            // VERIFICAR SE A CONTA PERTENCE AO CLIENTE
            Optional<Conta> contaOpt = contaService.buscarContaDoCliente(numeroConta, cliente.getIdCliente());
            if (contaOpt.isEmpty()) {
                model.addAttribute("erro", "Conta não encontrada ou acesso não autorizado");
                return "clientes/saldo";
            }

            Conta conta = contaOpt.get();

            // Verificar se a conta está ativa
            if (conta.getStatus() != StatusConta.ATIVA) {
                model.addAttribute("erro", "Conta não está ativa");
                return "clientes/saldo";
            }

            // Buscar informações completas para o saldo
            BigDecimal saldoDisponivel = operacaoService.consultarSaldoDisponivel(conta.getIdConta());

            SaldoResponseDTO saldoResponse = new SaldoResponseDTO(
                    conta.getNumeroConta(),
                    conta.getSaldo(),
                    BigDecimal.ZERO, // Em implementação real, buscaríamos o limite da conta corrente
                    conta.getStatus().name()
            );

            model.addAttribute("saldoResponse", saldoResponse);
            model.addAttribute("sucesso", "Consulta realizada com sucesso");

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao consultar saldo: " + e.getMessage());
        }

        return "clientes/saldo";
    }

    // ========== EXTRATO ==========
    @GetMapping("/extrato")
    public String paginaExtrato(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }
        return "clientes/extrato";
    }

    @PostMapping("/extrato")
    public String processarExtrato(@RequestParam String numeroConta,
                                   HttpSession session,
                                   Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        try {
            // BUSCAR CLIENTE ASSOCIADO AO USUÁRIO
            Optional<Cliente> clienteOpt = obterClienteLogado(session);
            if (clienteOpt.isEmpty()) {
                model.addAttribute("erro", "Cliente não encontrado");
                return "clientes/extrato";
            }

            Cliente cliente = clienteOpt.get();

            // VERIFICAR SE A CONTA PERTENCE AO CLIENTE
            Optional<Conta> contaOpt = contaService.buscarContaDoCliente(numeroConta, cliente.getIdCliente());
            if (contaOpt.isEmpty()) {
                model.addAttribute("erro", "Conta não encontrada ou acesso não autorizado");
                return "clientes/extrato";
            }

            Conta conta = contaOpt.get();

            // Verificar se a conta está ativa
            if (conta.getStatus() != StatusConta.ATIVA) {
                model.addAttribute("erro", "Conta não está ativa");
                return "clientes/extrato";
            }

            // Buscar últimas transações (por exemplo, últimas 20)
            List<Transacao> transacoes = transacaoService.buscarUltimasTransacoes(conta.getIdConta(), 20);

            model.addAttribute("transacoes", transacoes);
            model.addAttribute("conta", conta);
            model.addAttribute("sucesso", "Extrato gerado com sucesso");

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao gerar extrato: " + e.getMessage());
        }

        return "clientes/extrato";
    }

    // ========== ENCERRAR CONTA ==========

    /**
     * GET: exibe a tela de encerramento de conta do cliente
     */
    @GetMapping("/encerrar-conta")
    public String paginaEncerrarConta(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }
        return "clientes/encerrarConta"; // encerrarConta.html em /templates/clientes/
    }

    /**
     * POST: processa o encerramento da conta
     */
    @PostMapping("/encerrar")
    public String processarEncerramentoConta(@RequestParam("numeroConta") String numeroConta,
                                             @RequestParam("senha") String senha,
                                             @RequestParam("motivo") String motivo,
                                             HttpSession session,
                                             RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        try {
            // BUSCAR CLIENTE ASSOCIADO AO USUÁRIO
            Optional<Cliente> clienteOpt = obterClienteLogado(session);
            if (clienteOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado");
                return "redirect:/cliente/encerrar-conta";
            }

            Cliente cliente = clienteOpt.get();

            // VERIFICAR SE A CONTA PERTENCE AO CLIENTE
            Optional<Conta> contaOpt = contaService.buscarContaDoCliente(numeroConta, cliente.getIdCliente());
            if (contaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Conta não encontrada ou acesso não autorizado");
                return "redirect:/cliente/encerrar-conta";
            }

            Conta conta = contaOpt.get();

            // Verificar se a conta está ativa
            if (conta.getStatus() != StatusConta.ATIVA) {
                redirectAttributes.addFlashAttribute("erro", "Conta não está ativa");
                return "redirect:/cliente/encerrar-conta";
            }

            // Verificar se o saldo é zero (ou regras que você quiser)
            if (conta.getSaldo() == null || conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
                redirectAttributes.addFlashAttribute("erro",
                        "Não é possível encerrar a conta. Há saldo pendente ou dívidas ativas.");
                return "redirect:/cliente/encerrar-conta";
            }



            // Marcar conta como encerrada
            conta.setStatus(StatusConta.ENCERRADA);
            contaService.salvarConta(conta);


            redirectAttributes.addFlashAttribute("sucesso",
                    "Conta " + numeroConta + " encerrada com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao encerrar conta: " + e.getMessage());
        }

        return "redirect:/cliente/encerrar-conta";
    }

    // ========== LISTAR CONTAS DO CLIENTE ==========
    @GetMapping("/minhas-contas")
    public String listarContasCliente(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        try {
            // BUSCAR CLIENTE ASSOCIADO AO USUÁRIO
            Optional<Cliente> clienteOpt = obterClienteLogado(session);
            if (clienteOpt.isEmpty()) {
                model.addAttribute("erro", "Cliente não encontrado");
                return "clientes/minhasContas";
            }

            Cliente cliente = clienteOpt.get();

            // Buscar todas as contas do cliente
            List<Conta> contas = contaService.buscarContasPorCliente(cliente.getIdCliente());

            model.addAttribute("contas", contas);
            model.addAttribute("cliente", cliente);
            model.addAttribute("sucesso", "Contas carregadas com sucesso");

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar contas: " + e.getMessage());
        }

        return "clientes/minhasContas";
    }
}
