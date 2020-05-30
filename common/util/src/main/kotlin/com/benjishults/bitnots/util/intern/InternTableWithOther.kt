package com.benjishults.bitnots.util.intern

import java.util.regex.Pattern

open class InternTableWithOther<C, O>(val factory: (String, O) -> C) {

    protected val table = mutableMapOf<Pair<String, O>, C>()

    companion object {
        val similarPattern: Pattern = Pattern.compile("^(.*_)([0-9]+)$")
    }

    fun clear() {
        table.clear()
    }

    fun existsWith(name: String, other: O): Boolean = table.containsKey(name to other)

    fun intern(name: String, other: O): C =
        table[name to other] ?: factory(name, other).also { new ->
            table[name to other] = new
        }

    tailrec fun newSimilar(baseName: String, other: O): C =
        if (table.containsKey(baseName to other)) {
            val matcher = similarPattern.matcher(baseName)
            if (matcher.find()) {
                newSimilar(
                    matcher.appendTail(StringBuilder().also { sb ->
                        matcher.appendReplacement(
                            sb,
                            matcher.group(1) + (matcher.group(2).toLong() + 1)
                        )
                    }).toString(),
                    other
                )
            } else {
                newSimilar("${baseName}_0", other)
            }
        } else
            intern(baseName, other)

}
