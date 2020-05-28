package com.benjishults.bitnots.proofService.context.model

import com.benjishults.bitnots.util.identity.Identified
import kotlinx.serialization.Serializable

@Serializable
class ProvingContext(override val id: String): Identified by Identified {

}
