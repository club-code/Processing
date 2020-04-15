package metaball

import processing.core.PApplet
import processing.core.PVector
import kotlin.math.pow

const val METABALL_NUMBER = 10
const val METABALL_SPEED = 2f
const val METABALL_COLOR_TRESHOLD = 150
const val METABALL_RADIUS_MAX = 100f
const val METABALL_RADIUS = 25f

class Program : PApplet() {

    lateinit var metaballs: Collection<Metaball>

    inner class Metaball(
        val position: PVector,
        val velocity: PVector = PVector.random2D().mult(METABALL_SPEED)
    ) {
        fun update() {
            position.add(velocity)
            if (position.x < 0) {
                position.x = 0f
                velocity.x *= -1
            }
            if (position.x > width) {
                position.x = width.toFloat()
                velocity.x *= -1
            }
            if (position.y < 0) {
                position.y = 0f
                velocity.y *= -1
            }
            if (position.y > height) {
                position.y = height.toFloat()
                velocity.y *= -1
            }
        }

    }

    fun compute(distance: Float): Float {
        return 1/distance.pow(2)
    }

    override fun settings() {
        size(750, 400)
    }

    override fun setup() {
        frameRate(60f)
        mouseClicked()
    }

    override fun draw() {
        background(0)
        metaballs.forEach { it.update() }
        loadPixels()
        for (x in 0 until width) {
            for (y in 0 until height) {
                val fieldValue = metaballs.map {
                    compute(dist(x.toFloat(), y.toFloat(), it.position.x, it.position.y))
                }.reduce { acc, it -> acc + it }
                pixels[x + y * width] = color(
                    when {
                        fieldValue > compute(METABALL_RADIUS) -> 255
                        fieldValue < compute(METABALL_RADIUS_MAX) -> 0
                        else -> {
                            val value = map(
                                fieldValue,
                                compute(METABALL_RADIUS), compute(METABALL_RADIUS_MAX),
                                255f, 0f
                            ).toInt()
                            if(value >= METABALL_COLOR_TRESHOLD)
                                255
                            else
                                0
                        }
                    }
                )
            }
        }
        updatePixels()
    }

    override fun mouseClicked() {
        metaballs = (0 until METABALL_NUMBER).map { Metaball(PVector(random(width.toFloat()), random(height.toFloat()))) }
    }

    fun run() {
        runSketch()
    }
}

fun main(args: Array<String>) {
    Program().run()
}
