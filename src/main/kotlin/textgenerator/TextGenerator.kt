package textgenerator

import java.io.File

class NGram(private val order: Int) {
    private val sequences = HashMap<String, ArrayList<String>>()
    private val beginnings = mutableSetOf<Array<String>>()

    fun apply(input: String, point: Boolean = true){
        val wordsResults = Regex("[\\wâÀàéêèçîûùÔô\\-'’]+|,|;|\\.{2,3}|M\\.|\\.|!|\\?|:").findAll(input).map { it.value }.toList()
        for(i in wordsResults.indices){
            val word = concat(wordsResults, i, order)
            val nextWords = sequences[arrayToString(word)]
            if(i == 0 || wordsResults[i-1] == ".")
                beginnings.add(word)
            if(!point || word[word.size-1] != ".") {
                if (nextWords != null) {
                    if (i + order in wordsResults.indices)
                        nextWords.add(wordsResults[i + order])
                } else {
                    if (i + order in wordsResults.indices)
                        sequences[arrayToString(word)] = arrayListOf(wordsResults[i + order])
                }
            }
        }
    }

    fun generate(min: Int): String{
        val current = beginnings.random().clone()
        var size = 1
        val result = StringBuilder()
        var finished = false
        result.append(arrayToString(current))

        while(size < min || !finished){
            val next = sequences[arrayToString(current)]?.random()
            if(next !in listOf(",", "."))
                result.append(' ')
            if(next == null) {
                println(arrayToString(current))
                //result.append('#')
                result.append(generate(min - size))
                size = min
                finished = true
            } else {
                result.append(next)
                lsh(current, next)
                size++
                if(size >= min && next == ".")
                    finished = true
            }
        }

        return result.toString()
    }

    override fun toString(): String{
        val res = StringBuilder()
        res.append(sequences.toString()+"\n")
        /*for(i in beginnings){
            for(j in i){
                res.append("$j ")
            }
            res.append("\n")
        }*/
        return res.toString()
    }
}

fun concat(list: List<String>,i: Int, n: Int, point: Boolean = true): Array<String>{

    val result = Array(n){""}
    result[0] = list[i]
    if(point && list[i] == ".")
        return result
    for(j in 1 until n){
        if(j+i !in list.indices)
            break
        result[j] = list[j+i]

        if(point && list[j+i] == "."){
            break
        }

    }
    return result
}

fun arrayToString(array: Array<String>): String{
    val res = StringBuilder()
    res.append(array[0])
    for(i in 1 until array.size){
        if(array[i] !in listOf(",", "."))
            res.append(' ')
        res.append(array[i])
    }
    return res.toString()
}

fun <T> lsh(array: Array<T>, input: T){
    for(i in 0 until array.size-1){
        array[i] = array[i+1]
    }
    array[array.size-1] = input
}

fun stringToInt(str: String, default:Int)= if(str == "") default else str.toInt()


fun main(){
    val file = File("sample.txt")
    val fileContent=
        if(file.exists())
            file.readText()
        else
            NGram::class.java.classLoader.getResource("bovary.txt")?.readText()

    print("order(3): ")
    val order = stringToInt(readLine() ?: "", 3)

    val ngram = NGram(order)
    fileContent?.split("\n")?.forEach{
        ngram.apply(it, true)
    }
    print("minimum words per blocks(30): ")
    val min = stringToInt(readLine() ?: "", 30)

    print("nb blocks(5): ")
    val nb = stringToInt(readLine() ?: "", 5)
    println("\n")
    for(i in 0 until nb)
        println(ngram.generate(min))
    //println(ngram.toString())
}
