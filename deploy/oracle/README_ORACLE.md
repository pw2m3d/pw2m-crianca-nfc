# Publicação na Oracle Cloud

Estrutura recomendada:

```text
Internet
   ↓
Nginx / HTTPS
   ├── Angular compilado
   └── /api → Java 25 :8080
                 ↓
            MySQL HeatWave
```

## Variáveis obrigatórias da API

```text
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://ENDERECO_PRIVADO_MYSQL:3306/pw2m_nfc?useSSL=true&serverTimezone=UTC
DB_USERNAME=pw2m_app
DB_PASSWORD=...
JWT_SECRET_BASE64=...
CORS_ALLOWED_ORIGINS=https://nfc.seudominio.com.br
```

## Gerar JWT_SECRET_BASE64

Linux:

```bash
openssl rand -base64 48
```

Não coloque essa chave no GitHub.

## systemd

Use o arquivo:

```text
pw2m-nfc-api.service.example
```

Ajuste caminhos e variáveis antes de instalar.

## Nginx

O frontend Angular pode ser servido como arquivos estáticos.
As requisições `/api/` podem ser encaminhadas para:

```text
http://127.0.0.1:8080
```

Use HTTPS com Let's Encrypt ou outro certificado.
