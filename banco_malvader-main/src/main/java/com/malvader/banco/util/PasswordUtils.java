package com.malvader.banco.util;

import org.springframework.util.DigestUtils;
import java.nio.charset.StandardCharsets;

public class PasswordUtils {

    /**
     * Gera hash MD5 compatível com o MySQL
     */
    public static String gerarHashMD5(String senha) {
        return DigestUtils.md5DigestAsHex(senha.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Verifica se a senha corresponde ao hash
     */
    public static boolean verificarSenha(String senha, String hashArmazenado) {
        String hashSenha = gerarHashMD5(senha);
        return hashSenha.equals(hashArmazenado);
    }

    /**
     * Valida força da senha (para cadastro)
     */
    public static boolean validarForcaSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            return false;
        }

        boolean temMaiuscula = senha.matches(".*[A-Z].*");
        boolean temMinuscula = senha.matches(".*[a-z].*");
        boolean temNumero = senha.matches(".*[0-9].*");
        boolean temEspecial = senha.matches(".*[^a-zA-Z0-9].*");

        return temMaiuscula && temMinuscula && temNumero && temEspecial;
    }
}