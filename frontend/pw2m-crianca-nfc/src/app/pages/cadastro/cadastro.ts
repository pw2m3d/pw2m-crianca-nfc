import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.scss'
})
export class Cadastro {

  erro = '';
  sucesso = '';
  carregando = false;

  formulario;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {

    this.formulario =
      this.fb.nonNullable.group({

        nome: [
          '',
          [
            Validators.required,
            Validators.minLength(3)
          ]
        ],

        email: [
          '',
          [
            Validators.required,
            Validators.email
          ]
        ],

        senha: [
          '',
          [
            Validators.required,
            Validators.minLength(8)
          ]
        ],

        confirmarSenha: [
          '',
          [
            Validators.required
          ]
        ]

      });

  }

  async cadastrar() {

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const dados =
      this.formulario.getRawValue();

    if (
      dados.senha !==
      dados.confirmarSenha
    ) {

      this.erro =
        'As senhas não coincidem.';

      return;
    }

    this.erro = '';
    this.carregando = true;

    try {

      await firstValueFrom(
        this.auth.register(
          dados.nome,
          dados.email,
          dados.senha
        )
      );

      await this.router.navigate([
        '/dashboard'
      ]);

    } catch (erro: any) {

      console.error(
        'Erro no cadastro:',
        erro
      );

      if (erro.status === 409) {

        this.erro =
          'Este e-mail já está cadastrado.';

      } else {

        this.erro =
          erro?.error?.message ??
          'Não foi possível realizar o cadastro.';

      }

    } finally {

      this.carregando = false;

    }

  }

}