package particles

import processing.core.PApplet
import processing.event.MouseEvent
import kotlin.random.Random

const val NB_PARTICLE_WIDTH = 30
const val NB_PARTICLE_HEIGHT = 16

enum class Type {
    NOTHING,
    WALL,
    SAND,
    WATER;

    fun isMoveable() = this == SAND || this == WATER

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
                particles[i][j] = Particle(type, evenLastUpdate)
            }
        }
    }

    override fun settings() {
        size(1500, 800)
    }

    override fun setup() {
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
                    } else if (i - 1 >= 0 && particles[i - 1][j].type == Type.WATER) {
                        particle.swap(particles[i - 1][j], evenFrame)
                    } else {
                        particle.evenLastUpdate = evenFrame
                    }
                } else if (particle.evenLastUpdate != evenFrame && particle.type == Type.WATER) {
                    if (i - 1 >= 0 && particles[i - 1][j].type == Type.NOTHING) {
                        particle.swap(particles[i - 1][j], evenFrame)
                    } else if (i - 1 >= 0 && particles[i - 1][j].type == Type.WATER) {
                        val left = j - 1 >= 0 && particles[i][j - 1].type == Type.NOTHING &&
                                particles[i][j - 1].evenLastUpdate != evenFrame
                        val right = j + 1 < particles[i].size && particles[i][j + 1].type == Type.NOTHING &&
                                particles[i][j + 1].evenLastUpdate != evenFrame
                        if (left && right) {
                            val isLeft = Random.nextBoolean()
                            if (isLeft)
                                particle.swap(particles[i][j - 1], evenFrame)
                            else
                                particle.swap(particles[i][j + 1], evenFrame)
                        } else if (left) {
                            particle.swap(particles[i][j - 1], evenFrame)
                        } else if (right) {
                            particle.swap(particles[i][j + 1], evenFrame)
                        } else {
                            particle.evenLastUpdate = evenFrame
                        }
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
                        fill(100f);rect(j * 50f, height - (i + 1) * 50f, 50f, 50f)
                    }
                    Type.SAND -> {
                        fill(178f, 110f, 51f);rect(j * 50f, height - (i + 1) * 50f, 50f, 50f)
                    }
                    Type.WATER -> {
                        fill(51f, 119f, 178f);rect(j * 50f, height - (i + 1) * 50f, 50f, 50f)
                    }
                    Type.NOTHING -> {
                    }
                }
            }
        }
    }

    override fun mouseClicked(event: MouseEvent?) {
        if (event != null) {
            val i = (height - event.y) / 50
            val j = (event.x) / 50
            val evenFrame = frameCount % 2 == 0
            if (event.button == LEFT) {
                if (i in particles.indices && j in particles[i].indices && particles[i][j].type == Type.NOTHING) {
                    particles[i][j].type = Type.SAND
                    particles[i][j].evenLastUpdate = evenFrame
                }
            } else if (event.button == RIGHT) {
                if (i in particles.indices && j in particles[i].indices && particles[i][j].type == Type.NOTHING) {
                    particles[i][j].type = Type.WATER
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