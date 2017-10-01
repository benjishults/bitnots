
create function check_axiom_in_axiom_set() return trigger as
$BODY$
BEGIN
	IF (select formula_role from formula_plus f where f.formula_plus_id = new.formula_plus_id) <> 'axiom' then
    	RAISE EXCEPTION 'formula_plus % is not an axiom', NEW.formula_plus_id;
	end if;
	return new;
END;
$BODY$ LANGUAGE 'plpgsql';
