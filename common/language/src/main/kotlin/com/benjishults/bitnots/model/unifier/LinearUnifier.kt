package com.benjishults.bitnots.model.unifier

import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.util.UFNode

//fun Term<*>.linearUnify(other: Term<*>) = linearUnify(UFNode(this), UFNode(other))
//
//fun linearUnify(s: UFNode<Term<*>>, t: UFNode<Term<*>>): Substitution {
//    val tTerm = t.x
//    val sTerm = s.x
//    when {
//        s === t -> return EmptySub
//        s.x is FreeVariable -> {
//            s.parent = t
//            return Sub(s.x to t.x)
//        }
//        t.x is FreeVariable -> {
//            t.parent = s
//            return Sub(t.x to s.x)
//        }
//        tTerm is Function && sTerm is Function && tTerm.cons === sTerm.cons -> {
//            s.union(t)
//            for (index in 0..sTerm.arguments.lastIndex) {
//                if (linearUnify(UFNode(sTerm.arguments[index]), UFNode(tTerm.arguments[index])) === NotUnifiable) {
//                    return NotUnifiable
//                } else {
//                    
//                }
//            }
//            return EmptySub
//        }
//        else -> return NotUnifiable
//    }
//}
