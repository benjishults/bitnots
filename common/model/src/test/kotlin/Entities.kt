import java.lang.UnsupportedOperationException

interface Entity {
	fun inSentence(): String
	fun beginSentence(): String
	fun display(): Char
	fun person(): Int
	fun plural(): String
}

class Ring : Entity {
	override fun inSentence() = "a ring"

	override fun beginSentence() = "A ring"

	override fun display() = 'o'

	override fun person() = 3

	override fun plural() = "rings"
}

class Potion : Entity {
	override fun inSentence() = "a potion"

	override fun beginSentence() = "A potion"

	override fun display() = 'u'

	override fun person() = 3

	override fun plural() = "potions"
}

class MeleeWeapon : Entity {
	override fun inSentence() = "a melee weapon"

	override fun beginSentence() = "A melee weapon"

	override fun display() = '/'

	override fun person() = 3

	override fun plural() = "melee weapons"
}

class RangedWeapon : Entity {
	override fun inSentence() = "a ranged weapon"

	override fun beginSentence() = "A ranged weapon"

	override fun display() = ')'

	override fun person() = 3

	override fun plural() = "ranged weapons"
}

class Chest : Entity {
	override fun inSentence() = "a chest"

	override fun beginSentence() = "A chest"

	override fun display() = '['

	override fun person() = 3

	override fun plural() = "chests"
}

class Bag : Entity {
	override fun inSentence() = "a bag"

	override fun beginSentence() = "A bag"

	override fun display() = 'b'

	override fun person() = 3

	override fun plural() = "bags"
}

class Monster : Entity {
	override fun inSentence() = "a monster"

	override fun beginSentence() = "A monster"

	override fun display() = 'm'

	override fun person() = 3

	override fun plural() = "monsters"
}

data class Character(val inventory: MutableList<Any> = mutableListOf(), var location: Cell? = null, var facing: Direction = Direction.East) : Entity {

	override fun display() = '@'

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

	fun take(number: Int): Boolean {
		try {
			location?.let {
				it.objs.removeAt(number - 1).let {
					inventory.add(it)
					return true
				}
			}
		} catch (e: Exception) {
		}
		return false
	}

	fun take(): Boolean {
		location?.let {
			it.objs.takeIf {
				it.size == 1
			}?.let {
				inventory.add(it.first())
				return true
			}
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
