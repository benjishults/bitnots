class Cell(var objs: MutableList<Entity> = mutableListOf(), val walls: Array<Direction> = arrayOf()) {

	var level: Level? = null

	/**
	 * Sets the direction the user is facing to the opposite of the given direction.
	 * @return the receiver
	 * @throws IllegalArgumentException if the direcection is blocked.
	 */
	fun enterFrom(entity: Entity, direction: Direction): Cell {
		if (direction in walls)
			throw IllegalStateException("${entity.beginSentence()} bump${if (entity.person() == 3) "s" else ""} into an invisible wall!")
		return this
	}

	fun report(direction: Direction): String {
		return """
You are facing ${direction.natural}.
Here, you see ${when (objs.size) {0 -> "nothing."
			1 -> objs[0].inSentence() + "."
			else -> {
				var items = StringBuilder()
				for ((index, value) in objs.withIndex()) {
					items.append("\n")
					items.append(index + 1)
					items.append(") ")
					items.append(value.inSentence())
				}
				items.toString()
			}
		}}
You see ${if (direction in walls) "a wall" else "an opening"} ahead of you.
To your left is ${if (direction.left() in walls) "a wall" else "an opening"}.
To your right is ${if (direction.right() in walls) "a wall" else "an opening"}.
"""
	}

}
