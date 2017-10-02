package com.benjishults.bitnots.dataAccess.dao

import com.benjishults.bitnots.dataAccess.data.Problem

interface ProblemDao {
    fun getByName(name:String) : Problem
}