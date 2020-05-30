package com.benjishults.bitnots.util.intern

import java.util.regex.Pattern

/**
 * Currently not thread safe.  Associate this with a theory.
 */
open class InternTable<C>(val factory: (String) -> C) {

    protected val table = mutableMapOf<String, C>()

    companion object {
        val similarPattern = Pattern.compile("^(.*_)([0-9]+)$")
    }

    fun clear() {
        table.clear()
    }

    fun exists(name: String): Boolean = table.containsKey(name)

    /**
     * If this name is on the intern table, return the C with that name.  Otherwise, create a new C with the given name.
     */
    fun intern(name: String): C =
        table.get(name) ?: factory(name).also { new ->
            table.put(name, new)
        }

    /**
     * If the given [baseName] is on the intern table, then a new, similar name is chosen that is not on the intern table.
     * A new C with that name is returned.  If [baseName] is not on the on the intern table, then a new C is created with that name and returned.
     */
    tailrec fun newSimilar(baseName: String): C =
        if (table.containsKey(baseName)) {
            val matcher = similarPattern.matcher(baseName)
            if (matcher.find()) {
                newSimilar(
                    matcher.appendTail(StringBuilder().also { sb ->
                        matcher.appendReplacement(
                            sb,
                            matcher.group(1) + (matcher.group(2).toLong() + 1)
                        )
                    }).toString()
                )
            } else {
                newSimilar("${baseName}_0")
            }
        } else {
            intern(baseName)
        }

}
