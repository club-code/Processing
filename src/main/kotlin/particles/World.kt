package particles

import processing.core.PApplet
import processing.event.KeyEvent
import processing.event.MouseEvent

const val WIDTH = 50f

class Program : PApplet() {

    private lateinit var systems: Array<System>
    private lateinit var grid: Grid
    private var lastKey = '&'

    inner class Sand : GranularCell() {
        override fun show(x: Float, y: Float) {
            fill(178f, 110f, 51f)
            rect(x, y, WIDTH, WIDTH)
        }

    }

    inner class Iron : SolidCell(){
        override fun show(x: Float, y: Float) {
            fill(100f, 100f, 100f)
            rect(x, y, WIDTH, WIDTH)
        }

    }

    inner class HeavyIron : GravityCell() {
        override fun show(x: Float, y: Float) {
            fill(150f, 150f, 150f)
            rect(x, y, WIDTH, WIDTH)
        }
    }

    inner class Water : LiquidCell() {
        override fun blankCopy(): LiquidCell {
            return Water()
        }

        override fun show(x: Float, y: Float) {
            fill(0f, 0f, 200f)
            rect(x, y + WIDTH, WIDTH, -size * 50f)
        }

    }

    override fun settings() {
        size(1500, 800)
    }

    override fun setup() {
        grid = Grid(30, 16)
        systems = arrayOf(
            GravitySystem(),
            GranularSystem(),
            LiquidSystem()
        )
        noStroke()
    }

    override fun draw() {
        background(0)
        for (system in systems) {
            system.setup(grid)
        }

        for (i in grid.heightIndices) {
            for (j in grid.widthIndices) {
                for (system in systems) {
                    system.update(grid, i, j, frameCount)
                }
            }
        }

        for (system in systems) {
            system.close(grid)
        }

        for (i in grid.heightIndices) {
            for (j in grid.widthIndices) {
                grid[i, j]?.show(j * WIDTH, height - (i + 1) * WIDTH)
            }
        }
    }

    override fun keyPressed(event: KeyEvent?) {
        if (event != null) {
            lastKey = event.key
        }
    }

    override fun mousePressed(event: MouseEvent?) {
        if (event != null) {
            val i = (height - event.y) / WIDTH.toInt()
            val j = (event.x) / WIDTH.toInt()
            var cell: Cell = EmptyCell()
            when (lastKey) {
                '&' -> {
                    cell = Sand()
                    cell.evenFrame = frameCount % 2 == 0
                }
                'é' -> {
                    cell = HeavyIron()
                }
                '"' -> {
                    val new = Water()
                    new.liquid = 1f
                    cell = new
                }
                '\'' -> {
                    cell = Iron()
                }
            }

            grid[i, j] = cell
        }
    }

    override fun mouseDragged(event: MouseEvent?) {
        mousePressed(event)
    }

    fun run() {
        runSketch()
    }
}

fun main(args: Array<String>) {
    Program().run()
}
