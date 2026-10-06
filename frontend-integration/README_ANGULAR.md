# Integração no Angular existente

Esta pasta contém os arquivos para substituir o Supabase pelo backend Java.

## 1. Remova o uso do Supabase

Você não precisa mais de:

```text
supabase.service.ts
@supabase/supabase-js
```

Depois de migrar completamente, pode remover:

```bash
npm uninstall @supabase/supabase-js
```

## 2. Copie os arquivos

Copie:

```text
frontend-integration/src/app/core/
```

para:

```text
SEU_ANGULAR/src/app/core/
```

Copie o exemplo de environment e ajuste seu arquivo real:

```text
src/environments/environment.ts
```

## 3. environment.ts

Para desenvolvimento:

```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## 4. app.config.ts

Garanta estes providers:

```ts
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth.interceptor';

providers: [
  provideRouter(routes),
  provideHttpClient(
    withInterceptors([authInterceptor])
  )
]
```

## 5. Login

Seu formulário chama:

```ts
const response = await firstValueFrom(
  this.auth.login(email, senha)
);

this.auth.saveSession(response);
```

ou pode fazer isso diretamente no serviço usando `tap`.

## 6. Cadastro

```ts
this.auth.register(nome, email, senha)
```

## 7. Guard

O `auth.guard.ts` verifica se existe JWT em `localStorage`.

## 8. Crianças

Use:

```ts
childrenService.list()
childrenService.create(payload)
childrenService.get(id)
childrenService.update(id, payload)
```

## 9. Ficha pública NFC

A rota Angular pode ser:

```text
/p/:token
```

No componente:

```ts
this.children.getPublic(token)
```

A API pública é somente leitura.

## 10. CORS

Enquanto o Angular estiver em:

```text
http://localhost:4200
```

a API local já permite essa origem.

Na Oracle, altere:

```text
CORS_ALLOWED_ORIGINS=https://seu-dominio.com.br
```
