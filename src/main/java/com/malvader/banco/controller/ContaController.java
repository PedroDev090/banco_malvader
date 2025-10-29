package com.malvader.banco.controller;

import com.malvader.banco.dto.AberturaContaDTO;
import com.malvader.banco.models.*;
import com.malvader.banco.service.ContaService;
import com.malvader.banco.service.ClienteService;
import com.malvader.banco.service.AgenciaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private AgenciaService agenciaService;

    /**
     * Página de abertura de conta
     */
    @GetMapping("/abrir")
    public String abrirContaPage(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Verificar se é funcionário (apenas funcionários podem abrir contas)
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if (!"FUNCIONARIO".equals(tipoUsuario)) {
            return "redirect:/auth/acesso-negado";
        }

        model.addAttribute("aberturaContaDTO", new AberturaContaDTO());
        model.addAttribute("paginaAtiva", "abrir-conta");

        return "contas/abrir";
    }

    /**
     * Processar abertura de conta
     */
    @PostMapping("/abrir")
    public String processarAberturaConta(@Valid @ModelAttribute AberturaContaDTO aberturaContaDTO,
                                         BindingResult result,
                                         HttpSession session,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Verificar se é funcionário
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if (!"FUNCIONARIO".equals(tipoUsuario)) {
            return "redirect:/auth/acesso-negado";
        }

        if (result.hasErrors()) {
            model.addAttribute("paginaAtiva", "abrir-conta");
            return "contas/abrir";
        }

        try {
            // Buscar cliente e agência
            Optional<Cliente> clienteOpt = clienteService.buscarPorId(aberturaContaDTO.getIdCliente());
            Optional<Agencia> agenciaOpt = agenciaService.buscarPorId(aberturaContaDTO.getIdAgencia());

            if (clienteOpt.isEmpty() || agenciaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Cliente ou agência não encontrados");
                return "redirect:/contas/abrir";
            }

            // Criar conta base
            Conta conta = new Conta();
            conta.setAgencia(agenciaOpt.get());
            conta.setCliente(clienteOpt.get());
            conta.setSaldo(BigDecimal.ZERO);
            conta.setStatus(StatusConta.ATIVA);

            // Criar conta específica baseada no tipo
            switch (aberturaContaDTO.getTipoConta()) {
                case "CORRENTE":
                    conta.setTipoConta(TipoConta.CORRENTE);
                    ContaCorrente cc = contaService.criarContaCorrente(
                            conta,
                            aberturaContaDTO.getLimite() != null ? aberturaContaDTO.getLimite() : BigDecimal.valueOf(1000),
                            aberturaContaDTO.getDataVencimento() != null ? aberturaContaDTO.getDataVencimento() : LocalDate.now().plusYears(1),
                            aberturaContaDTO.getTaxaManutencao() != null ? aberturaContaDTO.getTaxaManutencao() : BigDecimal.valueOf(10)
                    );
                    redirectAttributes.addFlashAttribute("sucesso",
                            "Conta corrente aberta com sucesso! Número: " + cc.getConta().getNumeroConta());
                    break;

                case "POUPANCA":
                    conta.setTipoConta(TipoConta.POUPANCA);
                    ContaPoupanca cp = contaService.criarContaPoupanca(
                            conta,
                            aberturaContaDTO.getTaxaRendimento() != null ? aberturaContaDTO.getTaxaRendimento() : BigDecimal.valueOf(0.005)
                    );
                    redirectAttributes.addFlashAttribute("sucesso",
                            "Conta poupança aberta com sucesso! Número: " + cp.getConta().getNumeroConta());
                    break;

                case "INVESTIMENTO":
                    conta.setTipoConta(TipoConta.INVESTIMENTO);
                    ContaInvestimento ci = contaService.criarContaInvestimento(
                            conta,
                            PerfilRisco.valueOf(aberturaContaDTO.getPerfilRisco() != null ? aberturaContaDTO.getPerfilRisco() : "MEDIO"),
                            aberturaContaDTO.getValorMinimo() != null ? aberturaContaDTO.getValorMinimo() : BigDecimal.valueOf(1000),
                            aberturaContaDTO.getTaxaRendimentoBase() != null ? aberturaContaDTO.getTaxaRendimentoBase() : BigDecimal.valueOf(0.008)
                    );
                    redirectAttributes.addFlashAttribute("sucesso",
                            "Conta investimento aberta com sucesso! Número: " + ci.getConta().getNumeroConta());
                    break;

                default:
                    redirectAttributes.addFlashAttribute("erro", "Tipo de conta inválido");
                    return "redirect:/contas/abrir";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao abrir conta: " + e.getMessage());
        }

        return "redirect:/contas/abrir";
    }

    /**
     * Consultar conta por número
     */
    @GetMapping("/consultar")
    public String consultarContaPage(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("paginaAtiva", "consultar-conta");
        return "contas/consultar";
    }

    /**
     * API para consultar conta (AJAX)
     */
    @GetMapping("/consultar/api")
    @ResponseBody
    public Object consultarConta(@RequestParam String numeroConta, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return java.util.Map.of("erro", "Usuário não autenticado");
        }

        try {
            Optional<Conta> contaOpt = contaService.buscarPorNumero(numeroConta);
            if (contaOpt.isPresent()) {
                Conta conta = contaOpt.get();
                return java.util.Map.of(
                        "numero", conta.getNumeroConta(),
                        "tipo", conta.getTipoConta().name(),
                        "saldo", conta.getSaldo(),
                        "status", conta.getStatus().name(),
                        "cliente", conta.getCliente().getUsuario().getNome(),
                        "agencia", conta.getAgencia().getNome()
                );
            } else {
                return java.util.Map.of("erro", "Conta não encontrada");
            }
        } catch (Exception e) {
            return java.util.Map.of("erro", "Erro ao consultar conta: " + e.getMessage());
        }
    }

    /**
     * Bloquear conta
     */
    @PostMapping("/bloquear")
    public String bloquearConta(@RequestParam Integer idConta,
                                @RequestParam String motivo,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Apenas funcionários podem bloquear contas
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if (!"FUNCIONARIO".equals(tipoUsuario)) {
            return "redirect:/auth/acesso-negado";
        }

        try {
            boolean sucesso = contaService.bloquearConta(idConta, motivo);
            if (sucesso) {
                redirectAttributes.addFlashAttribute("sucesso", "Conta bloqueada com sucesso");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Não foi possível bloquear a conta");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao bloquear conta: " + e.getMessage());
        }

        return "redirect:/contas/consultar";
    }
}