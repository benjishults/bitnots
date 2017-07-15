import java.lang.UnsupportedOperationException

interface Entity {
	fun inSentence(): String
	fun beginSentence(): String
	fun person(): Int
	fun plural(): String
}

data class Character(val inventory: MutableList<Any> = mutableListOf(), var location: Cell? = null, var facing: Direction = Direction.East) : Entity {

	override fun plural() = throw UnsupportedOperationException()

	override fun person() = 2

	override fun inSentence(): String = "you"

	override fun beginSentence(): String = "You"


	fun face(direction: Direction): Character {
		facing = direction
		return this
	}

	fun drop(obj: Entity): Boolean {
		location?.objs.takeIf {
			inventory.remove(obj)
		}?.also {
			it.add(obj)
			return true
		}
		return false
	}

	fun take(obj: Entity): Boolean {
		val loc = location
		if (loc === null)
			return false
		else if (loc.objs.remove(obj)) {
			inventory.add(obj)
			return true
		}
		return false
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
