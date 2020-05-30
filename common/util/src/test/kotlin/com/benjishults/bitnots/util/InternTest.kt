package com.benjishults.bitnots.util

import com.benjishults.bitnots.util.intern.InternTable
import com.benjishults.bitnots.util.intern.InternTableWithOther
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InternTest {

    @Test
    fun `test newSimilar`() {
        val internTable = InternTable<String> { it }
        Assertions.assertEquals("a", internTable.newSimilar("a"))
        Assertions.assertEquals("a_0", internTable.newSimilar("a"))
        Assertions.assertEquals("a_1", internTable.newSimilar("a"))
        Assertions.assertEquals("a_2", internTable.newSimilar("a_0"))
        Assertions.assertEquals("a_0_", internTable.newSimilar("a_0_"))
        Assertions.assertEquals("a_0__0", internTable.newSimilar("a_0_"))
        Assertions.assertEquals("a_0__1", internTable.newSimilar("a_0_"))
    }

    @Test
    fun `test arity`() {
        val internTable = InternTableWithOther<String, Int> { string, arity -> string  }
        Assertions.assertEquals("a", internTable.newSimilar("a", 3))
        Assertions.assertEquals("a" , internTable.newSimilar("a", 2))
        Assertions.assertEquals("a_0" , internTable.newSimilar("a", 3))
        Assertions.assertEquals("a_1" , internTable.newSimilar("a", 3))
        Assertions.assertEquals("a_2" , internTable.newSimilar("a_0", 3))
        Assertions.assertEquals("a_0" , internTable.newSimilar("a_0", 2))
        Assertions.assertEquals("a_0_" , internTable.newSimilar("a_0_", 3))
        Assertions.assertEquals("a_0__0" , internTable.newSimilar("a_0_", 3))
        Assertions.assertEquals("a_0__1" , internTable.newSimilar("a_0_", 3))
    }

}
