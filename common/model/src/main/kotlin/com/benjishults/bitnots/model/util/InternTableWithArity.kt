package com.benjishults.bitnots.model.util

open class InternTableWithArity<C>(val makeNew: (String) -> C) :InternTable(makeNew) {

	private val table = mutableMapOf<String, Pair<C, Int>>()

	fun clear() {
		table.clear()
	}

	fun exists(name: String): Boolean = table.containsKey(name)

	fun intern(name: String, arity: Int): C {
		table.get(name)?.let {
			if (it.second != arity) {
				return new(name, arity)
			}
			return it.first
		} ?: return makeNew(name).also {
			table.put(name, it.to(arity))
		}
	}

	tailrec fun new(baseName: String, arity: Int): C {
		table.get(baseName)?.let {
			val index = baseName.lastIndexOf('-')
			if (index >= 0) {
				val trailingInt = baseName.substring(index + 1).toIntOrNull()
				if (trailingInt === null) {
					return new(baseName + "-0", arity)
				} else {
					return new("${baseName.substring(0, index)}-${trailingInt + 1}", arity)
				}
			} else {
				return new(baseName + "-0", arity)
			}
		} ?: return intern(baseName, arity)
	}

}

