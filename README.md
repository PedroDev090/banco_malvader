Alunos: Eduarda de Oliveira Bernardino, Pedro Henrique Fernandes, Matheus Martins Rios e Thiago Litiery Campelo Moreira
Professor: Malvezzi
Matéria: Laboratório de Banco de Dados


README
SISTEMA BANCO MALVADER

1) DESCRIÇÃO DO PROJETO
O Banco Malvader é um sistema acadêmico completo para gerenciamento de operações bancárias, desenvolvido com foco em arquitetura organizada, boas práticas de programação e modelagem profissional de banco de dados.
O sistema permite:
•	Cadastro e gestão de usuários, clientes e funcionários;
•	Cadastro de agências e seus endereços;
•	Abertura, auditoria, acompanhamento e encerramento de contas bancárias;
•	Movimentações como depósitos, saques, transferências, taxas e rendimentos;
•	Geração de relatórios internos;
•	Registro de eventos por meio de auditoria e histórico;
•	Manipulação dos dados via procedures, triggers, views e funções internas;
•	Armazenamento estruturado e seguro em MySQL.
O objetivo do projeto é apresentar uma solução completa e realista para um sistema bancário, integrando conhecimentos de banco de dados, regras de negócio e modelagem ER.
O projeto foi dividido entre os integrantes em Back-end, Front-end e Modelagem de Banco, resultando em um sistema funcional e completo.

2) TECNOLOGIAS UTILIZADAS
2.1) Back-end
•	Java
•	Spring Boot
•	MySQL Connector
2.2) Front-end
•	Estrutura baseada em Views e Controllers
•	 Thymeleaf
•	Linguagem de marcação: HTML e CSS
2.3) Banco de Dados
•	MySQL Workbench
•	Modelagem ER completa
•	Stored Procedures, Views e Triggers avançadas
2.4) Ferramentas e dependências opcionais: 
•	Git e Github

3) PRÉ-REQUISITOS
•	Java 
•	MySQL Server e MySQL Workbench
•	Banco de dados criado previamente com o script fornecido

4) CONFIGURAÇÃO DO BANCO DE DADOS
O script inclui:
4.1) Integridade Referencial Completa
•	PKs e FKs estruturadas
•	ON UPDATE/DELETE padrão (restritivo)
•	Índices nos campos de pesquisa
4.2) Regras de Negócio Duras (Triggers)
Exemplos:
•	Limite de depósito diário
•	Atualização automática de saldo
•	Auditoria automática ao abrir conta
•	Proibição de editar senha sem procedure
•	Limite de funcionários por agência
4.3) Procedures de Operações Internas
•	Alterar senha
•	Calcular score de crédito
•	Encerrar conta com validação
•	Aplicar taxa por saque excessivo
4.4) Funções internas
•	Algoritmo de Luhn
•	Gerador de conta bancária
4.5) Views
•	Resumo de contas por cliente
•	Movimentações recentes (últimos 90 dias)

5) FUNCIONALIDADES DO SISTEMA
5.1) Autenticação
•	Login de funcionários e clientes
•	Hash de senha com MD5
•	Validação via procedure
5.2) Usuários e Funcionários
•	Cadastro
•	Atualização
•	Associação a agências
•	Hierarquia (supervisor → subordinado)
5.3) Clientes
•	Geração automática de score
•	Vínculo com contas
•	Endereços associados
5.4) Contas Bancárias
•	Corrente
•	Poupança
•	Investimento
•	Número gerado automaticamente
•	Status: ativa, bloqueada, encerrada
5.5) Operações Bancárias
•	Depósito
•	Saque
•	Transferência
•	Taxas
•	Rendimento
Regras garantidas por triggers:
•	Limite diário de depósito
•	Consistência entre origem/destino
•	Atualização automática de saldo
5.6) Relatórios
•	Movimentações dos últimos 90 dias
•	Resumo de contas
•	Auditoria de abertura
•	Histórico de encerramento
5.7) Dashboard
•	Dados agregados
•	Indicadores do banco
•	Informações rápidas para gerentes
5.8) Interface Gráfica
•	Tela de Login
•	Dashboard inicial
•	Pagina de Clientes
•	Pagina de Funcionários
•	Transações
•	Relatórios
•	Configurações

CONCLUSÃO
O Sistema Bancário Malvader oferece uma arquitetura robusta, segura, documentada e totalmente estruturada. Possui:
•	Banco de dados profissional
•	Back-end modular em Java
•	Front-end flexível
•	Modelagem ER correta
•	Controle de operações bancárias realistas
O projeto atende integralmente os requisitos da disciplina e demonstra a capacidade da equipe em desenvolver um sistema completo de nível profissional.

