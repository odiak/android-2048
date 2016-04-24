package net.odiak.game2048

class Game(val nRows: Int, val nColumns: Int) {

    companion object {
        private val NONE = -1
    }

    val rawBoard = Board(nColumns, nRows)

    private val onMoveListeners = mutableSetOf<() -> Unit>()

    init {
        reset()
    }

    fun reset() {
        eachIndex { x, y ->
            rawBoard[x, y] = 0
        }

        val initialPositions = mutableSetOf<Position>()
        while (initialPositions.size < 2) {
            initialPositions.add(Position(randInt(nColumns), randInt(nRows)))
        }

        initialPositions.forEach {
            rawBoard[it] = 2
        }
    }

    fun move(direction: Direction) {
        println("move: ${direction}")

        when (direction) {
            Direction.LEFT -> {
                for (y in 0..nRows - 1) {
                    val row = Array(nColumns) { 0 }
                    var k = 0
                    var lastCombined = false
                    for (x in 0..nColumns - 1) {
                        val v = rawBoard[x, y]
                        if (v == 0) continue
                        if (k > 0 && row[k - 1] == v && !lastCombined) {
                            row[k - 1] = v * 2
                            lastCombined = true
                        } else {
                            row[k] = v
                            k++
                            lastCombined = false
                        }
                    }
                    row.forEachIndexed { x, v ->
                        rawBoard[x, y] = v
                    }
                }
            }
            Direction.UPPER -> {
                for (x in 0..nColumns - 1) {
                    val col = Array(nRows) { 0 }
                    var k = 0
                    var lastCombined = false
                    for (y in 0..nRows - 1) {
                        val v = rawBoard[x, y]
                        if (v == 0) continue
                        if (k > 0 && col[k - 1] == v && !lastCombined) {
                            col[k - 1] = v * 2
                            lastCombined = true
                        } else {
                            col[k] = v
                            k++
                            lastCombined = false
                        }
                    }
                    col.forEachIndexed { y, v ->
                        rawBoard[x, y]
                    }
                }
            }
            Direction.RIGHT -> {
                for (y in 0..nRows - 1) {
                    val row = Array(nColumns) { 0 }
                    var k = 0
                    var lastCombined = false
                    for (x in (nColumns - 1).downTo(0)) {
                        val v = rawBoard[x, y]
                        if (v == 0) continue
                        if (k > 0 && row[k - 1] == v && !lastCombined) {
                            row[k - 1] = v * 2
                            lastCombined = true
                        } else {
                            row[k] = v
                            k++
                            lastCombined = false
                        }
                    }
                    row.forEachIndexed { i, v ->
                        rawBoard[nColumns - i - 1, y] = v
                    }
                }
            }
            Direction.LOWER -> {
                for (x in 0..nColumns - 1) {
                    val col = Array(nRows) { 0 }
                    var k = 0
                    var lastCombined = false
                    for (y in (nRows - 1).downTo(0)) {
                        val v = rawBoard[x, y]
                        if (v == 0) continue
                        if (k > 0 && col[k - 1] == v && !lastCombined) {
                            col[k - 1] = v * 2
                            lastCombined = true
                        } else {
                            col[k] = v
                            k++
                            lastCombined = false
                        }
                    }
                    col.forEachIndexed { i, v ->
                        rawBoard[x, nRows - i - 1]
                    }
                }
            }
        }

        if (isGameOver()) return

        val p = newPosition(direction)
        rawBoard[p] = 2

        onMoveListeners.forEach { it() }
    }

    fun isGameOver(): Boolean {
        eachIndex { x, y ->
            if (rawBoard[x, y] == 0) return false
        }
        return true
    }

    fun addOnMoveListener(f: () -> Unit) {
        onMoveListeners.add(f)
    }

    fun removeOnMoveListener(f: () -> Unit) {
        onMoveListeners.remove(f)
    }

    private inline fun eachIndex(block: (Int, Int) -> Unit) {
        for (y in 0..nRows - 1) {
            for (x in 0..nColumns - 1) {
                block(x, y)
            }
        }
    }

    private fun indices(): List<Position> {
        return mutableListOf<Position>().apply {
            eachIndex { x, y ->
                add(Position(x, y))
            }
        }
    }

    private fun randInt(n: Int) = (Math.random() * n).toInt()

    private fun newPosition(direction: Direction): Position {
        val edgePoints = when (direction) {
            Direction.LEFT -> (0..nRows - 1).map { Position(it, 0) }
            Direction.RIGHT -> (0..nRows - 1).map { Position(it, nColumns - 1) }
            Direction.UPPER -> (0..nColumns - 1).map { Position(nRows - 1, it) }
            Direction.LOWER -> (0..nColumns - 1).map { Position(0, it) }
        }

        edgePoints.shuffle().forEach {
            if (rawBoard[it] == 0) return it
        }

        indices().shuffle().forEach {
            if (rawBoard[it] == 0) return it
        }

        throw IllegalStateException("no position to put a number!")
    }

    private fun <T> List<T>.sample(): T {
        val n = size
        require(n != 0)
        return this[(Math.random() * n).toInt()]
    }

    private fun <T> List<T>.shuffle(): List<T> {
        val shuffled = toMutableList()
        for (i in indices) {
            val r = randInt(i + 1)
            if (r != i) shuffled[i] = shuffled[r]
            shuffled[r] = this[i]
        }
        return shuffled
    }

    enum class Direction {
        LEFT, UPPER, RIGHT, LOWER;
    }

    interface Action {}

    data class Move(val from: Position, val to: Position) : Action

    data class Combine(val from1: Position, val from2: Position, val to: Position) : Action

    data class Add(val position: Position) : Action
}