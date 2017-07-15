import org.junit.Test
import org.junit.Assert


class CellTest {

	val character = Character()

	val northSouthEmptyCell = Cell(walls = arrayOf(Direction.North, Direction.South))
	val northEastEmptyCell = Cell(walls = arrayOf(Direction.North, Direction.East))
	val southEastEmptyCell = Cell(walls = arrayOf(Direction.South, Direction.East))
	val southWestNorthEmptyCell = Cell(walls = arrayOf(Direction.South, Direction.East, Direction.North))

	val level1 = Level(arrayOf(arrayOf(northSouthEmptyCell, northEastEmptyCell), arrayOf(southWestNorthEmptyCell, southEastEmptyCell)))

	val ring = Ring()
	val potion = Potion()
	val meleeWeapon = MeleeWeapon()
	val rangeWeapon = RangedWeapon()
	val chest = Chest()
	val bag = Bag()
	val monster = Monster()

	val duck = object : Entity {
		override fun display() = 'f'

		override fun plural() = "ducks"

		override fun person() = 3

		override fun inSentence(): String = "a duck"

		override fun beginSentence(): String = "A duck"
	}

	val rabbit = object : Entity {
		override fun display() = 'f'

		override fun plural() = "rabbits"
		override fun person() = 3

		override fun inSentence(): String = "a rabbit"

		override fun beginSentence(): String = "A rabbit"
	}

	@Test
	fun testObject() {
		northSouthEmptyCell.objs.add(duck)
		character.enterFrom(northSouthEmptyCell, Direction.West).face(Direction.South)
		Assert.assertEquals(northSouthEmptyCell.report(character.facing), """
You are facing South.
Here, you see a duck.
You see a wall ahead of you.
To your left is an opening.
To your right is an opening.
""")
	}

	@Test
	fun testTake() {
		northSouthEmptyCell.objs.add(duck)
		character.enterFrom(northSouthEmptyCell, Direction.West)
		assert(character.take(duck));
		assert(northSouthEmptyCell.objs.isEmpty())
		assert(character.inventory.size == 1)
		assert(character.inventory.firstOrNull() == duck)
	}

	@Test
	fun testReport() {
		character.enterFrom(northSouthEmptyCell, Direction.East)
		Assert.assertEquals(northSouthEmptyCell.report(character.facing), """
You are facing West.
Here, you see nothing.
You see an opening ahead of you.
To your left is a wall.
To your right is a wall.
""")
		character.face(Direction.North)
		Assert.assertEquals(northSouthEmptyCell.report(character.facing), """
You are facing North.
Here, you see nothing.
You see a wall ahead of you.
To your left is an opening.
To your right is an opening.
""")
	}

	@Test
	fun testFailedEntry() {
		try {
			character.enterFrom(northSouthEmptyCell, Direction.North)
			assert(false) { "you cannot enter through a wall" }
		} catch (e: IllegalStateException) {

		}
	}

	@Test
	fun testEntry() {
		assert(character.enterFrom(northSouthEmptyCell, Direction.East).facing === Direction.West)
	}

	@Test
	fun testFace() {
		character.enterFrom(northSouthEmptyCell, Direction.East)
		assert(character.face(Direction.North).facing === Direction.North)
	}

	@Test
	fun testTakeNothing() {
		assert(!character.take(duck))
		character.enterFrom(northSouthEmptyCell, Direction.West)
		assert(!character.take(rabbit))
	}

	@Test
	fun testTakeAndDrop() {
		northSouthEmptyCell.objs.add(duck)
		character.enterFrom(northSouthEmptyCell, Direction.West)
		assert(character.take(duck));
		assert(northSouthEmptyCell.objs.isEmpty())
		assert(character.inventory.size == 1)
		assert(character.inventory.firstOrNull() == duck)
		assert(character.drop(duck))
		assert(duck in northSouthEmptyCell.objs)
		assert(character.inventory.isEmpty())

	}

	@Test
	fun testTakeTwoAndDrop() {
		northSouthEmptyCell.objs.add(duck)
		northEastEmptyCell.objs.add(rabbit)

		character.enterFrom(northSouthEmptyCell, Direction.West)

		assert(character.take(duck));
		assert(northSouthEmptyCell.objs.isEmpty())
		assert(character.inventory.size == 1)
		assert(character.inventory.firstOrNull() === duck)

		character.enterFrom(northEastEmptyCell, Direction.West)

		assert(character.take(rabbit))
		assert(character.inventory.size == 2)
		assert(northEastEmptyCell.objs.isEmpty())

		character.enterFrom(northSouthEmptyCell, Direction.East)

		assert(character.drop(rabbit))
		assert(rabbit in northSouthEmptyCell.objs)
		assert(character.inventory.size == 1)
		assert(northSouthEmptyCell.objs.size == 1)
		assert(character.inventory.firstOrNull() === duck)

	}

	@Test
	fun testSymbols() {
		val entities = arrayOf<Entity>(duck, rabbit, character, ring, potion, meleeWeapon, rangeWeapon, chest, bag, monster)
		val sb = StringBuilder()
		entities.forEach {
			sb.append(it.display())
			sb.append(" ")
		}
		Assert.assertEquals("f f @ o u / ) [ b m ", sb.toString())
	}

	@Test
	fun testGraphics() {
		for (i in 128.toShort()..254.toShort()) {
			print(i.toShort().toChar())
			print(" ")
		}
	}

	private fun hex(i: Int): Char {
		return when (i) {
			0 -> '0'
			1 -> '1'
			2 -> '2'
			3 -> '3'
			4 -> '4'
			5 -> '5'
			6 -> '6'
			7 -> '7'
			8 -> '8'
			9 -> '9'
			10 -> 'A'
			11 -> 'B'
			12 -> 'C'
			13 -> 'D'
			14 -> 'E'
			15 -> 'F'
			else -> throw ArithmeticException();
		}
	}

	@Test
	fun testMultItemsPerCell() {
		northSouthEmptyCell.objs.add(duck)
		northEastEmptyCell.objs.add(rabbit)

		character.enterFrom(northEastEmptyCell, Direction.West)

		assert(character.take(rabbit))
		assert(character.inventory.size == 1)
		assert(northEastEmptyCell.objs.isEmpty())

		character.enterFrom(northSouthEmptyCell, Direction.East)

		assert(character.drop(rabbit))
		assert(rabbit in northSouthEmptyCell.objs)
		assert(character.inventory.size == 0)
		assert(northSouthEmptyCell.objs.size == 2)
	}

	@Test
	fun testReportMultipleThings() {
		northSouthEmptyCell.objs.add(duck)
		northSouthEmptyCell.objs.add(rabbit)

		character.enterFrom(northSouthEmptyCell, Direction.West)
		Assert.assertEquals("""
You are facing East.
Here, you see 
1) a duck
2) a rabbit
You see an opening ahead of you.
To your left is a wall.
To your right is a wall.
""", northSouthEmptyCell.report(character.facing))
	}

}
