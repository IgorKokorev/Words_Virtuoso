fun solution(first: Set<Int>, second: Set<Int>): Set<Int> {
     val n = second.size
    val result = mutableSetOf<Int>()
    for (elem in first) {
        if (elem%n == 0) result.add(elem)
    }
    return result
}