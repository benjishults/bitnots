drop table if exists form_cons;
drop table if exists term_cons;
drop table if exists formula;
drop table if exists term;
drop table if exists bound_var;
drop table if exists free_var;

create table tptp_domain (
    domain text primary key
)

insert into tptp_domain (text) values
    ('SYN'),
    ('TOP'),
    ('NAT'),
    ('PRC')
;

create table tptp_formula_form (
    tptp_formula_form text primary key
)

insert into tptp_formula_form (tptp_formula_form) values
    ('FOF'),
    ('CNF')
;

create table tptp_version (
    tptp_version text primary key
)

insert into tptp_version (tptp_version) values (
    '7.3.0'
);

create table tptp_problem_descriptor (
    tptp_problem_descriptor_id bigserial primary key,
    tptp_version text not null references tptp_version default '7.3.0',
    tptp_domain text not null references tptp_domain,
    number int not null default 0,
    version int not null default 1,
    size int not null default -1,
    tptp_problem_form text not null references tptp_formula_form
)

create table tptp_problem_set (
    tptp_problem_set_id bigserial primary key,
    root_id uuid not null,
    root_type root_type not null default 'user',
    name text not null default cast ( gen_random_uuid() as text ),
    version int not null default 0
)

create table tptp_problem_set_members (
    tptp_problem_set_id bigint references tptp_problem_set,
    tptp_problem_descriptor_id bigint references tptp_problem_descriptor
)

create table prover (
    prover_id bigserial primary key,
    prover_name text not null default cast ( gen_random_uuid() as text ),
    version int not null default 0
)

create table prover_limiter (
    prover_limiter_id bigserial primary key,
    prover_id bigint references prover,
    q_limit bigint not null default 3,
    step_limit bigint not null default -1
)

create table tptp_problem_set_run (
    tptp_problem_set_run_id bigserial primary key,
    tptp_problem_set_id bigint references tptp_problem_set,
    prover_limiter_id bigint references prover_limiter,
    prover_id bigint references prover
)

create type problem_run_status as enum (
    'success',
    'steps',
    'q-limit',
    'timeout',
    'failure',
    'error'
);

create table tptp_problem_set_run_results (
    tptp_problem_set_run_results_id bigserial primary key,
    tptp_problem_descriptor_id bigint not null references tptp_problem_descriptor,
    time interval null,
    status problem_run_status not null default 'error'
)
