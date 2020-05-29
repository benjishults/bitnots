package com.benjishults.bitnots.util

interface Composable<out S, in T> {
    // operator fun plus(other: Composable): Composable
    operator fun plus(other: T): S
}

/**
 *
 */
interface BranchClosureAttempt {

    // val branchCloser: Boolean
    // val onlyPotentionBranchCloser: Boolean

}

interface ComposableBranchClosureAttempt<out S : BranchClosureAttempt, in T : BranchClosureAttempt> :
    Composable<S, T>, BranchClosureAttempt

/**
 * Kotlin doesn't have sealed interfaces or this would be one.  It should have only [Match] and [NoMatch] inherit from it.
 */
interface AllOrNothing : BranchClosureAttempt { //, Composable<CriticalPairMatchAttempt>

    companion object {
        fun of(booleanValue: Boolean): AllOrNothing =
            Match.takeIf { booleanValue } ?: NoMatch
    }

}

/**
 * A branch closer no matter what else is going on -- not only potentially so.  Generally, combining with anything returns that other thing.
 */
interface Match : AllOrNothing {
    // override val branchCloser: Boolean
    //     get() = true
    // override val onlyPotentionBranchCloser: Boolean
    //     get() = false

    companion object : Match {}
}

/**
 * Not a branch closer nor even potentially so.  Generally combining with anything returns itself.
 */
interface NoMatch : AllOrNothing {
    // override val branchCloser: Boolean
    //     get() = false
    // override val onlyPotentionBranchCloser: Boolean
    //     get() = false

    companion object : NoMatch {}

}

interface PotentialMatch<out S : BranchClosureAttempt, in T : BranchClosureAttempt> :
    ComposableBranchClosureAttempt<S, T> {
    // override val branchCloser: Boolean
    //     get() = false
    // override val onlyPotentionBranchCloser: Boolean
    //     get() = true
}
