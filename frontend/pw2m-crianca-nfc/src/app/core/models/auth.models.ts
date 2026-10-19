export interface UserResponse {
  id: number;
  name: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
}

export interface AuthResponse {
  token: string;
  user: UserResponse;
}
