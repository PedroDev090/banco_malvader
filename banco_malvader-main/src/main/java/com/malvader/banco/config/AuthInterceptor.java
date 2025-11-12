package com.malvader.banco.config;

import com.malvader.banco.models.Cargo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    // URLs públicas que não precisam de autenticação
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/auth/login",
            "/auth/acesso-negado",   // importante!
            "/auth/verificar-cpf",
            "/",
            "/sobre",
            "/error",
            "/favicon.ico",
            "/css/",
            "/js/",
            "/images/",
            "/webjars/"
    );

    // URLs que exigem GERENTE
    private static final List<String> GERENTE_URLS = Arrays.asList(
            "/funcionario/gerente/",
            "/funcionario/cadastro",
            "/funcionario/relatorios/avancados"
    );

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String ctx = request.getContextPath();   // ex.: "/app" se houver context-path
        String uri = request.getRequestURI();    // ex.: "/app/clientes/consultar"
        HttpSession session = request.getSession(false);

        log.info("[AUTH] ctx='{}' uri='{}'", ctx, uri);

        if (isPublicUrl(ctx, uri)) {
            log.info("[AUTH] liberado (public) -> {}", uri);
            return true;
        }

        boolean logado = session != null && session.getAttribute("usuarioLogado") != null;
        if (!logado) {
            String target = ctx + "/auth/login";
            log.warn("[AUTH] não logado -> redirect {}", target);
            response.sendRedirect(target);
            return false;
        }

        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        Cargo cargo = (Cargo) session.getAttribute("cargo");

        if (!hasAccess(tipoUsuario, cargo, uri, ctx)) {
            String target = ctx + "/auth/acesso-negado";
            log.warn("[AUTH] sem acesso (tipo={}, cargo={}) -> redirect {}", tipoUsuario, cargo, target);
            response.sendRedirect(target);
            return false;
        }

        log.info("[AUTH] OK -> {}", uri);
        return true;
    }

    /** Verifica se a URL é pública (normalizando contra o context-path) */
    private boolean isPublicUrl(String ctx, String uri) {
        String path = uri.startsWith(ctx) ? uri.substring(ctx.length()) : uri;
        return PUBLIC_URLS.stream().anyMatch(path::startsWith)
                || "/error".equals(path) || "/".equals(path);
    }

    /** Regras de acesso por tipo/cargo e caminho */
    private boolean hasAccess(String tipoUsuario, Cargo cargo, String uri, String ctx) {
        String path = uri.startsWith(ctx) ? uri.substring(ctx.length()) : uri;

        // Cliente tentando acessar área de funcionário
        if (path.startsWith("/funcionario/") && !"FUNCIONARIO".equals(tipoUsuario)) {
            return false;
        }

        // Funcionário tentando acessar área de cliente
        if (path.startsWith("/cliente/") && !"CLIENTE".equals(tipoUsuario)) {
            return false;
        }

        // URLs exclusivas de GERENTE
        if (GERENTE_URLS.stream().anyMatch(path::startsWith)) {
            return cargo == Cargo.GERENTE;
        }

        // Área de funcionário (demais casos)
        if (path.startsWith("/funcionario/")) {
            return hasFuncionarioAccess(cargo, path);
        }

        // /clientes/**: o próprio controller já valida tipo/cargo; aqui liberamos
        return true;
    }

    /** Regras específicas por cargo dentro de /funcionario/**/
    private boolean hasFuncionarioAccess(Cargo cargo, String path) {
        if (cargo == null) return false;

        switch (cargo) {
            case GERENTE:
                return true;
            case ATENDENTE:
                // Atendente não acessa rotas de gerente ou de cadastro
                return !path.contains("/gerente/") && !path.contains("/cadastro");
            case ESTAGIARIO:
                // Estagiário: acesso bem limitado
                return path.startsWith("/funcionario/dashboard")
                        || path.startsWith("/funcionario/consulta/")
                        || path.equals("/funcionario/relatorios/basicos");
            default:
                return false;
        }
    }
}
