package com.benjishults.bitnots.regression.app.problem

import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor

class ProblemRow(val fileName: String, val descriptor: TptpProblemFileDescriptor, val harness: FolTableauHarness, var lastTime: Long?) {
}
