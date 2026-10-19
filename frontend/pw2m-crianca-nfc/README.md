# PW2M Criança NFC — Angular

Aplicação do responsável: login, pessoas protegidas, ativação de chaveiro por PIN e ficha pública.

Documentação completa do produto, da API e do fluxo comercial: [README na raiz do repositório](../../README.md).

## Local

A API precisa estar em `http://localhost:8080`.

```bash
npm install
npm start
```

Abra `http://localhost:4200/`.

`src/environments/environment.ts`:

```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## Rotas

- `/login` e `/cadastro` — JWT em `localStorage`
- `/dashboard` — pessoas da conta
- `/dispositivos` — claim (código + PIN) e lista dos chaveiros
- `/criancas/:id/nfcs` — vincular um chaveiro já ativado à pessoa
- `/d/:deviceToken` — ficha pública pelo código gravado na tag
- `/p/:token` — ficha pública pelo token da pessoa (compatibilidade)

## Build

```bash
npm run build
```
