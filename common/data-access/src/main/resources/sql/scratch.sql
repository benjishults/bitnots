
select * from formula_plus_dim;
select * from formula_dim;
select * from formula_role_dim;

select * from problem_dim;

select p.problem_source, p.problem_source_detail, af.formula_name, af.formula_role, f.formula
from problem_dim as p 
natural join problem_formula_plus 
natural join formula_plus_dim af
natural join formula_dim as f
order by p.problem_source_detail;

select *
from problem_dim as p 
natural join problem_formula_plus 
natural join formula_plus_dim 
natural join formula_dim as f;


 insert into formula_dim
 (formula, formula_hash)
 values ("hi", md5(?))
 on conflict (formula_hash)
 do update set
 formula = formula_dim.formula
 returning formula_id
;
