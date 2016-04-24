package net.odiak.game2048

/**
 * Created by kaido on 4/24/16.
 */
class Board(val nColumns: Int, val nRows: Int) {

    private val array = Array(nColumns) { Array(nRows) { 0 } }

    operator fun get(x: Int, y: Int) = array[x][y]

    operator fun get(p: Position) = this[p.x, p.y]

    operator fun set(x: Int, y: Int, v: Int) {
        array[x][y] = v
    }

    operator fun set(p: Position, v: Int) {
        this[p.x, p.y] = v
    }
}