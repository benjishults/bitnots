class Level(val cells: Array<Array<Cell>>) {

	var down: Level? = null
	var up: Level? = null
	var character: Character? = null

	init {
		cells.forEach { it.forEach { it.level = this } }
	}
	
	private fun findCoords(cell:Cell) : Pair<Int,Int>? {
	    var x = 0
		var y = 0
	    return null
	}
	
	fun display() : String {
		val center = character?.location?.also {  } ?: return "Nobody here"
		
		return """

"""
	}


}