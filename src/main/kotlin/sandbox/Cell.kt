package sandbox

const val TOP = 0
const val RIGHT = 1
const val BOTTOM = 2
const val LEFT = 3


sealed class Cell() {
    abstract fun show(x: Float, y: Float)
}

abstract class SolidCell : Cell() {

}

class EmptyCell : SolidCell() {
    override fun show(x: Float, y: Float) {}
}

abstract class GravityCell : Cell() {
}

abstract class GranularCell : Cell() {
    var evenFrame = false
}

abstract class LiquidCell : Cell() {
    var liquid = 0.0f
    var size = 1f

    var settled = false
        set(value){
            if (!value) settleCount = 0
            field = value
        }

    var settleCount = 0

    val flowDirections = Array(4){false}

    fun resetFlowDirections() {
        //Arrays.fill(flowDirections, false);
        flowDirections[0] = false
        flowDirections[1] = false
        flowDirections[2] = false
        flowDirections[3] = false
    }

    fun addLiquid(amount: Float) {
        liquid += amount
        settled = false
    }

    abstract fun blankCopy(): LiquidCell
}
