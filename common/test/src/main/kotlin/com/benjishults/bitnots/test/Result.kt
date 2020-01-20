package com.benjishults.bitnots.test

sealed class Result(val name: String)

object Proved : Result("success")

object Failed : Result("failed")

object TimeOut : Result("timeout")

data class Error(val message: String) : Result("error")
