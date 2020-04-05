package particles

typealias Cells = Array<Array<Cell>>

class Grid(val width: Int, val height: Int) {

    private val cells: Cells = create()
    val widthIndices = IntRange(0, width-1)
    val heightIndices = IntRange(0, height-1)

    private fun create(): Cells {
        return Array(height) { Array(width) { EmptyCell() as Cell } }
    }

    fun clear() {
        for (i in cells.indices) {
            for (j in cells[i].indices) {
                cells[i][j] = EmptyCell()
            }
        }
    }

    fun inBounds(i: Int, j: Int) = i in 0 until height && j in 0 until width

    operator fun get(i: Int, j: Int) = if (inBounds(i, j)) cells[i][j] else null

    operator fun set(i: Int, j: Int, cell: Cell){
        if (inBounds(i, j)){
            cells[i][j] = cell
        }
    }

    fun swap(i1: Int, j1: Int, i2: Int, j2: Int) {
        if (inBounds(i1, j1) && inBounds(i2, j2)) {
            val cell = cells[i1][j1]
            cells[i1][j1] = cells[i2][j2]
            cells[i2][j2] = cell
        }
    }
}