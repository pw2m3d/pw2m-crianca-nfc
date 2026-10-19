# PW2M Criança NFC

Chaveiro NFC vendido pela PW2M: o responsável ativa o produto com um PIN, vincula a uma pessoa protegida e a tag abre uma ficha de emergência somente leitura.

## Arquitetura

```text
frontend/pw2m-crianca-nfc  (Angular 22)
        ↓ HTTPS / JSON + JWT
backend  (Java 25 + Spring Boot 4.1.1)
        ↓ JPA / Flyway
MySQL 8.4+
        ↓ (opcional)
Oracle Cloud
```

Pastas:

| Pasta | Função |
| --- | --- |
| `backend/` | API Spring Boot |
| `frontend/pw2m-crianca-nfc/` | Aplicação Angular |
| `frontend-integration/` | Snippets antigos (não usar) |
| `database/` | Referência; a fonte da verdade é o Flyway |
| `deploy/oracle/` | Exemplo de publicação |

## Fluxo comercial

```text
ADMIN fabrica lote  →  código + PIN na embalagem
        ↓
cliente registra / faz login  (papel CUSTOMER)
        ↓
POST /api/devices/claim  (deviceCode + PIN)
        ↓
cadastra a pessoa protegida
        ↓
PUT /api/devices/{id}/assign
        ↓
scan da tag  →  GET /api/public/devices/{deviceToken}
```

Papéis:

- **CUSTOMER** — cadastro público (`POST /api/auth/register`). Ativa chaveiros, cadastra pessoas e vincula tags.
- **ADMIN** — não nasce pelo cadastro. Defina `ADMIN_EMAIL` e `ADMIN_PASSWORD` na subida da API, ou atualize `users.role` no MySQL. Fabrica lotes em `POST /api/admin/devices/manufacture`.

## Subir localmente

Requisitos: Java 25, Maven 3.6.3+, Node 22+, Docker Desktop (MySQL).

```bash
docker compose up -d mysql
cd backend
mvn spring-boot:run
```

MySQL local: `localhost:3306`, banco `pw2m_nfc`, usuário `pw2m`, senha `pw2m_dev`.

```bash
cd frontend/pw2m-crianca-nfc
npm install
npm start
```

Angular: `http://localhost:4200`  
API: `http://localhost:8080`  
Saúde: `http://localhost:8080/actuator/health`

Primeiro administrador (opcional, só em máquina controlada):

```text
ADMIN_EMAIL=admin@pw2m.com.br
ADMIN_PASSWORD=uma-senha-forte
ADMIN_NAME=Administrador PW2M
```

## Produção

```text
SPRING_PROFILES_ACTIVE=prod
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET_BASE64=
CORS_ALLOWED_ORIGINS=https://seu-dominio.com.br
```

O profile `prod` exige as variáveis acima, desliga Swagger e expõe só `/actuator/health`. Não grave senhas nem JWT no GitHub. Detalhes em [deploy/oracle/README_ORACLE.md](deploy/oracle/README_ORACLE.md).

## Esquema do banco

Não edite `database/schema.sql` para aplicar o banco. Use as migrations em `backend/src/main/resources/db/migration/` (V1 a V4: usuários, pessoas, NFC, estoque, papéis).

## Rotas da API

Autenticação:

```text
POST /api/auth/register
POST /api/auth/login
```

Pessoas protegidas (JWT):

```text
GET    /api/children
POST   /api/children
GET    /api/children/{id}
PUT    /api/children/{id}
DELETE /api/children/{id}
GET    /api/children/{id}/devices
PATCH  /api/children/{id}/devices/{deviceId}/toggle
```

Dispositivos do cliente (JWT):

```text
POST /api/devices/claim
GET  /api/devices
GET  /api/devices/{deviceId}
PUT  /api/devices/{deviceId}/assign
PUT  /api/devices/{deviceId}/unassign
```

Estoque (JWT + ROLE_ADMIN):

```text
POST /api/admin/devices/manufacture
```

Público (sem login):

```text
GET /api/public/devices/{deviceToken}
GET /api/public/children/{publicToken}
GET /api/public/children/{publicToken}/device/{deviceToken}
```

A ficha pública não devolve endereço, e-mail, senha nem IDs do responsável. A URL gravada na tag na fabricação deve ser a do `deviceToken`.

## Angular

Rotas principais:

```text
/login
/cadastro
/dashboard
/dispositivos
/criancas/nova
/criancas/:id/editar
/criancas/:id/nfcs
/p/:token
/d/:deviceToken
```

`environment.apiUrl` aponta para `http://localhost:8080/api`.

## Testes

```bash
cd backend
mvn test
```

Os testes de API usam Testcontainers (Docker). Sem Docker, essa suíte não sobe.
