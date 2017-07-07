enum class Direction(val natural: String) {
	North("North"), South("South"), East("East"), West("West");

	fun right(): Direction {
		return when (this) {
			North -> East
			South -> West
			East -> South
			West -> North
		}
	}

	fun left(): Direction {
		return when (this) {
			North -> West
			South -> East
			East -> North
			West -> South
		}
	}

	fun back(): Direction {
		return when (this) {
			North -> South
			South -> North
			East -> West
			West -> East
		}
	}

}

data class Character(val inventory: MutableList<Any> = mutableListOf(), var location: Cell? = null, var facing: Direction = Direction.East) : Entity {
	override fun person()=2


	override fun inSentence(): String = "you"

	override fun beginSentence(): String = "You"


	fun face(direction: Direction): Character {
		facing = direction
		return this
	}

	fun take(): Character {
		val loc = location
		if (loc === null)
			throw IllegalStateException("You are nowhere!")
		val locobj = loc.obj
		if (locobj === null) {
			throw IllegalStateException("There is nothing here to take!")
		} else {
			inventory.add(locobj)
			loc.obj = null
		}
		return this
	}

	/**
	 * Sets the direction the user is facing to the opposite of the given direction.
	 * @return the receiver
	 * @throws IllegalArgumentException if the direcection is blocked.
	 */
	fun enterFrom(cell: Cell, direction: Direction): Character {
		cell.enterFrom(this, direction)
		location = cell
		facing = direction.back()
		return this
	}

}

interface Entity {
	fun inSentence(): String
	fun beginSentence(): String
	fun person() : Int
}

class Cell(var obj: Entity? = null, val walls: Array<Direction> = arrayOf()) {

	/**
	 * Sets the direction the user is facing to the opposite of the given direction.
	 * @return the receiver
	 * @throws IllegalArgumentException if the direcection is blocked.
	 */
	fun enterFrom(entity: Entity, direction: Direction): Cell {
		if (direction in walls)
			throw IllegalArgumentException("${entity.beginSentence()} bump${if (entity.person() == 3) "s" else ""} into an invisible wall!")
		return this
	}


	fun report(direction: Direction): String {
		val locobj = obj
		return """
You are facing ${direction.natural}.
There is ${if (locobj === null) "nothing" else locobj.inSentence()} here.
You see ${if (direction in walls) "a wall" else "an opening"} ahead of you.
To your left is ${if (direction.left() in walls) "a wall" else "an opening"}.
To your right is ${if (direction.right() in walls) "a wall" else "an opening"}.
"""
	}

}
