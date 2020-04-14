package diffusion

import processing.core.PApplet
import kotlin.math.pow

const val GRID_WIDTH = 300
const val GRID_HEIGHT = 160

const val RECT_W = 5
const val RECT_H = 5

const val CELL_SIZE = 5

const val DIFFUSION_RATE_A = 1f
const val DIFFUSION_RATE_B = 0.5f
const val FEED = 0.055f
const val KILL = 0.062f

const val DELTA_TIME = 1f


class Program : PApplet() {

    private var grid = Grid(GRID_WIDTH, GRID_HEIGHT)
    private var next = Grid(GRID_WIDTH, GRID_HEIGHT)

    inner class Grid(val width: Int, val height: Int) {

        private val grid = Array(GRID_HEIGHT) { Array(GRID_WIDTH) { Chemicals(1f, 0f) } }

        fun laplacian(i: Int, j: Int): Pair<Float, Float> {
            var resA = 0f
            resA += this.getA(i, j) * -1f
            resA += this.getA(i, j - 1) * 0.2f
            resA += this.getA(i, j + 1) * 0.2f
            resA += this.getA(i - 1, j) * 0.2f
            resA += this.getA(i + 1, j) * 0.2f

            resA += this.getA(i - 1, j - 1) * 0.05f
            resA += this.getA(i - 1, j + 1) * 0.05f
            resA += this.getA(i + 1, j + 1) * 0.05f
            resA += this.getA(i + 1, j - 1) * 0.05f

            var resB = 0f
            resB += this.getB(i, j) * -1f
            resB += this.getB(i, j - 1) * 0.2f
            resB += this.getB(i, j + 1) * 0.2f
            resB += this.getB(i - 1, j) * 0.2f
            resB += this.getB(i + 1, j) * 0.2f

            resB += this.getB(i - 1, j - 1) * 0.05f
            resB += this.getB(i - 1, j + 1) * 0.05f
            resB += this.getB(i + 1, j + 1) * 0.05f
            resB += this.getB(i + 1, j - 1) * 0.05f
            return Pair(resA, resB)
        }

        fun getCell(i: Int, j: Int): Chemicals {
            return if (j in 0 until height && i in 0 until width) {
                grid[j][i]
            } else {
                throw ArrayIndexOutOfBoundsException("attention")
            }
        }

        fun getA(i: Int, j: Int): Float {
            return if (j in 0 until height && i in 0 until width) {
                grid[j][i].A
            } else {
                1f
            }
        }

        fun setA(i: Int, j: Int, a: Float) {
            if (j in 0 until height && i in 0 until width) {
                grid[j][i].A = a
            }
        }

        fun getB(i: Int, j: Int): Float {
            return if (j in 0 until height && i in 0 until width) {
                grid[j][i].B
            } else {
                0f
            }
        }

        fun setB(i: Int, j: Int, b: Float) {
            if (j in 0 until height && i in 0 until width) {
                grid[j][i].B = b
            }
        }
    }

    inner class Chemicals(var A: Float, var B: Float) {
        fun show(x: Int, y: Int) {
            val colorA = color(220, 220, 220)
            val colorB = color(0, 0, 0)
            val colorCell = lerpColor(colorA, colorB, B / (A + B))
            fill(colorCell)
            rect(x.toFloat(), y.toFloat(), CELL_SIZE.toFloat(), CELL_SIZE.toFloat())
        }
    }

    override fun settings() {
        size(1500, 800)
    }

    override fun setup() {
        noStroke()
        fillCircle()
    }

    override fun draw() {
        for (i in 0 until GRID_WIDTH) {
            for (j in 0 until GRID_HEIGHT) {
                val A = grid.getA(i, j)
                val B = grid.getB(i, j)
                val L = grid.laplacian(i, j)
                next.setA(i, j, A + (DIFFUSION_RATE_A * L.first - A * B * B + FEED * (1 - A)) * DELTA_TIME)
                next.setB(i, j, B + (DIFFUSION_RATE_B * L.second + A * B * B - (KILL + FEED) * B) * DELTA_TIME)
            }
        }
        val temp = grid
        grid = next
        next = temp

        for (i in 0 until GRID_WIDTH) {
            for (j in 0 until GRID_HEIGHT) {
                val cell = grid.getCell(i, j)
                cell.show(i * CELL_SIZE, j * CELL_SIZE)
            }
        }

    }

    fun run() {
        runSketch()
    }

    fun fillRect() {
        for (i in -RECT_W/2 until RECT_W/2) {
            for (j in -RECT_H/2 until RECT_H/2) {
                grid.setA(GRID_WIDTH / 2 + i, GRID_HEIGHT / 2 + j, 0f)
                grid.setB(GRID_WIDTH / 2 + i, GRID_HEIGHT / 2 + j, 1f)
            }
        }
    }

    private fun fillCircle() {
        for (i in -RECT_W/2 until RECT_W/2) {
            for (j in -RECT_H/2 until RECT_H/2) {
                if((2*i.toFloat()/RECT_W).pow(2f)+(2*j.toFloat()/RECT_H).pow(2f) <= 1){
                    grid.setA(GRID_WIDTH / 2 + i, GRID_HEIGHT / 2 + j, 0f)
                    grid.setB(GRID_WIDTH / 2 + i, GRID_HEIGHT / 2 + j, 1f)
                }
            }
        }
    }
}


fun main(args: Array<String>) {
    Program().run()
}
