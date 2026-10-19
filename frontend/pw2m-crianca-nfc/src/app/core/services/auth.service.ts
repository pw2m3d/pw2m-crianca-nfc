import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, UserResponse } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly tokenKey = 'pw2m_token';
  private readonly userKey = 'pw2m_user';

  constructor(private http: HttpClient) {}

  register(name: string, email: string, password: string) {
    return this.http.post<AuthResponse>(
      `${environment.apiUrl}/auth/register`,
      { name, email, password }
    ).pipe(
      tap(response => this.saveSession(response))
    );
  }

  login(email: string, password: string) {
    return this.http.post<AuthResponse>(
      `${environment.apiUrl}/auth/login`,
      { email, password }
    ).pipe(
      tap(response => this.saveSession(response))
    );
  }

  saveSession(response: AuthResponse) {
    localStorage.setItem(this.tokenKey, response.token);
    localStorage.setItem(this.userKey, JSON.stringify(response.user));
  }

  token(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.token();
  }

  currentUser(): UserResponse | null {
    const raw = localStorage.getItem(this.userKey);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as UserResponse;
    } catch {
      return null;
    }
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'ADMIN';
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
  }
}
