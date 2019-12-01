drop table if exists form_cons_dim;
drop table if exists term_cons_dim;
drop table if exists formula_fact;
drop table if exists term_fact;
drop table if exists bound_var_fact;
drop table if exists free_var_fact;

create table form_cons_dim (
    form_cons_id bigserial primary key,
    form_cons text not null,
    arity smallint null,
    -- index of bound variable argument
    bound smallint not null default 0
);

create unique index unique_formula_constructor
on form_cons_dim
(form_cons, arity)
where arity is not null;

create table term_cons_dim (
    term_cons_id bigserial primary key,
    term_cons text not null,
    arity smallint null,
    -- index of bound variable argument
    bound smallint not null default 0
);

create unique index unique_term_constructor
on term_cons_dim
(term_cons, arity)
where arity is not null;

create table formula_fact (
    formula_fact_id bigserial primary key,
    form_cons_id bigint not null references form_cons_dim,
    parent_form_fact bigint null, -- references formula_fact,
    parent_term_fact bigint null, -- references term_fact,
    position smallint null
);

create table term_fact (
    term_fact_id bigserial primary key,
    term_cons_id bigint not null references term_cons_dim,
    parent_form_fact bigint null, -- references formula_fact,
    parent_term_fact bigint null, -- references term_fact,
    position smallint null
);

create table bound_var_fact (
    bv_id bigserial primary key,
    bv_cons text not null,
    parent_form_fact bigint null,
    parent_term_fact bigint null,
    position smallint not null
);

create table free_var_fact (
    fv_id bigserial primary key,
    fv_cons text not null,
    parent_form_fact bigint null,
    parent_term_fact bigint null,
    position smallint not null
);

insert into form_cons_dim (form_cons, arity, bound) values
    ('all', 2, 1),
    ('exists', 2, 1),
    ('and', null, 0),
    ('or', null, 0),
    ('implies', 2, 0),
    ('iff', null, 0),
    ('not', 1, 0);

insert into term_cons_dim (term_cons, arity, bound) values
    ('set-of-all', 2, 1);
