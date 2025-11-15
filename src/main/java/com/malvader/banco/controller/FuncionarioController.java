package com.malvader.banco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FuncionarioController {

    @GetMapping("/atendente/abrir-conta")
    public String abrirConta(Model model) {
        return "funcionario/atendente/abrirConta";
    }
    @GetMapping("/atendente/consultar-saldo")
    public String consultarSaldo(Model model) {
        return "funcionario/atendente/consultarSaldo"; // caminho correto do template
    }
    @GetMapping("/atendente/atualizar-dados")
    public String atualizarDados(Model model) {
        return "funcionario/atendente/atualizarDados"; // caminho do template
    }
    @GetMapping("/atendente/relatorios")
    public String relatorios(Model model) {
        return "funcionario/atendente/relatorios"; // caminho do template
    }
    @GetMapping("/estagiario/consultar-cliente")
    public String consultarClienteEstagiario(Model model) {
        return "funcionario/estagiario/consultarCliente"; // caminho do template
    }

    @GetMapping("/estagiario/relatorios")
    public String relatoriosEstagiario(Model model) {
        return "funcionario/estagiario/relatorios"; // caminho do template
    }
    @GetMapping("/estagiario/desempenho")
    public String desempenhoEstagiario(Model model) {
        return "funcionario/estagiario/desempenho"; // caminho do template
    }


    // ROTAS DO GERENTE
    @GetMapping("/gerente/dashboard")
    public String dashboardGerente(Model model) {
        return "funcionario/gerente/dashboard";
    }

    @GetMapping("/gerente/cadastro")
    public String cadastrarFuncionario(Model model) {
        return "funcionario/gerente/cadastroFuncionario";
    }

    @GetMapping("/gerente/abrir-conta")
    public String abrirContaGerente(Model model) {
        return "funcionario/gerente/abrirConta";
    }

    @GetMapping("/gerente/atualizar-dados")
    public String atualizarDadosGerente(Model model) {
        return "funcionario/gerente/atualizarDados";
    }

    @GetMapping("/gerente/consultar-dados")
    public String consultarDadosGerente(Model model) {
        return "funcionario/gerente/consultarDados";
    }

    @GetMapping("/gerente/encerrar-conta")
    public String encerrarContaGerente(Model model) {
        return "funcionario/gerente/encerrarConta";
    }

    @GetMapping("/gerente/funcionarios")
    public String gerenciarFuncionarios(Model model) {
        return "funcionario/gerente/funcionarios";
    }

    @GetMapping("/gerente/configuracoes")
    public String configuracoesSistema(Model model) {
        return "funcionario/gerente/configuracoes";
    }

    @GetMapping("/gerente/relatorios")
    public String relatoriosAvancados(Model model) {
        return "funcionario/gerente/relatorios";
    }

    @GetMapping("/gerente/auditoria")
    public String auditoria(Model model) {
        return "funcionario/gerente/auditoria";
    }
}