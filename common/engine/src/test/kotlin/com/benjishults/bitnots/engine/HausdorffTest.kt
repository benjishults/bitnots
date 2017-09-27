package com.benjishults.bitnots.engine

import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.terms.BV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.theory.Claim

class HausdorffTest {

    val Hausdorff = Pred("Hausdorff", 1)
    val OpenIn = Pred("OpenIn", 2)
    val FunctionFromTo = Pred("FunctionFromTo", 3)
    val inverseImage = Fn("inverseImage", 2)
    val ClosedIn = Pred("ClosedIn", 2)
    val Disjoint = Pred("Disjoint", 2)

    val Member = Pred("Member", 2)

/*
(def-predicate (closed-in a b)
  (open-in (the-set-difference-of b a) b)
  (format "~a is a closed subset of ~a"))
 
    (def-predicate (a-hausdorff-top-space x)
      (forall ((a) (b))
        (implies (and (a-member-of a (coerce-to-class x))
              (a-member-of b (coerce-to-class x))
              (not (= a b)))
          (for-some ((g1) (g2))
        (and (open-in g1 x) (open-in g2 x)
             (a-member-of a g1) (a-member-of b g2)
             (disjoint g1 g2)))))
      (string "the definition of a Hausdorff space")
      (format "~a is a Hausdorff space"))
    */
    val diagonalTop = Fn("diagonalTop", 1)
    val graphOf = Fn("graphOf", 1)
    val domain = Fn("domain", 1)
    val productTop = Fn("productTop", 2)
    val crossProduct = Fn("crossProduct", 2)
    val apply = Fn("apply", 2)
    val pair = Fn("pair", 2)
    val toClass = Fn("toClass", 1)

    val y = BV("y")
    val x = BV("x")
    val a = BV("a")
    val b = BV("b")
    val g = BV("g")

/*    var claim = Claim(Implies(
            And(
                    //(def-theorem closed-subset-thm
//(implies (forall ((y)) (implies (and (a-member-of y (coerce-to-class x))
//                   (not (a-member-of y a)))
//           (for-some ((g))
//             (and (a-member-of y g)
//              (open-in g x)
//              (disjoint g a)))))
//(closed-in a x))
//(string "a simple theorem about closed sets"))
                    ForAll(a, formula = Implies(ForAll(y, formula = Implies(
                            And(
                                    Member(y, toClass(x)),
                                    Not(Member(y, a))),
                            ForSome(g, formula = And(
                                    Member(y, g),
                                    OpenIn(g, x),
                                    Disjoint(g, a))))),
                            ClosedIn(a, x))),
                    ForAll(x, formula = Implies(
                            Hausdorff(x),
                            ForAll(a, b, formula = Implies(
                                    And(
                                            Member(a, toClass(x)),
                                            Member(b, toClass(x)))
                            )
                            )
                    ),
                            )


            ))
*/
}
/*

(def-theorem hausdorff
(implies (a-hausdorff-top-space x)
(forall ((a) (b))
  (implies (and (a-member-of a (coerce-to-class x))
        (a-member-of b (coerce-to-class x))
        (not (= a b)))
(for-some ((g1) (g2))
  (and (open-in g1 x) (open-in g2 x)
       (a-member-of a g1) (a-member-of b g2)
       (disjoint g1 g2))))))
(string "the definition of a Hausdorff space"))

(def-theorem product-of-open-sets
(implies (and (open-in a x) (open-in b y))
(open-in (the-product-of a b) (the-product-top-space-of x y)))
(string "a basic fact about the product topology"))

(def-theorem product-top        ; a definition
(implies (a-member-of x (coerce-to-class (the-product-top-space-of s t)))
(for-some ((a) (b))
  (and (a-member-of a (coerce-to-class s))
   (a-member-of b (coerce-to-class t))
   (= x (the-ordered-pair a b)))))
(string "a basic fact about product topologies"))

(def-theorem product            ; a definition
(iff (a-member-of x (the-product-of s t))
   (for-some ((a) (b))
 (and (a-member-of a s) (a-member-of b t)
      (= x (the-ordered-pair a b)))))
(string "a basic fact about products"))

(def-predicate (disjoint a b)
(not (for-some ((y)) (and (a-member-of y a) (a-member-of y b))))
(string "the definition of disjoint")
(format "~a and ~a are disjoint"))

(def-theorem ordered-pair
(implies (= (the-ordered-pair a b) (the-ordered-pair c d))
(and (= a c) (= b d)))
(string "a basic fact about ordered pairs"))

(def-theorem diagonal-top
(iff (a-member-of x (coerce-to-class (the-diagonal-top s)))
   (for-some ((a))
 (and (a-member-of a (coerce-to-class s))
      (= x (the-ordered-pair a a)))))
(string "the definition of the diagonal"))

;;; from Abraham, Marsden, Raitu
;;; Manifolds, Tensor Analysis and Applications
(def-target challenge-AMR-1-4-4
(implies (a-hausdorff-top-space S)
(closed-in (coerce-to-class (the-diagonal-top S))
       (the-product-top-space-of S S)))
(string "Proposition 1.4.4 in A/M/R"))


*/
