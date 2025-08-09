fun simplifyExpression(expression: String): String {
    // If there are no brackets or absolute values, just return as-is
    if (!expression.contains(Regex("[\\[\\{\\(|\\|]")))
        return expression

    // Pattern to match simplest inner groups: (), [], {}, or || for absolute values
    // Captures optional coefficient before the group, the group itself, optional exponent
    val pattern = Regex("""([a-zA-Z0-9]*)?([\(\[\{\|])([^()\[\]\{\}\|]*)[\)\]\}\|](\^([a-zA-Z0-9]+))?""")

    var newExpression = expression
    val simpleGroups = pattern.findAll(expression).toList()

    for (match in simpleGroups) {
        val coefficient = match.groups[1]?.value ?: ""
        val opening = match.groups[2]?.value ?: ""
        val innerExpr = match.groups[3]?.value ?: ""
        val exponent = match.groups[5]?.value ?: ""

        var computedInner = innerExpr

        // If this group is an absolute value, compute the numeric value and make it positive
        if (opening == "|") {
            computedInner = absoluteValue(innerExpr)
        }

        // Try to compute numeric result of the inner expression if it’s purely arithmetic
        if (innerExpr.matches(Regex("^[0-9+\\-*/.]+$"))) {
            computedInner = computeArithmetic(innerExpr).first().toString()
        }

        // If exponent is present, expand or compute
        if (exponent.isNotEmpty()) {
            computedInner = expandExponent(computedInner, exponent.toInt())
        }

        // Reapply coefficient if there was one
        val replacement = if (coefficient.isNotEmpty()) {
            "${coefficient}*${computedInner}"
        } else {
            computedInner
        }

        // Replace the matched group with the computed value
        newExpression = newExpression.replace(match.value, replacement)
    }

    return newExpression
}

fun computeArithmetic(expression: String): ArrayList<Double> {
    val terms = ArrayList<Double>()

    // Very basic arithmetic evaluator (no operator precedence, processes left to right)
    // Example: "2+3-4" -> 1.0
    val tokens = Regex("(?<=[-+*/])|(?=[-+*/])").split(expression).map { it.trim() }.filter { it.isNotEmpty() }

    var currentValue = tokens[0].toDouble()
    var i = 1
    while (i < tokens.size) {
        val op = tokens[i]
        val num = tokens[i + 1].toDouble()
        when (op) {
            "+" -> currentValue += num
            "-" -> currentValue -= num
            "*" -> currentValue *= num
            "/" -> currentValue /= num
        }
        i += 2
    }

    terms.add(currentValue)
    return terms
}

fun transposeTerms(simplifiedLeft: String, simplifiedRight: String): Pair<String, String> {
    // This will be basic: move constants to right, variables to left
    // Assumes both sides are simplified strings like "x+5" and "3"
    var left = simplifiedLeft
    var right = simplifiedRight

    // Move any constants from left to right
    val constantPattern = Regex("""[+-]?\d+(\.\d+)?""")
    val constantsOnLeft = constantPattern.findAll(left).map { it.value }.toList()

    for (const in constantsOnLeft) {
        left = left.replaceFirst(const, "").trim()
        val negated = if (const.startsWith("-")) const.drop(1) else "-$const"
        right += if (right.isEmpty()) negated else "+$negated"
    }

    return left to right
}

fun expandExponent(base: String, exponent: Int): String {
    // Expand by multiplication: (x)^3 => x*x*x
    return if (exponent <= 1) base else List(exponent) { base }.joinToString("*")
}

fun multiply(multiplicands: ArrayList<Double>): String {
    var result = 1.0
    for (num in multiplicands) result *= num
    return result.toString()
}

fun divide(numerator: Double, denominator: Double): String {
    if (denominator == 0.0) throw ArithmeticException("Division by zero")
    return (numerator / denominator).toString()
}

fun absoluteValue(innerExpr: String): String {
    // Compute and return positive version if numeric
    return if (innerExpr.matches(Regex("^[0-9+\\-*/.]+$"))) {
        val valNum = computeArithmetic(innerExpr).first()
        kotlin.math.abs(valNum).toString()
    } else {
        // If variable, wrap in abs() for later
        "abs($innerExpr)"
    }
}

fun rationalExpression(expr: String): String {
    // Placeholder for rational expression handling
    // e.g., (x+1)/(x-1)
    return "Rational solving not yet implemented."
}
