package com.benjishults.bitnots.dataAccess.dao

import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.TermConstructor


interface FormulaDao {

     fun <T : TermConstructor> insertTerm(term:  Term)

}
