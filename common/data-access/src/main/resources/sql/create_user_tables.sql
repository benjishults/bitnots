drop table if exists user;
drop table if exists user_group;

create type root_type as enum (
    'user' -- ,
--    'user_group'
);

create table users (
    user_id uuid generated always as gen_random_uuid() stored primary key,
    user_name text not null,
    user_email text not null
)

create table user_groups (
    user_group_id bigserial primary key,
    user_group_name text null
)

create table user_group_members (
    user_group_id bigint not null references user_groups,
    user_id uuid not null references user
)

create table permissions (
    permission_id bigserial primary key,
    permission_name text not null
)

create table roles (
    role_id bigserial primary key,
    role_name text not null
)

create table role_permissions (
    role_id bigint not null references roles,
    permission_id bigint not null references permissions
)

create table user_group_role_map (
    role_id bigint not null references roles,
    user_group_id not null references user_groups
)

create table policies (
    policy_id bigserial primary key,
    root_id uuid not null,
    can_read boolean not null default false,
    can_write boolean not null default false
)

create table user_group_policy_map (
    policy_id bigint not null references policies,
    user_group_id not null references user_groups
)
