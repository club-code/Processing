package pi

import circlepacking.CirclePacking
import processing.core.*
import processing.core.PApplet
import java.lang.StringBuilder
import kotlin.math.pow


data class Square(var x: Double, var speed: Double, val width: Double, val mass: Double = 1.0){
    fun collide(other: Square) = !(x+width < other.x || x > other.x + other.width)

    fun bounce(other: Square): Double{
        val sumM = other.mass + mass
        var newSpeed = (mass-other.mass)/sumM*speed
        newSpeed += (2*other.mass/sumM)*other.speed
        return newSpeed
    }

    fun hitWall() = x <= 0

    fun reverse(){
        speed *= -1
    }

    fun update(){
        x += speed
    }
}

class SquarePacking : PApplet(){
    val digits = 6
    val timeSteps = 10.0.pow(digits-2).toInt()
    val squares = listOf(Square(250.0, 0.0, 10.0), Square(500.0, -5.0/timeSteps, 100.0, 100.0.pow(digits-1)))
    var count = 0

    inner class Truc(val a: String){
        fun b(){
            this@SquarePacking.rect(1f,1f,1f,1f)
        }
    }

    override fun setup(){
        fill(1f,0f,0f)
    }

    override fun draw(){
        background(200)
        for(i in 0 until timeSteps){
            if (squares[0].collide(squares[1])) {
                val v1 = squares[0].bounce(squares[1])
                val v2 = squares[1].bounce(squares[0])
                squares[0].speed = v1
                squares[1].speed = v2

                count++
                println(count)
            }
            if (squares[0].hitWall()) {
                squares[0].reverse()
                count++
                println(count)
            }

            updateSquares()

            /*if (squares[0].speed > 0 && squares[0].speed <= squares[1].speed) {
                noLoop()
            }*/
        }
        showSquares()

    }

    fun updateSquares(){
        for(square in squares){
            square.update()
        }
    }

    fun showSquares(){
        fill(0)
        for(square in squares){
            rect(square.x.toFloat(), 0f, square.width.toFloat(), square.width.toFloat())
        }
    }

    companion object {
        fun run() {
            val art = SquarePacking()
            art.setSize(1000, 1000)
            art.runSketch()
        }
    }
}

fun main(args: Array<String>) {
//    val a = arrayOf("alex", "truc", "yacine ce connard", "paul est meilleur en RA")
//    println(a.str())
    SquarePacking.run()
}

//fun <T> Array<T>.str(): String {
//    val res = StringBuilder("Array[")
//    for(i in this){
//        res.append("$i, ")
//    }
//    res.append("]")
//    return res.toString()
//}
