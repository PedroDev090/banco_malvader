package com.malvader.banco.controller;

import com.malvader.banco.dto.OperacaoDTO;
import com.malvader.banco.dto.SaldoResponseDTO;
import com.malvader.banco.models.*;
import com.malvader.banco.service.OperacaoService;
import com.malvader.banco.service.ContaService;
import com.malvader.banco.service.TransacaoService;
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

    // ========== DEPÓSITO ==========
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
            // Buscar conta pelo número
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
            // Buscar conta pelo número
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
            // Buscar conta pelo número
            Optional<Conta> contaOpt = contaService.buscarPorNumero(numeroConta);
            if (contaOpt.isEmpty()) {
                model.addAttribute("erro", "Conta não encontrada: " + numeroConta);
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
            // Buscar conta pelo número
            Optional<Conta> contaOpt = contaService.buscarPorNumero(numeroConta);
            if (contaOpt.isEmpty()) {
                model.addAttribute("erro", "Conta não encontrada: " + numeroConta);
                return "clientes/extrato";
            }

            Conta conta = contaOpt.get();

            // Verificar se a conta está ativa
            if (conta.getStatus() != StatusConta.ATIVA) {
                model.addAttribute("erro", "Conta não está ativa");
                return "clientes/extrato";
            }

            // Buscar últimas transações (últimos 30 dias)
            List<Transacao> transacoes = transacaoService.buscarUltimasTransacoes(conta.getIdConta(), 20);

            model.addAttribute("transacoes", transacoes);
            model.addAttribute("conta", conta);
            model.addAttribute("sucesso", "Extrato gerado com sucesso");

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao gerar extrato: " + e.getMessage());
        }

        return "clientes/extrato";
    }
}