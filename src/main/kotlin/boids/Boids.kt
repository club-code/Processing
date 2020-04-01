package boids

import processing.core.PApplet
import processing.core.PVector

const val FORCE_MAX = 0.2f
const val SPEED_MAX = 5f
const val ALIGN = 1.5f
const val COHESION = 1f
const val SEPARATION = 2f
const val SIZE = 50

class Program: PApplet() {

    private lateinit var boids: Collection<Boid>

    inner class Boid(
        private val pos: PVector = PVector(random(width.toFloat()), random(height.toFloat())),
        private val vel: PVector = PVector.random2D(),
        private val acc: PVector = PVector()
    ){
        fun edges() {
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

        fun align(boids: Collection<Boid>): PVector{
            val perceptionRadius = 110f
            val steering = PVector()
            var total = 0f
            for (other in boids) {
                val d = distanceFlatTorus(pos.x, pos.y, other.pos.x, other.pos.y)
                if (other != this && d < perceptionRadius) {
                    steering.add(other.vel)
                    total++
                }
            }
            if (total > 0) {
                steering.div(total)
                steering.setMag(SPEED_MAX)
                steering.sub(vel)
                steering.limit(FORCE_MAX)
            }
            return steering
        }

        fun separation(boids: Collection<Boid>): PVector{
            val perceptionRadius = 100f
            val steering = PVector()
            var total = 0f
            for (other in boids) {
                val d = distanceFlatTorus(pos.x, pos.y, other.pos.x, other.pos.y)
                if (other != this && d < perceptionRadius) {
                    val diff = PVector.sub(pos, other.pos)
                    diff.div(d * d)
                    steering.add(diff)
                    total++
                }
            }
            if (total > 0) {
                steering.div(total)
                steering.setMag(SPEED_MAX)
                steering.sub(vel)
                steering.limit(FORCE_MAX)
            }
            return steering
        }

        fun cohesion(boids: Collection<Boid>): PVector{
            val perceptionRadius = 200f
            val steering = PVector()
            var total = 0f
            for (other in boids) {
                val d = distanceFlatTorus(pos.x, pos.y, other.pos.x, other.pos.y)
                if (other != this && d < perceptionRadius) {
                    steering.add(other.pos)
                    total++
                }
            }
            if (total > 0) {
                steering.div(total)
                steering.sub(pos)
                steering.setMag(SPEED_MAX)
                steering.sub(vel)
                steering.limit(FORCE_MAX)
            }
            return steering
        }

        fun flock(boids: Collection<Boid>){
            val alignment = align(boids)
            val cohesion = cohesion(boids)
            val separation = separation(boids)

            alignment.mult(ALIGN)
            cohesion.mult(COHESION)
            separation.mult(SEPARATION)

            acc.add(alignment)
            acc.add(cohesion)
            acc.add(separation)
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
        fun distanceFlatTorus(x1: Float, y1: Float, x2: Float, y2: Float): Float =
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
            boid.flock(boids)
            boid.update()
            boid.show()
        }
    }

    fun run(){
        runSketch()
    }
}

fun main(){
    Program().run()
}

