-- ============================================================
-- PW2M CRIANÇA NFC
-- 03 - FUNÇÕES PÚBLICAS SEGURAS
-- ============================================================
--
-- IMPORTANTE:
-- Não damos SELECT público direto nas tabelas.
-- A pessoa que encosta o celular no NFC chama esta função
-- passando o token da criança.
--
-- A função devolve SOMENTE o conteúdo permitido.
-- Endereço, e-mail, IDs internos e dados administrativos não são
-- devolvidos.
-- ============================================================

create or replace function public.get_public_child(p_token uuid)
returns jsonb
language sql
stable
security definer
set search_path = public
as $$
  select jsonb_build_object(
    'name', c.full_name,

    -- Retorna idade, não a data exata de nascimento.
    'age_years',
      case
        when c.birth_date is null then null
        else extract(year from age(current_date, c.birth_date))::int
      end,

    'blood_type', c.blood_type,
    'photo_url', c.photo_url,

    'medical', jsonb_build_object(
      'allergies', mi.allergies,
      'conditions', mi.conditions_text,
      'medications', mi.medications,
      'health_plan', mi.health_plan,
      'health_plan_number', mi.health_plan_number,
      'emergency_notes', mi.emergency_notes
    ),

    'contacts',
      coalesce(
        (
          select jsonb_agg(
            jsonb_build_object(
              'name', ec.name,
              'relation', ec.relation_label,
              'phone', ec.phone,
              'whatsapp', ec.whatsapp
            )
            order by ec.priority, ec.created_at
          )
          from public.emergency_contacts ec
          where ec.child_id = c.id
        ),
        '[]'::jsonb
      )
  )
  from public.children c
  left join public.medical_info mi
    on mi.child_id = c.id
  where c.public_token = p_token
    and c.public_enabled = true
  limit 1;
$$;

-- Só as funções necessárias ficam executáveis publicamente.
revoke all on function public.get_public_child(uuid) from public;
grant execute on function public.get_public_child(uuid) to anon, authenticated;

-- ============================================================
-- RPC OPCIONAL PARA REGISTRAR ACESSO À FICHA
-- ============================================================

create or replace function public.log_public_child_access(p_token uuid)
returns boolean
language plpgsql
security definer
set search_path = public
as $$
declare
  v_child_id uuid;
begin
  select id into v_child_id
  from public.children
  where public_token = p_token
    and public_enabled = true
  limit 1;

  if v_child_id is null then
    return false;
  end if;

  insert into public.access_logs(child_id)
  values (v_child_id);

  return true;
end;
$$;

revoke all on function public.log_public_child_access(uuid) from public;
grant execute on function public.log_public_child_access(uuid) to anon, authenticated;
