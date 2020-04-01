package boids

import processing.core.PApplet
import processing.core.PVector

const val MASS = 1

const val FORCE_MAX = 0.2f
const val SPEED_MAX = 5f
const val ALIGN = 1.5f
const val COHESION = 1f
const val SEPARATION = 2f
const val SIZE = 50
const val RADIUS_SEPARATION = 100f
const val RADIUS_ALIGN = 150f
const val RADIUS_COHESION = 200f

class Program: PApplet() {

    private lateinit var boids: Collection<Boid>

    inner class Boid(
        private val pos: PVector = PVector(random(width.toFloat()), random(height.toFloat())),
        private val vel: PVector = PVector.random2D(),
        private val acc: PVector = PVector()
    ){
        private fun edges() {
            if (pos.x > width) {
                pos.x = 0f
            } else if (pos.x < 0) {
                pos.x = width.toFloat()
            }
            if (pos.y > height) {
                pos.y = 0f
            } else if (pos.y < 0) {
                pos.y = height.toFloat()
            }
        }

        private fun align(): PVector{
            return averageNeighbours(
                RADIUS_ALIGN,
                { steering, other, _ ->
                    steering.add(other.vel)
                },
                { steering, total ->
                    if (total > 0) {
                        steering.div(total)
                        steering.setMag(SPEED_MAX*MASS)
                        steering.sub(vel)
                        steering.limit(FORCE_MAX)
                    }
                }
            )
        }


        private fun separation(): PVector{
            return averageNeighbours(
                RADIUS_SEPARATION,
                { steering, other, d ->
                    val diff = PVector.sub(pos, other.pos)
                    diff.div(d * d)
                    steering.add(diff)
                },
                { steering, total ->
                    if (total > 0) {
                        steering.div(total)
                        steering.setMag(SPEED_MAX*MASS)
                        steering.sub(vel)
                        steering.limit(FORCE_MAX)
                    }
                }
            )
        }

        private fun cohesion(): PVector{
            return averageNeighbours(
                RADIUS_COHESION,
                { steering, other, _ ->
                    steering.add(other.pos)
                },
                { steering, total ->
                    if (total > 0) {
                        steering.div(total)
                        steering.sub(pos)
                        steering.setMag(SPEED_MAX*MASS)
                        steering.sub(vel)
                        steering.limit(FORCE_MAX)
                    }
                }
            )
        }

        fun flock(){
            val alignment = align()
            val cohesion = cohesion()
            val separation = separation()

            alignment.mult(ALIGN)
            cohesion.mult(COHESION)
            separation.mult(SEPARATION)

            acc.add(alignment)
            acc.add(cohesion)
            acc.add(separation)
        }

        private fun averageNeighbours(
            radius: Float,
            operation: (PVector, Boid, Float) -> Unit,
            steeringOp: (PVector, Float)-> Unit
        ): PVector{
            val steering = PVector()
            var total = 0f
            for (other in boids) {
                val d = distanceFlatTorus(pos.x, pos.y, other.pos.x, other.pos.y)
                if (other != this && d < radius) {
                    operation(steering, other, d)
                    total++
                }
            }
            if (total > 0) {
                steeringOp(steering, total)
            }
            return steering
        }

        fun update(){
            pos.add(vel)
            vel.add(acc)
            vel.limit(SPEED_MAX)
            acc.mult(0f)
            edges()
        }

        fun show(){
            pushMatrix()
            translate(pos.x, pos.y)
            rotate(vel.heading() - PI/2)

            beginShape()
            vertex(0f,0f)
            vertex(20f, -50f)
            vertex(0f,-30f)
            vertex(-20f, -50f)
            endShape(CLOSE)

            popMatrix()
        }

        /**
         * Warning: in the paper, he swapped x2 and y1
         * @link https://www.researchgate.net/publication/327930363_Packing_of_Circles_on_Square_Flat_Torus_as_Global_Optimization_of_Mixed_Integer_Nonlinear_problem#pf1
         */
        private fun distanceFlatTorus(x1: Float, y1: Float, x2: Float, y2: Float): Float =
            sqrt(pow(min(abs(x1-x2), width-abs(x1-x2)), 2f) + pow(min(abs(y1-y2), height-abs(y1-y2)), 2f))
    }

    override fun settings(){
        size(1500,800)
    }

    override fun setup(){
        boids = List(SIZE){
            Boid()
        }
    }

    override fun draw(){
        background(0)
        for(boid in boids){
            boid.flock()
            boid.update()
            boid.show()
        }
    }

    fun run(){
        runSketch()
    }
}

fun main(args: Array<String>){
    Program().run()
}

