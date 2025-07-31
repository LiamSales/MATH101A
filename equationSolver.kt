class EquationsAndInequalitiesInOneVariable(val expression: String, val variable: Char) {

    private lateinit var expressions: Pair<String, String>
    private lateinit var operatorUsed: String
    private var answer: String = ""

    init {
        solve2expressions(expression, variable)
    }

    // Parse expression like "3x+5=2x+10" into left and right sides
    private fun parseExpression(expr: String): Triple<String, Char, String> {
        val operatorRegex = Regex("(<=|>=|=|<|>)")

        val match = operatorRegex.find(expr) ?: throw IllegalArgumentException("No valid operator found")
        operatorUsed = match.value  // Save to class var

        val split = expr.split(operatorRegex)

        if (split.size != 2) throw IllegalArgumentException("Invalid equation format")

        val left = split[0].trim()
        val right = split[1].trim()

        expressions = Pair(left, right)

        return Triple(left, operatorUsed[0], right) // return as requested
    }

    fun solve2expressions(expression: String = this.expression, variable: Char): String {
        val parsed = parseExpression(expression)
        val leftExpr = simplifyExpression(parsed.first)
        val rightExpr = simplifyExpression(parsed.third)

        answer = "$leftExpr ${parsed.second} $rightExpr"
        return answer
    }

    private fun simplifyExpression(expr: String): String {
        // Remove spaces and group terms – basic step
        val cleaned = expr.replace(" ", "")

        // Future: parse tokens, simplify parentheses, etc.
        // For now, return as-is
        return cleaned
    }

    fun higherOrder(): String {
        // Placeholder for handling x^2, x^3 terms
        return "Higher order solving not yet implemented."
    }

    fun rational(): String {
        // Placeholder for handling rational expressions (fractions)
        return "Rational solving not yet implemented."
    }
}
