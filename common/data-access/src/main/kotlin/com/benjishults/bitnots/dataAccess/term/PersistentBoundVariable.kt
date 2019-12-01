package com.benjishults.bitnots.dataAccess.term

import com.benjishults.bitnots.model.terms.BoundVariable
import java.sql.Connection

class PersistentBoundVariable(variable: BoundVariable) : PersistentVariable<BoundVariable>(variable) {
    override fun save(connection: Connection, termParentId: Long, formParentId: Long, position: Int) {
        saveHelper(
            connection,
            "bound_var_fact",
            "bv_cons",
            variable.cons.name,
            termParentId,
            formParentId,
            position
        )
    }
}
