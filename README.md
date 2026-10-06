# PW2M Criança NFC — Angular + Java 25 + Spring Boot + MySQL

Projeto-base para substituir o Supabase por uma API própria.

## Arquitetura

```text
Angular 22
   ↓ HTTPS / JSON
Java 25 + Spring Boot 4.1.1
   ↓ JPA / Hibernate
MySQL 8.4+
   ↓
Oracle Cloud
```

O GitHub fica responsável pelo versionamento do código. O banco e a API podem ser publicados na Oracle Cloud.

## O que já está implementado no backend

- cadastro de responsável;
- login por e-mail e senha;
- senha armazenada com BCrypt;
- autenticação JWT;
- cadastro, consulta, alteração e exclusão de criança;
- dados médicos;
- contatos de emergência;
- token público permanente para o NFC;
- endpoint público somente leitura;
- cadastro/desativação de tags NFC adicionais;
- registro básico de acessos públicos;
- CORS configurável;
- migrations Flyway;
- MySQL;
- endpoint `/actuator/health`.

## Rotas principais

```text
POST   /api/auth/register
POST   /api/auth/login

GET    /api/children
POST   /api/children
GET    /api/children/{id}
PUT    /api/children/{id}
DELETE /api/children/{id}

POST   /api/children/{id}/devices
PATCH  /api/children/{id}/devices/{deviceId}/toggle

GET    /api/public/children/{publicToken}
GET    /api/public/children/{publicToken}/device/{deviceToken}
```

A rota pública NÃO retorna endereço residencial, e-mail, senha ou IDs administrativos do responsável.

## Requisitos locais

- Java 25
- Maven 3.6.3+
- Docker Desktop (opcional, recomendado para subir MySQL local)
- Angular já pode continuar no projeto existente

Spring Boot 4.1.1 suporta Java 25.

## 1. Subir o MySQL local

Na raiz:

```bash
docker compose up -d mysql
```

Banco local:

```text
host: localhost
porta: 3306
database: pw2m_nfc
usuário: pw2m
senha: pw2m_dev
```

## 2. Rodar a API

Entre:

```bash
cd backend
```

Execute:

```bash
mvn spring-boot:run
```

API:

```text
http://localhost:8080
```

Teste:

```text
http://localhost:8080/actuator/health
```

## 3. Testar cadastro

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Responsavel Teste\",\"email\":\"teste@pw2m.com.br\",\"password\":\"12345678\"}"
```

A resposta contém um JWT.

## 4. Integração com seu Angular existente

Abra:

```text
frontend-integration/README_ANGULAR.md
```

Os arquivos desta pasta substituem o uso do Supabase no frontend.

## 5. Produção

Use o profile:

```text
SPRING_PROFILES_ACTIVE=prod
```

e configure as variáveis:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET_BASE64
CORS_ALLOWED_ORIGINS
```

Nunca coloque senha do banco ou `JWT_SECRET_BASE64` no GitHub.

## NFC

Cada criança recebe um `publicToken` no primeiro cadastro.

Exemplo:

```text
0f75dbb5-4aef-4801-8cd9-70e763db09a1
```

O Angular pode usar:

```text
https://nfc.seudominio.com/p/0f75dbb5-4aef-4801-8cd9-70e763db09a1
```

Esse link é gravado uma vez na tag. Mesmo que o responsável altere telefone, alergias ou medicamentos, o token permanece igual.
