package com.malvader.banco.controller;

import com.malvader.banco.dto.UsuarioSessaoDTO;
import com.malvader.banco.service.FuncionarioService;
import com.malvader.banco.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Dashboard principal - redireciona para o dashboard apropriado
     */
    @GetMapping
    public String dashboardPrincipal(HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        return determinarDashboardPorTipoUsuario(session);
    }

    /**
     * Dashboard do funcionário
     */
    @GetMapping("/funcionario")
    public String dashboardFuncionario(HttpSession session, Model model) {
        if (!verificarAcesso(session, "FUNCIONARIO")) {
            return "redirect:/auth/acesso-negado";
        }

        UsuarioSessaoDTO usuarioSessao = (UsuarioSessaoDTO) session.getAttribute("usuarioSessao");
        model.addAttribute("usuario", usuarioSessao);
        model.addAttribute("paginaAtiva", "dashboard");


        return "funcionario/dashboard";
    }



    /**
     * Dashboard do cliente - atualizado com operações
     */
    @GetMapping("/cliente")
    public String dashboardCliente(HttpSession session, Model model) {
        if (!verificarAcesso(session, "CLIENTE")) {
            return "redirect:/auth/acesso-negado";
        }

        UsuarioSessaoDTO usuarioSessao = (UsuarioSessaoDTO) session.getAttribute("usuarioSessao");
        model.addAttribute("usuario", usuarioSessao);
        model.addAttribute("paginaAtiva", "dashboard");

        try {

            model.addAttribute("saldoTotal", 5250.00);
            model.addAttribute("totalContas", 3);
            model.addAttribute("ultimaTransacao", 150.00);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar dashboard: " + e.getMessage());
        }

        return "cliente/dashboard";
    }


    /**
     * Dashboard do GERENTE
     */
    @GetMapping("/funcionario/gerente")
    public String dashboardGerente(HttpSession session, Model model) {
        if (!verificarAcesso(session, "FUNCIONARIO")) {
            return "redirect:/auth/acesso-negado";
        }

        // Verificar se é GERENTE
        UsuarioSessaoDTO usuarioSessao = (UsuarioSessaoDTO) session.getAttribute("usuarioSessao");
        if (!"GERENTE".equals(usuarioSessao.getCargo())) {
            return "redirect:/auth/acesso-negado";
        }

        model.addAttribute("usuario", usuarioSessao);
        model.addAttribute("paginaAtiva", "dashboard");
        return "funcionario/gerente/dashboard";
    }

    /**
     * Dashboard do ATENDENTE
     */
    @GetMapping("/funcionario/atendente")
    public String dashboardAtendente(HttpSession session, Model model) {
        if (!verificarAcesso(session, "FUNCIONARIO")) {
            return "redirect:/auth/acesso-negado";
        }

        UsuarioSessaoDTO usuarioSessao = (UsuarioSessaoDTO) session.getAttribute("usuarioSessao");
        if (!"ATENDENTE".equals(usuarioSessao.getCargo())) {
            return "redirect:/auth/acesso-negado";
        }

        model.addAttribute("usuario", usuarioSessao);
        model.addAttribute("paginaAtiva", "dashboard");
        return "funcionario/atendente/dashboard";
    }

    /**
     * Dashboard do ESTAGIARIO
     */
    @GetMapping("/funcionario/estagiario")
    public String dashboardEstagiario(HttpSession session, Model model) {
        if (!verificarAcesso(session, "FUNCIONARIO")) {
            return "redirect:/auth/acesso-negado";
        }

        UsuarioSessaoDTO usuarioSessao = (UsuarioSessaoDTO) session.getAttribute("usuarioSessao");
        if (!"ESTAGIARIO".equals(usuarioSessao.getCargo())) {
            return "redirect:/auth/acesso-negado";
        }

        model.addAttribute("usuario", usuarioSessao);
        model.addAttribute("paginaAtiva", "dashboard");
        return "funcionario/estagiario/dashboard";
    }

    /**
     * Verifica se o usuário tem acesso à página
     */
    private boolean verificarAcesso(HttpSession session, String tipoRequerido) {
        if (session.getAttribute("usuarioLogado") == null) {
            return false;
        }

        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        return tipoRequerido.equals(tipoUsuario);
    }

    /**
     * Determina o dashboard baseado no tipo de usuário
     */
    private String determinarDashboardPorTipoUsuario(HttpSession session) {
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        String cargo = (String) session.getAttribute("cargo");

        if ("FUNCIONARIO".equals(tipoUsuario)) {
            if ("GERENTE".equals(cargo)) {
                return "redirect:/dashboard/funcionario/gerente";
            }
            return "redirect:/dashboard/funcionario";
        } else if ("CLIENTE".equals(tipoUsuario)) {
            return "redirect:/dashboard/cliente";
        }

        return "redirect:/auth/login";
    }


}