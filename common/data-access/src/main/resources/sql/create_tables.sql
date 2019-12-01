drop table if exists problem_axiom_set;
drop table if exists axiom_axiom_set;
drop table if exists problem_formula_plus;
drop table if exists problem_formula_plus_dim;
drop table if exists axiom_set;
drop table if exists axiom_set_dim;
drop table if exists formula_plus_dim;
drop table if exists problem_dim;
drop table if exists problem_source_dim;
drop table if exists formula_role_dim;
drop table if exists formula_dim;

create table formula_dim (
--    formula_id bigserial primary key,
--    language_id bigint 
    formula_hash text primary key,
    formula text not null
);

create table formula_role_dim (
    formula_role text primary key
);

insert into formula_role_dim (formula_role) values
    ('axiom'),
    ('hypothesis'),
    ('definition'),
    ('assumption'),
    ('lemma'),
    ('theorem'),
    ('corollary'),
    ('conjecture'),
    ('negated_conjecture'),
    ('plain'),
    ('type'),
    ('fi_domain'),
    ('fi_functors'),
    ('fi_predicates'),
    ('unknown');

create table problem_source_dim (
    problem_source text primary key
);

insert into problem_source_dim (problem_source) values
('TPTP'),
('bitnots')
;

create table problem_dim (
    problem_id bigserial primary key,
    problem_source text not null default 'bitnots' references problem_source_dim on delete set default on update cascade,
    problem_source_detail text null -- e.g., file name or URI
--    constraint unique_problem_sources
--    exclude (problem_source with =, problem_source_detail with =) where (problem_source_detail is not null)
);

create unique index unique_problem_sources 
on problem_dim 
(problem_source, problem_source_detail) 
where problem_source_detail is not null;

create table formula_plus_dim (
    formula_plus_id bigserial primary key,
    formula_role text not null default 'conjecture' references formula_role_dim on update cascade on delete set default,
    formula_hash text not null references formula_dim on delete cascade,
    formula_name text not null default '',
    problem_id bigint null references problem_dim on delete set null
);

create unique index formula_plus_natural_key 
on formula_plus_dim (formula_hash, problem_id, formula_name, formula_role) 
where (problem_id is not null);

create index 
on formula_plus_dim (formula_role);

create table axiom_set_dim (
    axiom_set_id bigserial primary key,
    axiom_set_name text not null default '',
    axiom_set_source text not null default 'bitnots' references problem_source_dim on delete set default on update cascade,
    axiom_set_source_detail text null -- e.g., file name or URI

);
create unique index on axiom_set_dim (axiom_set_source, axiom_set_name, axiom_set_source_detail)
where axiom_set_source_detail is not null;

create table axiom_axiom_set (
    axiom_set_id bigint not null references axiom_set_dim on delete cascade,
    formula_plus_id bigint not null references formula_plus_dim on delete cascade,
    primary key (axiom_set_id, formula_plus_id)
);

create trigger ensure_axiom_in_axiom_set 
    before insert or update
    on axiom_axiom_set
    for each row
    execute procedure check_axiom_in_axiom_set()
;

create table problem_axiom_set (
    axiom_set_id bigint not null references axiom_set_dim on delete cascade,
    problem_id bigint not null references problem_dim on delete cascade,
    primary key (axiom_set_id, problem_id)

);

create table problem_formula_plus (
    formula_plus_id bigint not null references formula_plus_dim on delete cascade,
    problem_id bigint not null references problem_dim on delete cascade,
    primary key (formula_plus_id, problem_id)

);
