package com.benjishults.bitnots.model.util

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
                return new(name, other)
            }
            return it.second
        } ?: return makeNew(name, other).also {
            table.put(name, other.to(it))
        }
    }

    tailrec fun new(baseName: String, other: O): C {
        table.get(baseName)?.let {
            val index = baseName.lastIndexOf('-')
            if (index >= 0) {
                val trailingInt = baseName.substring(index + 1).toIntOrNull()
                if (trailingInt === null) {
                    return new(baseName + "-0", other)
                } else {
                    return new("${baseName.substring(0, index)}-${trailingInt + 1}", other)
                }
            } else {
                return new(baseName + "-0", other)
            }
        } ?: return intern(baseName, other)
    }

}

