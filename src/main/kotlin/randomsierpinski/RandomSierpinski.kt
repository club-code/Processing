package randomsierpinski

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*



import processing.core.PApplet
import processing.core.PVector
import java.util.concurrent.ArrayBlockingQueue

const val count = 10
const val capacity = 100

class Program : PApplet() {
    val points = ArrayBlockingQueue<PVector>(capacity)
    var job: Job? = null

    lateinit var start: PVector

    lateinit var triangle: Triangle

    class Triangle(val point1:  PVector, val point2: PVector, val point3: PVector)

    fun generatePoints(start: PVector, count: Int): PVector{
        var current = start
        repeat(count){
            current = when(random(1f)){
                in 0f..(1f/3) -> PVector.mult(PVector.add(current, triangle.point1), 0.5f)
                in (1f/3)..(2f/3) -> PVector.mult(PVector.add(current, triangle.point2), 0.5f)
                else -> PVector.mult(PVector.add(current, triangle.point3), 0.5f)
            }
            points.put(current)
        }
        return current
    }

    override fun settings(){
        size(1500, 800)

    }

    override fun setup(){
        mouseClicked()
    }

    override fun mouseClicked() {
        job?.cancel()
        points.clear()
        triangle = Triangle (
            PVector(random(1500f),random( 800f)),
            PVector(random(1500f),random( 800f)),
            PVector(random(1500f),random( 800f))
        )
        val alpha = random(1f)
        val beta = random(1f)
        val point1 = PVector.add(PVector.mult(triangle.point1, alpha),PVector.mult(triangle.point2, 1-alpha))
        start = PVector.add(PVector.mult(point1, beta), PVector.mult(triangle.point3, 1-beta))



        job = GlobalScope.launch {
            while(true){
                start = generatePoints(start, count)
            }
        }
        background(255f)
    }

    override fun draw() {
        repeat(capacity) {
            val point = points.take()
            ellipse(point.x, point.y, 1f, 1f)
        }
    }


    fun run(){
        runSketch()
    }
}

fun main(){
    Program().run()
}

