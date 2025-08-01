class OneVariable(val expression: String) {

    private lateinit var expressions: Pair<String, String>
    private lateinit var operatorUsed: String
    private var answer: String = ""

    init {
        solve2expressions(expression)
    }

    private fun parseExpression(expression: String): Quadruple<String, String, String, Char> {

        var expression = expression.replace(" ", "")

        // Ensure all groupings have matching pairs
        val stack = mutableListOf<Char>()
        for (char in expression) {
            when (char) {
                '(', '[', '{' -> stack.add(char)
                ')' -> if (stack.isEmpty() || stack.removeAt(stack.lastIndex) != '(') throw IllegalArgumentException("Unmatched parentheses in the expression")
                ']' -> if (stack.isEmpty() || stack.removeAt(stack.lastIndex) != '[') throw IllegalArgumentException("Unmatched brackets in the expression")
                '}' -> if (stack.isEmpty() || stack.removeAt(stack.lastIndex) != '{') throw IllegalArgumentException("Unmatched braces in the expression")
            }

        if (stack.isNotEmpty()) throw IllegalArgumentException("Unmatched groupings in the expression")

        // Extract variable from the expression
        val variable = expression.find { it.isLetter() } ?: throw IllegalArgumentException("No variable found in the expression")

        // Ensure the variable is a single character
        if (expression.count { it == variable } != 1) {
            throw IllegalArgumentException("Variable must be a single character")
        }

        // Regex to find the operator in the expression
        val operatorRegex = Regex("(<=|>=|=|<|>)")
        
        val match = operatorRegex.find(expression) ?: throw IllegalArgumentException("No valid operator found")
        val operatorUsed = match.value

        val split = expression.split(operatorRegex)

        if (split.size != 2) throw IllegalArgumentException("Invalid equation format")

        val left = split[0].trim()
        val right = split[1].trim()

        if (left.isEmpty() || right.isEmpty()) {
            throw IllegalArgumentException("Both sides of the equation must be non-empty")
        }

        return Quadruple(left, operatorUsed, right, variable) // return as requested
    }

    fun solve2expressions(expression: String = this.expression): String {

        val parsed = parseExpression(expression)

        val leftExpr = simplifyExpression(parsed.first)

        val rightExpr = simplifyExpression(parsed.third)

        answer = "$leftExpr ${parsed.second} $rightExpr"
        return answer
    }

    private fun simplifyExpression(expression: String): String {

        if (!expression.contains(Regex("[\\[\\{\\(]")))
        return expression

        else{
            //remove parenthesis layer by layer
        return simplifyExpression(expression)
        }
        
    }



    fun rational(): String {
        // Placeholder for handling rational expressions (fractions)
        return "Rational solving not yet implemented."
    }

}
