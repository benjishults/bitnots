import com.benjishults.bitnots.theory.language.FolLanguage

theory {
    language {
        FolLanguage()
    }
    unDefinedTerms(
            Const("a"),
            Fn("f", 2)
    )
    definedTerms {

    }
    unDefinedPredicates(
            Pred("P", 3),
            Prop("Q")
    )
    axioms(
            ForAll(["x", "y", "z"] Implies(P(x, y, a)))
    )
    definedPredicates {

    }
}

Theory(
    language =        FolLanguage(),
    unDefinedTerms = [
            Const(a),
            Fn(f, 2)
            ],
    definedTerms = [],
    unDefinedPredicates = [
            Pred(P, 3),
            Prop(Q)
            ],
    axioms = [
            Axiom("axiom1", ForAll [x, y, z] Implies(P(x, y, a))))
            ],
    definedPredicates = [
                         Prep(R, 1) = {x -> Implies(P(x,x,x), Q)}
    ]
}

annotatedFormula {
    name { "" }
    role { FormulaRoles.hypothesis }
    formula { }
}

