package mondrian

import processing.core.PApplet
import processing.core.PVector
import kotlin.math.pow
import kotlin.random.Random

const val MIN_LENGTH = 100f
const val RANDOM_DIRECTION = 0.33
const val RANDOM_CONTINUE = 0.75

class Program : PApplet() {

    lateinit var colors: List<PVector>

    inner class Rectangle(val x: Float, val y: Float, val width: Float, val height: Float) {
        fun mondrian() {
            val size = map(area(), MIN_LENGTH*MIN_LENGTH,
                this@Program.width.toFloat()*this@Program.height.toFloat(), 0f, 1f)
            val direction = Random.nextFloat()
            if (width >= 2 * MIN_LENGTH && height >= 2 * MIN_LENGTH) {
                if(Random.nextFloat() < size.pow(0.2f)) {
                    if(direction < 1f / 2) {
                        val position = Random.nextFloat() * (width - 2 * MIN_LENGTH) + MIN_LENGTH
                        val left = Rectangle(x, y, position, height)
                        val right = Rectangle(x + position, y, width - position, height)
                        left.mondrian()
                        right.mondrian()
                    } else  {
                        val position = Random.nextFloat() * (height - 2 * MIN_LENGTH) + MIN_LENGTH
                        val down = Rectangle(x, y, width, position)
                        val up = Rectangle(x, y + position, width, height - position)
                        down.mondrian()
                        up.mondrian()
                    }
                } else {
                    draw()
                }

            } else if (width >= 2 * MIN_LENGTH) {
                if (direction < RANDOM_CONTINUE || width > height * 4) {
                    val position = Random.nextFloat() * (width - 2 * MIN_LENGTH) + MIN_LENGTH
                    val left = Rectangle(x, y, position, height)
                    val right = Rectangle(x + position, y, width- position, height)
                    left.mondrian()
                    right.mondrian()
                } else {
                    draw()
                }
            } else if (height >= 2 * MIN_LENGTH) {
                if (direction < RANDOM_CONTINUE || height > width * 4) {
                    val position = Random.nextFloat() * (height - 2 * MIN_LENGTH) + MIN_LENGTH
                    val down = Rectangle(x, y, width, position)
                    val up = Rectangle(x, y + position, width, height - position)
                    down.mondrian()
                    up.mondrian()
                } else {
                    draw()
                }
            } else {
                draw()
            }
        }

        fun draw() {
            val c = colors.random()
            fill(c.x, c.y, c.z)
            rect(x, y, width, height)
        }

        fun area() = width*height
    }

    override fun settings() {
        size(1500, 800)
    }

    override fun setup() {
        colors = listOf(
            PVector(255f, 0f, 0f), PVector(255f, 255f, 0f),
            PVector(0f, 0f, 255f),
            PVector(255f, 255f, 255f), PVector(255f, 255f, 255f), PVector(255f, 255f, 255f),
            PVector(0f, 0f, 0f)
        )
        mouseClicked()
    }

    override fun draw() {

    }

    override fun mouseClicked() {
        Rectangle(0f, 0f, width.toFloat(), height.toFloat()).mondrian()
    }

    fun run() {
        runSketch()
    }
}

fun main(args: Array<String>) {
    Program().run()
}