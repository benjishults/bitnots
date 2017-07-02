package com.benjishults.bitnots.model.util

open class InternTable<T, A>(val factory: (String, Array<out A>) -> T) {

	private val table = mutableMapOf<String, T>()

	fun clear() {
		table.clear()
	}

	fun exists(name: String): Boolean = table.containsKey(name)

	fun intern(name: String, vararg others: A): T {
		table.get(name)?.let {
			return it
		} ?: return factory(name, others).also {
			table.put(name, it)
		}
	}

	tailrec fun new(baseName: String, vararg others: A): T {
		if (table.containsKey(baseName)) {
			val index = baseName.lastIndexOf('-')
			if (index >= 0) {
				val trailingInt = baseName.substring(index + 1).toIntOrNull()
				if (trailingInt === null) {
					return new(baseName + "-0", *others)
				} else {
					return new("${baseName.substring(0, index)}-${trailingInt + 1}", *others)
				}
			} else {
				return new(baseName + "-0", *others)
			}
		} else {
			return intern(baseName, *others)
		}
	}

}