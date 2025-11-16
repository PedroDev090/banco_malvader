package com.malvader.banco.controller;

import com.malvader.banco.models.Cliente;
import com.malvader.banco.models.Conta;
import com.malvader.banco.models.Funcionario;
import com.malvader.banco.models.Transacao;
import com.malvader.banco.models.TipoTransacao;
import com.malvader.banco.repository.ClienteRepository;
import com.malvader.banco.repository.TransacaoRepository;
import com.malvader.banco.service.ContaService;
import com.malvader.banco.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class FuncionarioController {

    @Autowired
    private ContaService contaService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    // ====== ABRIR CONTA (ATENDENTE) ======
    @GetMapping("/atendente/abrir-conta")
    public String abrirConta(Model model) {
        model.addAttribute("conta", new Conta());
        return "funcionario/atendente/abrirConta";
    }

    @PostMapping("/atendente/abrir-conta")
    public String salvarConta(
            @ModelAttribute Conta conta,
            RedirectAttributes redirectAttributes
    ) {
        contaService.salvarConta(conta);
        redirectAttributes.addFlashAttribute("mensagem", "Conta aberta com sucesso!");
        return "redirect:/atendente/abrir-conta";
    }

    // ====== CONSULTAR DADOS (CONTA / FUNCIONÁRIO / CLIENTE) ======
    @GetMapping("/atendente/consultar-saldo")
    public String consultarSaldo(
            @RequestParam(value = "numeroConta", required = false) String numeroConta,
            @RequestParam(value = "codigoFuncionario", required = false) String codigoFuncionario,
            @RequestParam(value = "cargo", required = false) String cargo,
            @RequestParam(value = "cpfCliente", required = false) String cpfCliente,
            @RequestParam(value = "aba", required = false) String aba,
            Model model
    ) {
        if (aba == null || aba.isBlank()) {
            aba = "conta";
        }
        model.addAttribute("aba", aba);

        // ---------- CONTA ----------
        if ("conta".equals(aba) && numeroConta != null && !numeroConta.isBlank()) {

            Optional<Conta> contaOpt = contaService.buscarPorNumero(numeroConta);

            if (contaOpt.isPresent()) {
                Conta conta = contaOpt.get();
                model.addAttribute("conta", conta);

                contaService.buscarContaCorrente(conta.getIdConta())
                        .ifPresent(cc -> model.addAttribute("contaCorrente", cc));

                contaService.buscarContaPoupanca(conta.getIdConta())
                        .ifPresent(cp -> model.addAttribute("contaPoupanca", cp));

                contaService.buscarContaInvestimento(conta.getIdConta())
                        .ifPresent(ci -> model.addAttribute("contaInvestimento", ci));

            } else {
                model.addAttribute("mensagemErro",
                        "Conta não encontrada com o número informado.");
            }
        }

        // ---------- FUNCIONÁRIO ----------
        if ("funcionario".equals(aba) && codigoFuncionario != null && !codigoFuncionario.isBlank()) {

            Optional<Funcionario> funcOpt =
                    funcionarioService.buscarPorCodigoECargo(codigoFuncionario, cargo);

            if (funcOpt.isPresent()) {
                Funcionario funcionario = funcOpt.get();
                model.addAttribute("funcionario", funcionario);

                model.addAttribute("numContasAbertasFuncionario",
                        funcionarioService.contarContasAbertas(funcionario));

                model.addAttribute("desempenhoMedioFuncionario",
                        funcionarioService.calcularDesempenho(funcionario));

            } else {
                model.addAttribute("mensagemErroFuncionario",
                        "Funcionário não encontrado para os dados informados.");
            }
        }

        // ---------- CLIENTE ----------
        if ("cliente".equals(aba) && cpfCliente != null && !cpfCliente.isBlank()) {

            Optional<Cliente> clienteOpt =
                    clienteRepository.findByUsuarioCpf(cpfCliente);

            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                model.addAttribute("cliente", cliente);

                model.addAttribute("contasCliente",
                        contaService.buscarContasPorCliente(cliente.getIdCliente()));

            } else {
                model.addAttribute("mensagemErroCliente",
                        "Cliente não encontrado para o CPF informado.");
            }
        }

        return "funcionario/atendente/consultarSaldo";
    }

    // ====== ROTAS DO ATENDENTE ======
    @GetMapping("/atendente/atualizar-dados")
    public String atualizarDados(Model model) {
        return "funcionario/atendente/atualizarDados";
    }

    @PostMapping("/atendente/atualizar-dados")
    public String atualizarDadosConta(
            @RequestParam("numeroConta") String numeroConta,
            @RequestParam("limite") BigDecimal limite,
            @RequestParam("vencimento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimento,
            @RequestParam("taxa") BigDecimal taxa,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Conta> contaOpt = contaService.buscarPorNumero(numeroConta);

        if (contaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErroAtualizar",
                    "Conta não encontrada para o número informado.");
            return "redirect:/atendente/atualizar-dados";
        }

        Conta conta = contaOpt.get();
        // TODO: atualizar dados específicos da conta (corrente/poupança/investimento)

        contaService.salvarConta(conta);

        redirectAttributes.addFlashAttribute("mensagemSucessoAtualizar",
                "Dados da conta atualizados com sucesso!");

        return "redirect:/atendente/atualizar-dados";
    }

    @PostMapping("/atendente/atualizar-cliente")
    public String atualizarDadosCliente(
            @RequestParam("cpfCliente") String cpfCliente,
            @RequestParam("telefone") String telefone,
            @RequestParam("endereco") String endereco,
            @RequestParam("senha") String senha,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Cliente> clienteOpt = clienteRepository.findByUsuarioCpf(cpfCliente);

        if (clienteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErroAtualizarCliente",
                    "Cliente não encontrado para o CPF informado.");
            return "redirect:/atendente/atualizar-dados";
        }

        Cliente cliente = clienteOpt.get();
        cliente.getUsuario().setTelefone(telefone);
        // TODO: atualizar endereço (EnderecoUsuario) e senha (hash) se precisar

        clienteRepository.save(cliente);

        redirectAttributes.addFlashAttribute("mensagemSucessoAtualizarCliente",
                "Dados do cliente atualizados com sucesso!");
        return "redirect:/atendente/atualizar-dados";
    }

    // ====== RELATÓRIOS (ATENDENTE) ======
    @GetMapping("/atendente/relatorios")
    public String relatorios(
            @RequestParam(value = "dataInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @RequestParam(value = "dataFim", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

            @RequestParam(value = "tipoTransacao", required = false) String tipoTransacao,
            @RequestParam(value = "agencia", required = false) String agencia,
            Model model
    ) {
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("tipoTransacao", tipoTransacao);
        model.addAttribute("agencia", agencia);

        List<Transacao> transacoes = List.of();

        if (dataInicio != null && dataFim != null) {

            LocalDateTime inicio = dataInicio.atStartOfDay();
            LocalDateTime fim = dataFim.plusDays(1).atStartOfDay();

            TipoTransacao tipoEnum = null;
            if (tipoTransacao != null && !tipoTransacao.isBlank()) {
                try {
                    tipoEnum = TipoTransacao.valueOf(tipoTransacao);
                } catch (IllegalArgumentException e) {
                    tipoEnum = null;
                }
            }

            Integer idAgencia = null;
            if (agencia != null && !agencia.isBlank()) {
                idAgencia = Integer.parseInt(agencia);
            }

            transacoes = transacaoRepository.buscarPorFiltros(
                    inicio,
                    fim,
                    tipoEnum,
                    idAgencia
            );
        }

        model.addAttribute("transacoes", transacoes);
        model.addAttribute("contasInadimplentes", contaService.buscarInadimplentes());

        return "funcionario/atendente/relatorios";
    }

    // ====== ROTAS DO ESTAGIÁRIO ======
    @GetMapping("/estagiario/consultar-cliente")
    public String consultarClienteEstagiario(Model model) {
        return "funcionario/estagiario/consultarCliente";
    }

    @GetMapping("/estagiario/relatorios")
    public String relatoriosEstagiario(Model model) {
        return "funcionario/estagiario/relatorios";
    }

    @GetMapping("/estagiario/desempenho")
    public String desempenhoEstagiario(Model model) {
        return "funcionario/estagiario/desempenho";
    }

    // ====== DASHBOARD GENÉRICO DO FUNCIONÁRIO ======
    @GetMapping("/funcionario/dashboard")
    public String dashboardFuncionario(Model model) {
        return "funcionario/gerente/dashboard";
    }

    // ====== ROTAS DO GERENTE ======
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
