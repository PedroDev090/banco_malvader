package com.malvader.banco.config;

import com.malvader.banco.models.Cargo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    // URLs públicas que não precisam de autenticação
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/auth/login",
            "/auth/verificar-cpf",
            "/",
            "/sobre",
            "/error",
            "/css/",
            "/js/",
            "/images/",
            "/webjars/"
    );

    // URLs que precisam de permissão de gerente
    private static final List<String> GERENTE_URLS = Arrays.asList(
            "/funcionario/gerente/",
            "/funcionario/cadastro",
            "/funcionario/relatorios/avancados"
    );

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        // Verificar se é URL pública
        if (isPublicUrl(requestURI)) {
            return true;
        }

        // Verificar se usuário está logado
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        // Obter dados da sessão
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        Cargo cargo = (Cargo) session.getAttribute("cargo");

        // Controle de acesso por tipo de usuário
        if (!hasAccess(tipoUsuario, cargo, requestURI)) {
            response.sendRedirect("/auth/acesso-negado");
            return false;
        }

        return true;
    }

    /**
     * Verifica se a URL é pública
     */
    private boolean isPublicUrl(String requestURI) {
        return PUBLIC_URLS.stream().anyMatch(requestURI::startsWith);
    }

    /**
     * Verifica se o usuário tem acesso à URL solicitada
     */
    private boolean hasAccess(String tipoUsuario, Cargo cargo, String requestURI) {

        // Cliente tentando acessar área de funcionário
        if (requestURI.startsWith("/funcionario/") && !"FUNCIONARIO".equals(tipoUsuario)) {
            return false;
        }

        // Funcionário tentando acessar área de cliente
        if (requestURI.startsWith("/cliente/") && !"CLIENTE".equals(tipoUsuario)) {
            return false;
        }

        // Verificar acesso a URLs de gerente
        if (GERENTE_URLS.stream().anyMatch(requestURI::startsWith)) {
            return cargo == Cargo.GERENTE;
        }

        // Acesso a URLs comuns de funcionário baseado no cargo
        if (requestURI.startsWith("/funcionario/")) {
            return hasFuncionarioAccess(cargo, requestURI);
        }

        return true;
    }

    /**
     * Verifica acesso para funcionários baseado no cargo
     */
    private boolean hasFuncionarioAccess(Cargo cargo, String requestURI) {
        if (cargo == null) return false;

        switch (cargo) {
            case GERENTE:
                // Gerente tem acesso a tudo
                return true;

            case ATENDENTE:
                // Atendente não pode acessar funcionalidades de gerente
                return !requestURI.contains("/gerente/") &&
                        !requestURI.contains("/cadastro");

            case ESTAGIARIO:
                // Estagiário tem acesso limitado
                return requestURI.startsWith("/funcionario/dashboard") ||
                        requestURI.startsWith("/funcionario/consulta/") ||
                        requestURI.equals("/funcionario/relatorios/basicos");

            default:
                return false;
        }
    }
}