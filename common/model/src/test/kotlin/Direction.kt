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
