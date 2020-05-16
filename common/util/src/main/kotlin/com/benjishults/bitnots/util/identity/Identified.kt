package com.benjishults.bitnots.util.identity

interface Identified

fun <T : Identified> T.id() = this::class.qualifiedName!!
