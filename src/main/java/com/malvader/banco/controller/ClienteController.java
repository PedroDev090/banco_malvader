package com.malvader.banco.controller;

import com.malvader.banco.models.Cliente;
import com.malvader.banco.models.Usuario;
import com.malvader.banco.service.ClienteService;
import com.malvader.banco.service.ContaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ContaService contaService;

    /**
     * Página de consulta de clientes
     */
    @GetMapping("/consultar")
    public String consultarClientesPage(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Apenas funcionários podem consultar clientes
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if (!"FUNCIONARIO".equals(tipoUsuario)) {
            return "redirect:/auth/acesso-negado";
        }

        model.addAttribute("paginaAtiva", "consultar-clientes");
        return "clientes/consultar";
    }

    /**
     * API para buscar cliente por CPF (AJAX)
     */
    @GetMapping("/buscar-por-cpf")
    @ResponseBody
    public Object buscarClientePorCpf(@RequestParam String cpf, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return java.util.Map.of("erro", "Usuário não autenticado");
        }

        try {
            Optional<Cliente> clienteOpt = clienteService.buscarPorCpf(cpf);
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                Usuario usuario = cliente.getUsuario();

                return java.util.Map.of(
                        "idCliente", cliente.getIdCliente(),
                        "nome", usuario.getNome(),
                        "cpf", usuario.getCpf(),
                        "telefone", usuario.getTelefone(),
                        "dataNascimento", usuario.getDataNascimento(),
                        "scoreCredito", cliente.getScoreCredito(),
                        "totalContas", contaService.buscarPorCliente(cliente.getIdCliente()).size()
                );
            } else {
                return java.util.Map.of("erro", "Cliente não encontrado");
            }
        } catch (Exception e) {
            return java.util.Map.of("erro", "Erro ao buscar cliente: " + e.getMessage());
        }
    }

    /**
     * Detalhes do cliente
     */
    @GetMapping("/detalhes/{idCliente}")
    public String detalhesCliente(@PathVariable Integer idCliente,
                                  HttpSession session,
                                  Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Apenas funcionários podem ver detalhes de clientes
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if (!"FUNCIONARIO".equals(tipoUsuario)) {
            return "redirect:/auth/acesso-negado";
        }

        try {
            Optional<Cliente> clienteOpt = clienteService.buscarCompleto(idCliente);
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                model.addAttribute("cliente", cliente);
                model.addAttribute("contas", contaService.buscarPorCliente(idCliente));
                model.addAttribute("patrimonio", contaService.calcularPatrimonioCliente(idCliente));
            } else {
                model.addAttribute("erro", "Cliente não encontrado");
            }
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar detalhes: " + e.getMessage());
        }

        model.addAttribute("paginaAtiva", "consultar-clientes");
        return "clientes/detalhes";
    }

    /**
     * Listar todos os clientes (para gerentes)
     */
    @GetMapping("/listar")
    public String listarClientes(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Apenas gerentes podem listar todos os clientes
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        String cargo = (String) session.getAttribute("cargo");

        if (!"FUNCIONARIO".equals(tipoUsuario) || !"GERENTE".equals(cargo)) {
            return "redirect:/auth/acesso-negado";
        }

        try {
            List<Cliente> clientes = clienteService.buscarTodos();
            model.addAttribute("clientes", clientes);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar clientes: " + e.getMessage());
        }

        model.addAttribute("paginaAtiva", "gerenciar-clientes");
        return "clientes/listar";
    }

    /**
     * Buscar clientes inadimplentes
     */
    @GetMapping("/inadimplentes")
    public String clientesInadimplentes(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        // Apenas gerentes podem ver inadimplentes
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        String cargo = (String) session.getAttribute("cargo");

        if (!"FUNCIONARIO".equals(tipoUsuario) || !"GERENTE".equals(cargo)) {
            return "redirect:/auth/acesso-negado";
        }

        try {
            List<Cliente> inadimplentes = clienteService.buscarInadimplentes();
            model.addAttribute("inadimplentes", inadimplentes);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar inadimplentes: " + e.getMessage());
        }

        model.addAttribute("paginaAtiva", "relatorios");
        return "clientes/inadimplentes";
    }
}