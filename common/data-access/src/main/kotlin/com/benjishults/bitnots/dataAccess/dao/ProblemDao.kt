package com.benjishults.bitnots.dataAccess.dao

import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.problem.Problem

interface ProblemDao {

//    fun getBySource(source: ProblemSource, detail: String): Problem
//    fun getIdBySource(source: ProblemSource, detail: String): Long
    fun getIdByAnnotatedFormula(annotatedFormula: FolAnnotatedFormula, problemId: Long): Long

    fun insertProblem(problem: Problem): Long
    fun insertAnnotatedFormula(annotatedFormula: FolAnnotatedFormula): Long

    fun insertAxiomSet()

}
