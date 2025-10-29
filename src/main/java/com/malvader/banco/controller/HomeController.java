package com.malvader.banco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Página inicial - redireciona para login
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/auth/login";
    }

    /**
     * Página sobre o sistema (opcional)
     */
    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }
}
