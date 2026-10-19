import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {

  erro = '';
  carregando = false;

  formulario;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.formulario = this.fb.nonNullable.group({
      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],
      senha: [
        '',
        Validators.required
      ]
    });
  }

  async entrar() {

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.erro = '';
    this.carregando = true;

    const dados =
      this.formulario.getRawValue();

    try {

      await firstValueFrom(
        this.auth.login(
          dados.email,
          dados.senha
        )
      );

      await this.router.navigate([
        '/dashboard'
      ]);

    } catch (erro: any) {

      console.error(
        'Erro no login:',
        erro
      );

      if (erro.status === 401 ||
          erro.status === 403) {

        this.erro =
          'E-mail ou senha inválidos.';

      } else {

        this.erro =
          erro?.error?.message ??
          'Não foi possível entrar no sistema.';

      }

    } finally {

      this.carregando = false;

    }
  }
}