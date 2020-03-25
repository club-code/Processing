import java.lang.Thread.sleep


fun main(args: Array<String>){
    var found = false
    while(!found) {
        println("CherryTree: 1")
        println("CirclePacking: 2")
        println("Square: 3")
        println("RandomSierpinski: 4")
        println("TextGenerator: 5")
        val a = readLine()
        if (a != null) {
            found = true
            when (a.toInt()) {
                1 -> cherrytree.main()
                2 -> circlepacking.main(args)
                3 -> pi.main(args)
                4 -> randomsierpinski.main()
                5 -> textgenerator.main()
                else -> found = false
            }
        }
    }
}
