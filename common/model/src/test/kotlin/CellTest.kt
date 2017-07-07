import org.junit.Test
import org.junit.Assert


class CellTest {

	val character = Character()
	val northSouthEmptyCell = Cell(walls = arrayOf(Direction.North, Direction.South))
	val duck = object : Entity {
		override fun person() = 3

		override fun inSentence(): String = "a duck"

		override fun beginSentence(): String = "A duck"
	}

	@Test
	fun testObject() {
		northSouthEmptyCell.obj = duck
		character.enterFrom(northSouthEmptyCell, Direction.West).face(Direction.South)
		Assert.assertEquals(northSouthEmptyCell.report(character.facing), """
You are facing South.
There is a duck here.
You see a wall ahead of you.
To your left is an opening.
To your right is an opening.
""")
	}

	@Test
	fun testTake() {
		northSouthEmptyCell.obj = duck
		character.enterFrom(northSouthEmptyCell, Direction.West)
		character.take();
		assert(northSouthEmptyCell.obj === null)
		assert(character.inventory.size == 1)
		assert(character.inventory.firstOrNull() == duck)
	}

	@Test
	fun testReport() {
		character.enterFrom(northSouthEmptyCell, Direction.East)
		Assert.assertEquals(northSouthEmptyCell.report(character.facing), """
You are facing West.
There is nothing here.
You see an opening ahead of you.
To your left is a wall.
To your right is a wall.
""")
		character.face(Direction.North)
		Assert.assertEquals(northSouthEmptyCell.report(character.facing), """
You are facing North.
There is nothing here.
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
		} catch (e: Throwable) {

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

}
