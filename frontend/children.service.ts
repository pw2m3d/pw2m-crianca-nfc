import { Injectable } from '@angular/core';
import { SupabaseService } from './supabase.service';

export interface ChildInput {
  full_name: string;
  birth_date?: string | null;
  blood_type?: string | null;
  photo_url?: string | null;
  address_line?: string | null;
  city?: string | null;
  state?: string | null;
  public_enabled?: boolean;
}

@Injectable({ providedIn: 'root' })
export class ChildrenService {
  constructor(private supabase: SupabaseService) {}

  async listMine() {
    const { data, error } = await this.supabase.client
      .from('children')
      .select('*')
      .order('created_at', { ascending: false });

    if (error) throw error;
    return data;
  }

  async create(input: ChildInput) {
    const {
      data: { user }
    } = await this.supabase.client.auth.getUser();

    if (!user) {
      throw new Error('Usuário não autenticado.');
    }

    const { data, error } = await this.supabase.client
      .from('children')
      .insert({
        ...input,
        owner_id: user.id
      })
      .select()
      .single();

    if (error) throw error;
    return data;
  }

  async update(childId: string, input: Partial<ChildInput>) {
    const { data, error } = await this.supabase.client
      .from('children')
      .update(input)
      .eq('id', childId)
      .select()
      .single();

    if (error) throw error;
    return data;
  }

  async getPublicByToken(token: string) {
    const { data, error } = await this.supabase.client.rpc(
      'get_public_child',
      { p_token: token }
    );

    if (error) throw error;
    return data;
  }

  async logPublicAccess(token: string) {
    await this.supabase.client.rpc(
      'log_public_child_access',
      { p_token: token }
    );
  }
}
