package com.benjishults.bitnots.dataAccess.dao

import com.benjishults.bitnots.dataAccess.data.Problem
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula

interface ProblemDao {
    fun getByName(name: String): Problem

    fun insertProblem(problem: Problem): Long
    fun insertAnnotatedFormula(annotatedFormula: FolAnnotatedFormula): Long
    
    fun insertAxiomSet()

}