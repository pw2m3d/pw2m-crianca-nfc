-- ============================================================
-- PW2M CRIANÇA NFC
-- 02 - ROW LEVEL SECURITY
-- ============================================================

alter table public.profiles enable row level security;
alter table public.children enable row level security;
alter table public.emergency_contacts enable row level security;
alter table public.medical_info enable row level security;
alter table public.nfc_devices enable row level security;
alter table public.access_logs enable row level security;

-- ------------------------------------------------------------
-- PROFILES
-- ------------------------------------------------------------

drop policy if exists "profile_select_own" on public.profiles;
create policy "profile_select_own"
on public.profiles
for select
to authenticated
using (id = auth.uid());

drop policy if exists "profile_update_own" on public.profiles;
create policy "profile_update_own"
on public.profiles
for update
to authenticated
using (id = auth.uid())
with check (id = auth.uid());

-- ------------------------------------------------------------
-- CHILDREN
-- ------------------------------------------------------------

drop policy if exists "children_select_own" on public.children;
create policy "children_select_own"
on public.children
for select
to authenticated
using (owner_id = auth.uid());

drop policy if exists "children_insert_own" on public.children;
create policy "children_insert_own"
on public.children
for insert
to authenticated
with check (owner_id = auth.uid());

drop policy if exists "children_update_own" on public.children;
create policy "children_update_own"
on public.children
for update
to authenticated
using (owner_id = auth.uid())
with check (owner_id = auth.uid());

drop policy if exists "children_delete_own" on public.children;
create policy "children_delete_own"
on public.children
for delete
to authenticated
using (owner_id = auth.uid());

-- ------------------------------------------------------------
-- EMERGENCY_CONTACTS
-- ------------------------------------------------------------

drop policy if exists "contacts_select_own" on public.emergency_contacts;
create policy "contacts_select_own"
on public.emergency_contacts
for select
to authenticated
using (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

drop policy if exists "contacts_insert_own" on public.emergency_contacts;
create policy "contacts_insert_own"
on public.emergency_contacts
for insert
to authenticated
with check (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

drop policy if exists "contacts_update_own" on public.emergency_contacts;
create policy "contacts_update_own"
on public.emergency_contacts
for update
to authenticated
using (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
)
with check (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

drop policy if exists "contacts_delete_own" on public.emergency_contacts;
create policy "contacts_delete_own"
on public.emergency_contacts
for delete
to authenticated
using (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

-- ------------------------------------------------------------
-- MEDICAL_INFO
-- ------------------------------------------------------------

drop policy if exists "medical_select_own" on public.medical_info;
create policy "medical_select_own"
on public.medical_info
for select
to authenticated
using (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

drop policy if exists "medical_insert_own" on public.medical_info;
create policy "medical_insert_own"
on public.medical_info
for insert
to authenticated
with check (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

drop policy if exists "medical_update_own" on public.medical_info;
create policy "medical_update_own"
on public.medical_info
for update
to authenticated
using (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
)
with check (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

drop policy if exists "medical_delete_own" on public.medical_info;
create policy "medical_delete_own"
on public.medical_info
for delete
to authenticated
using (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

-- ------------------------------------------------------------
-- NFC_DEVICES
-- ------------------------------------------------------------

drop policy if exists "nfc_select_own" on public.nfc_devices;
create policy "nfc_select_own"
on public.nfc_devices
for select
to authenticated
using (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

drop policy if exists "nfc_insert_own" on public.nfc_devices;
create policy "nfc_insert_own"
on public.nfc_devices
for insert
to authenticated
with check (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

drop policy if exists "nfc_update_own" on public.nfc_devices;
create policy "nfc_update_own"
on public.nfc_devices
for update
to authenticated
using (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
)
with check (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

drop policy if exists "nfc_delete_own" on public.nfc_devices;
create policy "nfc_delete_own"
on public.nfc_devices
for delete
to authenticated
using (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);

-- ------------------------------------------------------------
-- ACCESS_LOGS
-- Responsável consegue consultar os acessos das próprias crianças.
-- O INSERT público será feito apenas por uma RPC segura.
-- ------------------------------------------------------------

drop policy if exists "logs_select_own" on public.access_logs;
create policy "logs_select_own"
on public.access_logs
for select
to authenticated
using (
  exists (
    select 1
    from public.children c
    where c.id = child_id
      and c.owner_id = auth.uid()
  )
);
