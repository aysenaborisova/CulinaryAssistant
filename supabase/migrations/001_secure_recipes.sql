alter table public.recipes enable row level security;

create policy "Authenticated users can read recipes"
on public.recipes
for select
to authenticated
using (true);

create policy "Users can create their own recipes"
on public.recipes
for insert
to authenticated
with check (auth.uid() = user_id);

create policy "Users can update their own recipes"
on public.recipes
for update
to authenticated
using (auth.uid() = user_id)
with check (auth.uid() = user_id);

create policy "Users can delete their own recipes"
on public.recipes
for delete
to authenticated
using (auth.uid() = user_id);
