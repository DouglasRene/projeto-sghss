Sistema de Gestão Hospitalar e Saúde (SGHSS)
Um sistema robusto e modular para a gestão de clínicas e instituições de saúde, construído com Spring Boot. O SGHSS foi projetado para otimizar processos administrativos e clínicos, oferecendo uma base sólida para o gerenciamento de pacientes, profissionais, consultas, históricos médicos e exames laboratoriais, com foco em segurança e escalabilidade.

🚀 Funcionalidades Atuais
Esta versão do SGHSS oferece as seguintes capacidades:

Autenticação e Autorização (Spring Security + JWT):

Login seguro de usuários e emissão de JSON Web Tokens (JWT) para controle de acesso.

Controle de Acesso Baseado em Papéis (RBAC), com perfis ADMIN, PROFISSIONAL_SAUDE e PACIENTE.

Gerenciamento de Usuários:

Operações CRUD completas para administrar contas de usuário, incluindo associação de perfis.

Gerenciamento de Pacientes:

Cadastro e manutenção de informações detalhadas de pacientes.

Validação de dados (CPF, e-mail) e associação opcional a uma conta de usuário.

Gerenciamento de Profissionais de Saúde:

Registro e manutenção de dados de médicos e outros profissionais, incluindo especialidade e CRM.

Associação opcional a uma conta de usuário.

Gestão de Consultas Médicas:

Agendamento de consultas com associação a pacientes e profissionais.

Gestão do status da consulta (agendada, concluída, cancelada).

Regras de acesso para pacientes agendarem e cancelarem suas próprias consultas.

Histórico Médico do Paciente:

Registro de entradas no histórico (diagnósticos, sintomas, tratamentos) vinculadas a pacientes e profissionais que registraram.

Acesso seguro, permitindo a pacientes visualizarem apenas seu próprio histórico.

Gerenciamento de Exames Laboratoriais:

Cadastro de Tipos de Exames: Definição de exames como "Hemograma", "Glicemia", com valores de referência.

Registro de Exames por Paciente: Armazenamento de resultados de exames específicos, incluindo laboratório, data e link para documentos.

Auditoria de Registro: Rastreamento do usuário que solicitou/registrou o exame.

Acesso controlado, com pacientes visualizando apenas seus próprios resultados.

Sistema de Logs Avançado:

Configuração robusta de logs com Logback, para console e arquivos rotacionados.

Logs de Auditoria dedicados, registrando ações críticas de usuários para rastreabilidade e segurança.

Suporte para logs estruturados em JSON (via logstash-logback-encoder), facilitando a integração com ferramentas de monitoramento como ELK Stack.

🛠️ Tecnologias Utilizadas
O projeto foi desenvolvido utilizando as seguintes tecnologias:

Java 17+: Linguagem de programação principal.

Spring Boot 3.x: Framework base para a aplicação.

Spring Security: Para autenticação e autorização, incluindo JWT.

Spring Data JPA: Simplificação do acesso a dados com JPA.

Hibernate: Implementação de referência do JPA.

PostgreSQL: Sistema de gerenciamento de banco de dados relacional.

Maven: Ferramenta de automação de build e gerenciamento de dependências.

Logback: Sistema de logging padrão do Spring Boot.

Logstash Logback Encoder: Para logs em formato JSON.

Jakarta Validation (Bean Validation): Para validação de dados em DTOs.

Swagger / OpenAPI (com SpringDoc OpenAPI): Para documentação interativa da API.

🚀 Como Executar o Projeto
Para configurar e rodar o SGHSS em seu ambiente local, siga os passos abaixo:

Pré-requisitos
Java Development Kit (JDK) 17 ou superior

Apache Maven 3.x

PostgreSQL: Instância local ou remota do banco de dados.

Git: Para clonar o repositório.

Configuração do Banco de Dados
Crie um banco de dados PostgreSQL para o projeto (ex: sghss_db).

Crie um usuário e senha para o banco de dados (ex: sghss_user, sghss_password).

Atualize o arquivo src/main/resources/application.properties com suas credenciais do banco de dados:

Properties

# Database Configuration (Mysql)
spring.datasource.url=jdbc:mysql://localhost:3306/sghss_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=developer
spring.datasource.password=developer
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

Configuração de Logs (Opcional, mas Recomendado)
Se você estiver usando o logback-spring.xml para configurações avançadas de logging (JSON, logs de auditoria separados), certifique-se de que o arquivo esteja em src/main/resources/. As pastas logs serão criadas automaticamente na raiz do projeto onde a aplicação for executada.

Executando a Aplicação
Clone o repositório:

Bash

git clone https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git
cd SEU_REPOSITORIO
(Lembre-se de substituir SEU_USUARIO e SEU_REPOSITORIO pelo seu próprio usuário e nome do repositório no GitHub)

Compile o projeto com Maven:

Bash

mvn clean install
Execute a aplicação:

Bash

mvn spring-boot:run
Ou, se você tem um JAR executável gerado por mvn package:

Bash

java -jar target/sghss-0.0.1-SNAPSHOT.jar # O nome do arquivo JAR pode variar
A aplicação será iniciada na porta padrão 8080 (ou na porta configurada em application.properties).

📖 Documentação da API (Swagger UI)
Uma vez que a aplicação esteja em execução, você pode acessar a documentação interativa da API gerada automaticamente pelo Swagger/OpenAPI:

URL: http://localhost:8080/swagger-ui.html

Esta interface permite que você visualize todos os endpoints disponíveis, seus parâmetros, modelos de requisição/resposta e realize testes diretamente no navegador.

🧪 Testes
O projeto foi projetado com a segurança em mente, utilizando Spring Security para controle de acesso baseado em papéis. É altamente recomendável que você realize os testes detalhados para cada endpoint e perfil de usuário:

ADMIN: Acesso total (CRUD em todos os módulos).

PROFISSIONAL_SAUDE: Acesso a informações de pacientes, profissionais, gerenciamento de consultas, histórico e exames (com restrições em operações de deleção e criação de tipos de exames).

PACIENTE: Acesso restrito apenas aos seus próprios dados (paciente, consultas, histórico, exames) e visualização de tipos de exames e profissionais.

Utilize ferramentas como Postman ou Insomnia para simular as requisições HTTP e validar as respostas e os códigos de status. Monitore os logs gerados para verificar o comportamento da aplicação e os registros de auditoria.

🤝 Contribuição
Contribuições são bem-vindas! Se você tiver sugestões, encontrar bugs ou quiser adicionar novas funcionalidades, sinta-se à vontade para:

Fazer um fork do repositório.

Criar uma nova branch (git checkout -b feature/sua-feature).

Realizar suas alterações.

Fazer um commit (git commit -m 'feat: Adiciona nova funcionalidade X').

Fazer um push para a branch (git push origin feature/sua-feature).

Abrir um Pull Request.

📄 Licença
Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para mais detalhes.

