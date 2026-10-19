import {
  Component,
  OnInit,
  signal
} from '@angular/core';

import {
  ActivatedRoute,
  RouterLink
} from '@angular/router';

import {
  ChildrenService
} from '../../core/services/children.service';

import {
  AuthService
} from '../../core/services/auth.service';

import {
  PublicChild
} from '../../core/models/child.models';

@Component({
  selector: 'app-ficha-publica',
  standalone: true,

  imports: [
    RouterLink
  ],

  templateUrl: './ficha-publica.html',
  styleUrl: './ficha-publica.scss'
})
export class FichaPublica implements OnInit {

  crianca =
    signal<PublicChild | null>(null);

  carregando =
    signal(true);

  erro =
    signal('');

  constructor(
    private route: ActivatedRoute,
    private children: ChildrenService,
    private auth: AuthService
  ) {}


  get responsavelLogado(): boolean {
    return this.auth.isLoggedIn();
  }


  ngOnInit(): void {

    const token =
      this.route.snapshot
        .paramMap
        .get('token');

    const deviceToken =
      this.route.snapshot
        .paramMap
        .get('deviceToken');

    if (!token && !deviceToken) {

      this.erro.set(
        'Ficha não encontrada.'
      );

      this.carregando.set(false);

      return;
    }

    const consulta = deviceToken
      ? this.children.getPublicByDevice(deviceToken)
      : this.children.getPublic(token!);

    consulta
      .subscribe({

        next: dados => {

          this.crianca.set(dados);

          this.carregando.set(false);

        },

        error: erro => {

          console.error(
            'Erro ficha pública:',
            erro
          );

          this.erro.set(
            'Esta ficha não está disponível.'
          );

          this.carregando.set(false);

        }

      });

  }


  telefoneLink(
    telefone?: string | null
  ): string {

    return (
      telefone ?? ''
    ).replace(/\D/g, '');

  }


  whatsappLink(
    whatsapp?: string | null
  ): string {

    let numero =
      this.telefoneLink(
        whatsapp
      );


    if (
      numero.length === 10 ||
      numero.length === 11
    ) {

      numero =
        `55${numero}`;

    }
    return `https://wa.me/${numero}`;
  }
}