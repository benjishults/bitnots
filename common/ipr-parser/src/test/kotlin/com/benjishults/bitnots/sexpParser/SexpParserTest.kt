package com.benjishults.bitnots.sexpParser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.fol.equality.Equals
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.FreeVariable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SexpParserTest {

    val p = Pred("p", 1)
    val e = Pred("e", 1)
    val g = Pred("g", 1)
    val s = Pred("s", 2)
    val c = Pred("c", 1)

    val f = Fn("f", 1)

    val a = BoundVariable.intern("a")
    val b = BoundVariable.intern("b")
    val x = BoundVariable.intern("x")
    val x2 = BoundVariable.intern("x2")
    val x3 = BoundVariable.intern("x3")
    val x4 = BoundVariable.intern("x4")
    val y = BoundVariable.intern("y")
    val z = BoundVariable.intern("z")

    val zz = FreeVariable.intern("z")

    val map = mapOf<String, Formula>(
        """
 (forall ((a) (b))      
                 (implies (and (for-some ((x) (y)) (forall ((z))
                                                     (or (= z x) (= z y))))
                               (and (p a) (p b))
                               (not (= a b)))
                   (forall ((x)) (p x))))
 """
                to ForAll(
            a, b,
            formula = Implies(
                And(
                    ForSome(
                        x, y,
                        formula = ForAll(
                            z,
                            formula = Or(
                                Equals(z, x),
                                Equals(z, y)
                            )
                        )
                    ),
                    And(
                        p(a),
                        p(b)
                    ),
                    Not(Equals(a, b))
                ),
                ForAll(x, formula = p(x))
            )
        ),
        "(implies (p z) (p z))" to Implies(p(zz), p(zz)),
        """
   (forall ((a))
    (for-some ((x) (x2) (x3) (x4) (y))
      (implies (and (p a) (e a)
                    (implies (e x)
                      (or (g x) (s x (f x))))
                    (implies (e x2)
                      (or (g x2) (c (f x2)))) 
                    (implies (s a y) (p y))) 
        (or (and (p x3) (g x3)) 
            (and (p x4) (c x4))))))
 """
                to ForAll(
            a,
            formula = ForSome(
                x, x2, x3, x4, y,
                formula = Implies(
                    And(
                        p(a),
                        e(a),
                        Implies(
                            e(x),
                            Or(g(x), s(x, f(x)))
                        ),
                        Implies(e(x2), Or(g(x2), c(f(x2)))),
                        Implies(s(a, y), p(y))
                    ),
                    Or(And(p(x3), g(x3)), And(p(x4), c(x4)))
                )
            )
        ),
        "(implies (p) (q))" to Implies(PropositionalVariable.intern("p"), PropositionalVariable.intern("q"))
    )

    // TODO test error conditions

    @Test
    fun formulaParserTest() {
        map.forEach { string, formula ->
            Assertions.assertEquals(
                SexpParser.parseFormula(
                    SexpTokenizer(string.reader().buffered(), "test"),
                    emptySet()
                ).toString(),
                formula.toString()
            )
            println(formula)

        }
    }
}
