package com.benjishults.bitnots.model.unifier

//fun  Term) = linearUnify(UFNode(this), UFNode(other))
//
//fun linearUnify(s: UFNode< Term): Substitution {
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
//                if (linearUnify(UFNode(sTerm.arguments[index]), UFNode(tTerm.arguments[index])) === NotCompatible) {
//                    return NotCompatible
//                } else {
//
//                }
//            }
//            return EmptySub
//        }
//        else -> return NotCompatible
//    }
//}
