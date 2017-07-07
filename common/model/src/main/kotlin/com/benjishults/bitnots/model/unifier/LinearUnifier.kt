package com.benjishults.bitnots.model.unifier

import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.model.util.UFNode

//fun Term<*>.linearUnify(other: Term<*>) = linearUnify(UFNode(this), UFNode(other))

private fun linearUnify(s: UFNode<Term<*>>, t: UFNode<Term<*>>): Substitution {
	when {
		s === t -> return EmptySub
		s.x is FreeVariable -> {
			s.parent = t
			return Sub(s.x.to(t.x))
		}
		t.x is FreeVariable -> {
			t.parent = s
			return Sub(t.x.to(s.x))
		}
		t.x is Function && s.x is Function && t.x.cons === s.x.cons -> {
			s.union(t)
			for (index in 0..s.x.arguments.lastIndex) {
				if (linearUnify(UFNode(s.x.arguments[index]), UFNode(t.x.arguments[index])) === NotUnifiable) {
					return NotUnifiable
				} else {
					
				}
			}
			return EmptySub
		}
		else -> return NotUnifiable
	}
}

