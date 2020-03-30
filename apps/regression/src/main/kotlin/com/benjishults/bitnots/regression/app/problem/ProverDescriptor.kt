package com.benjishults.bitnots.regression.app.problem

import com.benjishults.bitnots.prover.Prover
import com.benjishults.bitnots.prover.finish.ProofInProgress

data class ProverDescriptor<T : ProofInProgress>(val prover: Prover<T>, val version: String)
