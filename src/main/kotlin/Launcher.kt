
fun main(args: Array<String>){
    var found = false
    while(!found) {
        println("CherryTree: 1")
        println("CirclePacking: 2")
        println("Square: 3")
        println("RandomSierpinski: 4")
        println("Rocket: 5")
        println("TextGenerator: 6")
        val a = readLine()
        if (a != null) {
            found = true
            when (a.toInt()) {
                1 -> cherrytree.main()
                2 -> circlepacking.main(args)
                3 -> pi.main(args)
                4 -> randomsierpinski.main()
                5 -> genetic.main()
                6 -> textgenerator.main()
                else -> found = false
            }
        }
    }
}
