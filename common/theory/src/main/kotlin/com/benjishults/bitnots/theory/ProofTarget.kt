package com.benjishults.bitnots.theory

import com.benjishults.bitnots.model.formulas.Formula

/**
 * A complete description of a conjecture including any needed context.
 */
interface ProofTarget

/**
 * The target of the proof is a single formula.
 */
class FormulaProofTarget(val targetFormula: Formula): ProofTarget {

}

