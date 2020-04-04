package com.benjishults.bitnots.util.intern

open class InternTableWithOther<C, O>(val makeNew: (String, O) -> C) {

    protected val table = mutableMapOf<String, Pair<O, C>>()

    fun clear() {
        table.clear()
    }

    fun exists(name: String): Boolean = table.containsKey(name)
    fun existsWith(name: String, other: O): Boolean = table.get(name)?.second == other

    fun intern(name: String, other: O): C {
        table.get(name)?.let {
            if (it.first != other) {
                return newSimilar(name, other)
            }
            return it.second
        } ?: return makeNew(name, other).also {
            table.put(name, other to it)
        }
    }

    tailrec fun newSimilar(baseName: String, other: O): C {
        if (table.get(baseName) !== null) {
            val index = baseName.lastIndexOf('-')
            if (index >= 0) {
                val trailingInt = baseName.substring(index + 1).toIntOrNull()
                if (trailingInt === null) {
                    return newSimilar(baseName + "-0", other)
                } else {
                    return newSimilar("${baseName.substring(0, index)}-${trailingInt + 1}", other)
                }
            } else {
                return newSimilar(baseName + "-0", other)
            }
        } else
            return intern(baseName, other)
    }

}
