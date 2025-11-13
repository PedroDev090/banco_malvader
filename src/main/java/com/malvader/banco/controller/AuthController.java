package com.malvader.banco.controller;

import com.malvader.banco.dto.LoginDTO;
import com.malvader.banco.dto.LoginResponseDTO;
import com.malvader.banco.dto.UsuarioSessaoDTO;
import com.malvader.banco.service.AuthService;
import com.malvader.banco.service.FuncionarioService;
import com.malvader.banco.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Página de login
     */
    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        // Se já estiver logado, redirecionar para o dashboard apropriado
        if (session.getAttribute("usuarioLogado") != null) {
            return determinarRedirectPorTipoUsuario(session);
        }

        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    /**
     * Processar login
     */
    @PostMapping("/login")
    public String processLogin(@ModelAttribute LoginDTO loginDTO,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        LoginResponseDTO resultado = authService.autenticar(loginDTO);

        if (resultado.isSucesso()) {
            // Salvar dados na sessão
            salvarDadosSessao(session, resultado);

            redirectAttributes.addFlashAttribute("sucesso", "Login realizado com sucesso!");
            return "redirect:" + resultado.getRedirectUrl();
        } else {
            model.addAttribute("erro", resultado.getMensagem());
            model.addAttribute("loginDTO", loginDTO);
            return "auth/login";
        }
    }

    /**
     * Logout
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("sucesso", "Logout realizado com sucesso!");
        return "redirect:/auth/login";
    }

    /**
     * Endpoint para verificar CPF via AJAX
     */
    @GetMapping("/verificar-cpf")
    @ResponseBody
    public String verificarCpf(@RequestParam String cpf) {
        boolean existe = authService.verificarCpfExistente(cpf);
        if (existe) {
            String tipoUsuario = authService.obterTipoUsuarioPorCpf(cpf);
            return tipoUsuario != null ? tipoUsuario : "NOT_FOUND";
        }
        return "NOT_FOUND";
    }

    /**
     * Página de acesso negado
     */
    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "auth/acesso-negado";
    }

    /**
     * Salva dados do usuário na sessão
     */
    private void salvarDadosSessao(HttpSession session, LoginResponseDTO resultado) {
        session.setAttribute("usuarioLogado", true);
        session.setAttribute("idUsuario", resultado.getIdUsuario());
        session.setAttribute("nomeUsuario", resultado.getNome());
        session.setAttribute("tipoUsuario", resultado.getTipoUsuario());
        session.setAttribute("cargo", resultado.getCargo());

        // Criar DTO de sessão para facilitar acesso
        UsuarioSessaoDTO usuarioSessao = new UsuarioSessaoDTO();
        usuarioSessao.setIdUsuario(resultado.getIdUsuario());
        usuarioSessao.setNome(resultado.getNome());
        usuarioSessao.setTipoUsuario(resultado.getTipoUsuario());
        usuarioSessao.setCargo(resultado.getCargo() != null ? resultado.getCargo().name() : null);

        session.setAttribute("usuarioSessao", usuarioSessao);
    }

    /**
     * Determina redirect baseado no tipo de usuário na sessão
     */
    private String determinarRedirectPorTipoUsuario(HttpSession session) {
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if ("FUNCIONARIO".equals(tipoUsuario)) {
            return "redirect:/funcionario/dashboard";
        } else if ("CLIENTE".equals(tipoUsuario)) {
            return "redirect:/cliente/dashboard";
        }
        return "redirect:/auth/login";
    }
}