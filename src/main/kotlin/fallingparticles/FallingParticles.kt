package fallingparticles

import processing.core.PApplet
import processing.event.MouseEvent

const val NB_PARTICLE_WIDTH = 75
const val NB_PARTICLE_HEIGHT = 40
const val WIDTH = 20f
const val FILL_SIZE = 0

enum class Type {
    NOTHING,
    WALL,
    SAND;

    fun isMoveable() = this == SAND

}

data class Particle(var type: Type, var evenLastUpdate: Boolean = false) {
    fun swap(other: Particle, evenLastUpdate: Boolean) {
        val type = this.type
        this.type = other.type
        this.evenLastUpdate = evenLastUpdate
        other.type = type
        other.evenLastUpdate = evenLastUpdate
    }
}

class Program : PApplet() {
    private val particles = Array(NB_PARTICLE_HEIGHT) { Array(NB_PARTICLE_WIDTH) { Particle(Type.NOTHING) } }

    private fun setType(startX: Int, startY: Int, endX: Int, endY: Int, type: Type, evenLastUpdate: Boolean = false) {
        for (i in startY..endY) {
            for (j in startX..endX) {
                if (i in 0 until NB_PARTICLE_HEIGHT && j in 0 until NB_PARTICLE_WIDTH)
                    particles[i][j] = Particle(type, evenLastUpdate)
            }
        }
    }

    override fun settings() {
        size(1500, 800)
    }

    override fun setup() {
        frameRate(2f)
        setType(0, 0, 10, 2, Type.WALL)
        setType(4, 3, 6, 3, Type.WALL)
        noStroke()
    }

    override fun draw() {
        background(0)
        val evenFrame = frameCount % 2 == 0

        for (i in particles.size - 1 downTo 0) {
            for (j in particles[i].indices) {
                val particle = particles[i][j]
                if (particle.evenLastUpdate != evenFrame && particle.type == Type.SAND) {
                    if (i - 1 >= 0 && particles[i - 1][j].type == Type.NOTHING) {
                        particle.swap(particles[i - 1][j], evenFrame)
                    } else if (evenFrame && i - 1 >= 0 && j - 1 >= 0 && particles[i - 1][j - 1].type == Type.NOTHING) {
                        particle.swap(particles[i - 1][j - 1], evenFrame)
                    } else if (!evenFrame && i - 1 >= 0 && j + 1 < particles[i].size && particles[i - 1][j + 1].type == Type.NOTHING) {
                        particle.swap(particles[i - 1][j + 1], evenFrame)
                    } else {
                        particle.evenLastUpdate = evenFrame
                    }
                } else if (particle.type.isMoveable()) {
                    particle.evenLastUpdate = evenFrame
                }

            }

        }
        for ((i, particleLine) in particles.withIndex()) {
            for ((j, particle) in particleLine.withIndex()) {
                when (particle.type) {
                    Type.WALL -> {
                        fill(100f);rect(j * WIDTH, height - (i + 1) * WIDTH, WIDTH, WIDTH)
                    }
                    Type.SAND -> {
                        fill(178f, 110f, 51f);rect(j * WIDTH, height - (i + 1) * WIDTH, WIDTH, WIDTH)
                    }
                    Type.NOTHING -> {
                    }
                }
            }
        }
    }

    override fun mouseClicked(event: MouseEvent?) {
        if (event != null) {
            val i = (height - event.y) / WIDTH.toInt()
            val j = (event.x) / WIDTH.toInt()
            val evenFrame = frameCount % 2 == 0
            if (event.button == LEFT) {
                setType(j - FILL_SIZE, i - FILL_SIZE, j + FILL_SIZE, i + FILL_SIZE, Type.SAND, evenFrame)
//                if (i in particles.indices && j in particles[i].indices && particles[i][j].type == Type.NOTHING) {
//                    particles[i][j].type = Type.SAND
//                    particles[i][j].evenLastUpdate = evenFrame
//                }
            } else if (event.button == RIGHT) {
                if (i in particles.indices && j in particles[i].indices && particles[i][j].type == Type.SAND) {
                    particles[i][j].type = Type.NOTHING
                    particles[i][j].evenLastUpdate = evenFrame
                }
            }
        }
    }

    override fun mouseDragged(event: MouseEvent?) {
        mouseClicked(event)
    }

    fun run() {
        runSketch()
    }
}

fun main(args: Array<String>) {
    Program().run()
}