package com.benjishults.bitnots.inference

import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.VarArgPropositionalFormula
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SignedFormulaFactoryTest {

    @Test
    fun `test signed formula factory registration`() {

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            SignedFormulaFactory.registerBuilder(And::class, { _, _ -> TODO() })
        }

        class TestForm(override val constructor: PropositionalFormulaConstructor) :
            VarArgPropositionalFormula(constructor)

        Assertions.assertThrows(NullPointerException::class.java) {
            SignedFormulaFactory.createSignedFormula(TestForm(PropositionalFormulaConstructor.intern("hi")))
        }

        SignedFormulaFactory.registerBuilder(TestForm::class, { _, _ -> TODO() })

        Assertions.assertThrows(NotImplementedError::class.java) {
            SignedFormulaFactory.createSignedFormula(TestForm(PropositionalFormulaConstructor.intern("hi")))
        }
    }
}
