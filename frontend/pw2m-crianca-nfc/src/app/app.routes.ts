import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then(m => m.Login)
  },
  {
    path: 'cadastro',
    loadComponent: () => import('./pages/cadastro/cadastro').then(m => m.Cadastro)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/dashboard/dashboard').then(m => m.Dashboard)
  },
  {
    path: 'dispositivos',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/dispositivos/dispositivos').then(m => m.Dispositivos)
  },
  {
    path: 'admin/estoque',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./pages/admin-estoque/admin-estoque').then(m => m.AdminEstoque)
  },
  {
    path: 'criancas/nova',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/crianca-form/crianca-form').then(m => m.CriancaForm)
  },
  {
    path: 'criancas/:id/editar',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/crianca-form/crianca-form').then(m => m.CriancaForm)
  },
  {
    path: 'p/:token',
    loadComponent: () => import('./pages/ficha-publica/ficha-publica').then(m => m.FichaPublica)
  },
  {
    path: 'd/:deviceToken',
    loadComponent: () => import('./pages/ficha-publica/ficha-publica').then(m => m.FichaPublica)
  },
  {
    path: 'criancas/:id/nfcs',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/nfc-devices/nfc-devices').then(m => m.NfcDevices)
  },
  { path: '**', redirectTo: 'login' }
];
