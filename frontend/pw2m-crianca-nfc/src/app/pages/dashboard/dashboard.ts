import {
  Component,
  OnInit,
  signal
} from '@angular/core';

import {
  Router,
  RouterLink
} from '@angular/router';

import {
  AuthService
} from '../../core/services/auth.service';

import {
  ChildrenService
} from '../../core/services/children.service';

import {
  ChildSummary
} from '../../core/models/child.models';

import { firstValueFrom } from 'rxjs';
@Component({
  selector: 'app-dashboard',
  standalone: true,

  imports: [
    RouterLink
  ],

  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {

  criancas = signal<ChildSummary[]>([]);

  carregando = signal(true);

  erro = signal('');

  admin = false;

  constructor(
    private auth: AuthService,
    private childrenService: ChildrenService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.admin = this.auth.isAdmin();
    this.carregar();
  }

  carregar(): void {

    this.carregando.set(true);
    this.erro.set('');

    this.childrenService
      .list()
      .subscribe({

        next: (dados) => {

          console.log(
            'Crianças recebidas:',
            dados
          );

          this.criancas.set(dados);

          this.carregando.set(false);

        },

        error: (erro) => {

          console.error(
            'Erro ao carregar crianças:',
            erro
          );

          this.carregando.set(false);

          if (
            erro.status === 401 ||
            erro.status === 403
          ) {

            this.auth.logout();

            this.router.navigate([
              '/login'
            ]);

            return;
          }

          this.erro.set(
            erro?.error?.message ??
            'Não foi possível carregar as crianças.'
          );

        }

      });

  }

  sair(): void {

    this.auth.logout();

    this.router.navigate([
      '/login'
    ]);

  }
    async excluir(
      id: number,
      nome: string
    ) {

      const confirmou =
        confirm(
          `Deseja realmente excluir a ficha de ${nome}?`
        );

      if (!confirmou) {
        return;
      }

      try {

        await firstValueFrom(
          this.childrenService.delete(id)
        );

        this.criancas.update(
          lista =>
            lista.filter(
              crianca =>
                crianca.id !== id
            )
        );

      } catch (erro) {

        console.error(
          'Erro ao excluir:',
          erro
        );

        this.erro.set(
          'Não foi possível excluir a ficha.'
        );

      }

    }
}