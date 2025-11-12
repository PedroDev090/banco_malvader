package com.malvader.banco.controller;

import com.malvader.banco.service.TransacaoService;
import com.malvader.banco.service.ContaService;
import com.malvader.banco.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Página de relatórios
     */
    @GetMapping
    public String relatoriosPage(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("paginaAtiva", "relatorios");
        return "relatorios/index";
    }

    /**
     * Relatório de movimentações
     */
    @GetMapping("/movimentacoes")
    public String relatorioMovimentacoes(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Apenas funcionários podem ver relatórios
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if (!"FUNCIONARIO".equals(tipoUsuario)) {
            return "redirect:/auth/acesso-negado";
        }

        model.addAttribute("paginaAtiva", "relatorios");
        return "relatorios/movimentacoes";
    }

    /**
     * API para relatório de movimentações (AJAX)
     */
    @GetMapping("/movimentacoes/api")
    @ResponseBody
    public Object movimentacoesApi(@RequestParam String dataInicio,
                                   @RequestParam String dataFim,
                                   HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return Map.of("erro", "Usuário não autenticado");
        }

        try {
            // Em produção, converter as datas corretamente
            LocalDateTime inicio = LocalDateTime.now().minusDays(30); // Exemplo
            LocalDateTime fim = LocalDateTime.now();

            Map<String, Object> relatorio = new HashMap<>();

            // Dados simulados - em produção usar os serviços reais
            relatorio.put("totalDepositos", 154200.75);
            relatorio.put("totalSaques", 89250.30);
            relatorio.put("totalTransferencias", 45300.00);
            relatorio.put("contasAbertas", 23);
            relatorio.put("clientesNovos", 15);

            return relatorio;

        } catch (Exception e) {
            return Map.of("erro", "Erro ao gerar relatório: " + e.getMessage());
        }
    }

    /**
     * Relatório de contas
     */
    @GetMapping("/contas")
    public String relatorioContas(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Apenas funcionários podem ver relatórios
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if (!"FUNCIONARIO".equals(tipoUsuario)) {
            return "redirect:/auth/acesso-negado";
        }

        try {
            // Dados para o relatório
            model.addAttribute("totalContas", contaService.buscarPorCliente(1).size()); // Exemplo
            model.addAttribute("contasAtivas", 156);
            model.addAttribute("contasInativas", 12);
            model.addAttribute("contasCorrente", 89);
            model.addAttribute("contasPoupanca", 45);
            model.addAttribute("contasInvestimento", 22);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar relatório: " + e.getMessage());
        }

        model.addAttribute("paginaAtiva", "relatorios");
        return "relatorios/contas";
    }

    /**
     * Relatório de desempenho (apenas gerentes)
     */
    @GetMapping("/desempenho")
    public String relatorioDesempenho(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Apenas gerentes podem ver relatório de desempenho
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        String cargo = (String) session.getAttribute("cargo");

        if (!"FUNCIONARIO".equals(tipoUsuario) || !"GERENTE".equals(cargo)) {
            return "redirect:/auth/acesso-negado";
        }

        try {
            // Dados simulados para desempenho
            model.addAttribute("lucroMensal", 154200.75);
            model.addAttribute("custoOperacional", 89250.30);
            model.addAttribute("novosClientes", 45);
            model.addAttribute("satisfacao", 94.5);
            model.addAttribute("inadimplencia", 2.3);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar relatório: " + e.getMessage());
        }

        model.addAttribute("paginaAtiva", "relatorios");
        return "relatorios/desempenho";
    }
}
