package com.benjishults.bitnots.prover

//
// interface ProofConstraints<in Pf : ProofInProgress, in Pv: Prover<*>> {
//
//     suspend fun prove(prover: Pv, proofInProgress: Pf): ProofProgressIndicator
//
// }
//
// interface LimitedTimeProofConstraints<in Pf : ProofInProgress, in Pv: Prover<*>> : ProofConstraints<Pf, Pv> {
//     val millis: Long
//     val decoratedProofConstraints: ProofConstraints<Pf, Pv>
//
//     override suspend fun prove(prover: Pv, proofInProgress: Pf): ProofProgressIndicator {
//         return try {
//             withTimeout(millis) {
//                 decoratedProofConstraints.prove(prover, proofInProgress)
//             }
//         } catch (e: TimeoutCancellationException) {
//             TimeOutProofIndicator(millis)
//         }
//     }
//
// }
