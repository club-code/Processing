package genetic

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector
import kotlin.random.Random

const val DNA_SIZE = 200
const val POP_SIZE = 50
const val TAR_RADIUS = 20f
var MAX_EPSILON = 0.05f
const val MUTATION_RATE = 0.002f

class Program : PApplet() {

    lateinit var population: Population<PVector, Rocket>
    lateinit var target: PVector
    var count = 0
    lateinit var obstacles: List<Pair<PVector, PVector>>

    inner class Rocket(
        private val pos: PVector = PVector(width.toFloat() / 2, height.toFloat()),
        private val vel: PVector = PVector(),
        private val acc: PVector = PVector()
    ) : Evolutionable<Rocket, PVector> {

        var crashed = false

        override var dna: DNA<PVector> = randomDNA()

        private var min: Float = Float.MAX_VALUE
        private var last: Float = Float.MIN_VALUE
        private var countMin: Int = Int.MAX_VALUE
        private var distanceParcourue: Float = 0f

        private fun applyForce(force: PVector) {
            acc.add(force)
        }

        fun update() {
            if (!crashed) {
                this.applyForce(if (count < dna.genes.size) dna.genes[count] else PVector())

                vel.add(acc)
                pos.add(vel)
                acc.mult(0f)
                val d = dist(pos.x, pos.y, target.x, target.y)
                last = d
                distanceParcourue += vel.mag()
                if (d < min) {
                    countMin = count
                    min = d
                }
                if (min < TAR_RADIUS) {
                    min = 0f
                    crashed = true
                    MAX_EPSILON += 0.001f
                } else {
                    for (o in obstacles) {
                        if (pos.x in o.first.x..o.first.x + o.second.x && pos.y in o.first.y..o.first.y + o.second.y) {
                            crashed = true
                            break
                        }
                    }
                }
            }

        }

        fun show() {
            pushMatrix()
            translate(pos.x, pos.y)
            rotate(vel.heading() - PI / 2)

            beginShape()
            vertex(0f, 0f)
            vertex(20f, -50f)
            vertex(0f, -30f)
            vertex(-20f, -50f)
            endShape(CLOSE)

            popMatrix()
        }

        private fun randomDNA() = DNA(MutableList(DNA_SIZE) { PVector.random2D() })

        override fun computeFitness(): Float {
//            return if(min > 0){
//                val m = min(1/(min)+if(min < 400) 1/countMin else 0, 0.1f)
//                if(crashed && min > 100){
//                    m/10
//                }
//                m
//            } else {
//                0.1f
//            }
            val avg = distanceParcourue / frameCount
            return avg / (last)
        }

        override fun changeDNA(dna: DNA<PVector>): Rocket {
            return Rocket().apply { this.dna = dna }
        }

        override fun mutate(it: PVector): PVector {
            return if (Random.nextFloat() < MUTATION_RATE) {
                PVector.random2D()
            } else {
                it
            }
        }
    }

    override fun settings() {
        size(1500, 800)
    }

    override fun setup() {
        target = PVector(width.toFloat() / 2, 50f)
        obstacles = arrayListOf(
            PVector(width.toFloat() / 3, height.toFloat() / 2) to PVector(width.toFloat() / 2, 100f)
        )
        randomPopulation()
    }

    override fun draw() {
        background(0)
        population.list.forEach {
            it.update()
            it.show()
        }

        rectMode(PConstants.CORNER)
        for (o in obstacles) {
            rect(o.first.x, o.first.y, o.second.x, o.second.y)
        }

        count++
        if (count == DNA_SIZE) {
            count = 0
            population.evaluate()
        }

        ellipse(target.x, target.y, TAR_RADIUS, TAR_RADIUS)
    }

    fun run() {
        runSketch()
    }

    private fun randomPopulation() {
        population = Population(
            MutableList(POP_SIZE) {
                Rocket(
                    PVector(width.toFloat() / 2, height.toFloat())
                )
            }
        )
    }
}

fun main(args: Array<String>) {
    Program().run()
}

