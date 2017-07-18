package com.benjishults.bitnots.model.util

open class InternTable<C>(open val makeNew: (String) -> C) {

	protected val table = mutableMapOf<String, C>()

	fun clear() {
		table.clear()
	}

	fun exists(name: String): Boolean = table.containsKey(name)

	fun intern(name: String): C {
		table.get(name)?.let {
			return it
		} ?: return makeNew(name).also {
			table.put(name, it)
		}
	}

	tailrec fun new(baseName: String): C {
		if (table.containsKey(baseName)) {
			val index = baseName.lastIndexOf('-')
			if (index >= 0) {
				val trailingInt = baseName.substring(index + 1).toIntOrNull()
				if (trailingInt === null) {
					return new(baseName + "-0")
				} else {
					return new("${baseName.substring(0, index)}-${trailingInt + 1}")
				}
			} else {
				return new(baseName + "-0")
			}
		} else {
			return intern(baseName)
		}
	}

}