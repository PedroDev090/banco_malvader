package com.malvader.banco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @GetMapping("/dashboard")
    public String dashboardCliente() {
        return "clientes/menuCliente";
    }

    @GetMapping("/saque")
    public String paginaSaque() {
        return "clientes/saque";
    }

    @GetMapping("/deposito")
    public String paginaDeposito() {
        return "clientes/deposito";
    }

    @GetMapping("/transferencia")
    public String paginaTransferencia() {
        return "clientes/transferencia";
    }

    @GetMapping("/saldo")
    public String paginaSaldo() {
        return "clientes/saldo";
    }


    @GetMapping("/extrato")
    public String paginaExtrato() {
        return "clientes/extrato";
    }
}
