package com.benjishults.bitnots.dataAccess.term

import com.benjishults.bitnots.model.terms.FreeVariable
import java.sql.Connection

class PersistentFreeVariable(variable: FreeVariable) : PersistentVariable<FreeVariable>(variable) {
    override fun save(connection: Connection, termParentId: Long, formParentId: Long, position: Int) {
        saveHelper(
            connection,
            "free_var_fact",
            "fv_cons",
            variable.cons.name,
            termParentId,
            formParentId,
            position
        )
    }
}