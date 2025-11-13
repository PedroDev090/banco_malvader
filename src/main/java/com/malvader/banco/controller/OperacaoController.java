package com.malvader.banco.controller;

import com.malvader.banco.dto.OperacaoDTO;
import com.malvader.banco.dto.PixDTO;
import com.malvader.banco.dto.SaldoResponseDTO;
import com.malvader.banco.dto.TransacaoResponseDTO;
import com.malvader.banco.models.Transacao;
import com.malvader.banco.service.OperacaoService;
import com.malvader.banco.service.ContaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/operacoes")
public class OperacaoController {

    @Autowired
    private OperacaoService operacaoService;

    @Autowired
    private ContaService contaService;

    /**
     * Página inicial de operações
     */
    @GetMapping
    public String operacoesPage(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("operacaoDTO", new OperacaoDTO());
        model.addAttribute("pixDTO", new PixDTO());
        model.addAttribute("paginaAtiva", "operacoes");

        return "operacoes/index";
    }

    /**
     * Realizar operação bancária
     */
    @PostMapping("/realizar")
    public String realizarOperacao(@Valid @ModelAttribute OperacaoDTO operacaoDTO,
                                   BindingResult result,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("pixDTO", new PixDTO());
            model.addAttribute("paginaAtiva", "operacoes");
            return "operacoes/index";
        }

        try {
            Transacao transacao = operacaoService.realizarOperacao(
                    operacaoDTO.getIdContaOrigem(),
                    operacaoDTO.getIdContaDestino(),
                    com.malvader.banco.models.TipoTransacao.valueOf(operacaoDTO.getTipoOperacao()),
                    operacaoDTO.getValor(),
                    operacaoDTO.getDescricao()
            );

            redirectAttributes.addFlashAttribute("sucesso",
                    "Operação realizada com sucesso! ID: " + transacao.getIdTransacao());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao realizar operação: " + e.getMessage());
        }

        return "redirect:/operacoes";
    }

    /**
     * Realizar transferência PIX
     */
    @PostMapping("/pix")
    public String realizarPix(@Valid @ModelAttribute PixDTO pixDTO,
                              BindingResult result,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("erro", "Dados do PIX inválidos");
            return "redirect:/operacoes";
        }

        try {
            Transacao transacao = operacaoService.realizarPix(
                    pixDTO.getIdContaOrigem(),
                    pixDTO.getChavePix(),
                    pixDTO.getValor(),
                    pixDTO.getDescricao()
            );

            redirectAttributes.addFlashAttribute("sucesso",
                    "PIX realizado com sucesso! ID: " + transacao.getIdTransacao());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao realizar PIX: " + e.getMessage());
        }

        return "redirect:/operacoes";
    }

    /**
     * Consultar saldo
     */
    @GetMapping("/saldo")
    public String consultarSaldo(@RequestParam Integer idConta,
                                 HttpSession session,
                                 Model model) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        try {
            BigDecimal saldo = operacaoService.consultarSaldo(idConta);

            // Buscar informações da conta para o response
            var contaOpt = contaService.buscarPorNumero(idConta.toString());
            if (contaOpt.isPresent()) {
                var conta = contaOpt.get();
                SaldoResponseDTO saldoResponse = new SaldoResponseDTO(
                        conta.getNumeroConta(),
                        saldo,
                        BigDecimal.ZERO, // Em uma implementação real, buscaríamos o limite
                        conta.getStatus().name()
                );
                model.addAttribute("saldo", saldoResponse);
            }

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao consultar saldo: " + e.getMessage());
        }

        model.addAttribute("paginaAtiva", "saldo");
        return "operacoes/saldo";
    }

    /**
     * Página de extrato
     */
    @GetMapping("/extrato")
    public String extratoPage(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("paginaAtiva", "extrato");
        return "operacoes/extrato";
    }

    /**
     * API para buscar extrato (AJAX)
     */
    @GetMapping("/extrato/api")
    @ResponseBody
    public Object buscarExtrato(@RequestParam Integer idConta,
                                @RequestParam String dataInicio,
                                @RequestParam String dataFim,
                                HttpSession session) {

        if (session.getAttribute("usuarioLogado") == null) {
            return java.util.Map.of("erro", "Usuário não autenticado");
        }

        try {
            // Converter datas (simplificado - em produção usar DateTimeFormatter)
            // var inicio = LocalDateTime.parse(dataInicio);
            // var fim = LocalDateTime.parse(dataFim);

            // var extrato = operacaoService.consultarExtratoPeriodo(idConta, inicio, fim);
            // return extrato;

            return java.util.Map.of("mensagem", "Extrato em desenvolvimento");

        } catch (Exception e) {
            return java.util.Map.of("erro", "Erro ao buscar extrato: " + e.getMessage());
        }
    }
}