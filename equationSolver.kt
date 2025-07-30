/* Equations and Inequalities in One Variable Linear Equations
Quadratic and Rational Equations
Linear, Quadratic and Rational Inequalities
Equations and Inequalities Involving Absolute Values
References
 */

class EquationsAndInequalitiesInOneVariable(val expression: String, val variable: Char) {

    private val expressionsList = arrayListOf<String>()

    init {
        parseExpression(expression)
    }

    // Parse expression like "3x+5=2x+10" into left and right sides
    private fun parseExpression(expr: String) {
        val operatorRegex = Regex("(<=|>=|=|<|>)")
        val match = operatorRegex.find(expr) ?: throw IllegalArgumentException("No valid operator found")
        val split = expr.split(match.value)
        if (split.size != 2) throw IllegalArgumentException("Invalid equation format")
        expressionsList.add(split[0])
        expressionsList.add(split[1])
    }

    // Solve linear equations of the form "ax + b = cx + d"
    fun solve2expressions(left: String = expressionsList[0], right: String = expressionsList[1]): String {
        val (leftCoeff, leftConst) = simplifyExpression(left)
        val (rightCoeff, rightConst) = simplifyExpression(right)

        val finalCoeff = leftCoeff - rightCoeff
        val finalConst = rightConst - leftConst

        return when {
            finalCoeff == 0.0 && finalConst == 0.0 -> "All real numbers"
            finalCoeff == 0.0 -> "No solution"
            else -> {
                val result = finalConst / finalCoeff
                "$variable = ${"%.2f".format(result)}"
            }
        }
    }

    // Simplify expressions: return Pair(coefficient of x, constant)
    private fun simplifyExpression(expr: String): Pair<Double, Double> {
        var expression = expr.replace(" ", "")
        if (expression.isNotEmpty() && expression[0] != '-') expression = "+$expression"

        val termRegex = Regex("([+-])((\\d*\\.?\\d*)?${variable}?\\^?[0-9]?)")
        val matches = termRegex.findAll(expression)

        var coefficient = 0.0
        var constant = 0.0

        for (match in matches) {
            val sign = match.groupValues[1]
            val term = match.groupValues[2]

            if (term.contains(variable)) {
                // Extract coefficient of x
                val coeffStr = term.replace(variable.toString(), "").ifEmpty { "1" }
                val coeff = coeffStr.toDoubleOrNull() ?: 1.0
                coefficient += if (sign == "-") -coeff else coeff
            } else {
                // Constant term
                val num = term.toDoubleOrNull() ?: continue
                constant += if (sign == "-") -num else num
            }
        }

        return Pair(coefficient, constant)
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