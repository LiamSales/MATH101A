class OneVariable(val expression: String) {

    private lateinit var expressions: Pair<String, String>
    private lateinit var operatorUsed: String
    private var answer: String = ""

    init {
        solve2expressions(expression)
    }

    data class ParsedExpression(
        val left: String,
        val operator: String,
        val right: String,
        val variable: Char
    )

    private fun parseExpression(expression: String): ParsedExpression {

        // Remove whitespace
        var expression = expression.replace(" ", "")

        // Validation
        if (!expression.matches(Regex("^[a-zA-Z0-9^+\\-*/()\\[\\]{}<>=|]+$"))) {
            throw IllegalArgumentException(
                "Expression contains invalid characters. Only a-z, A-Z, 0-9, ^, +, -, *, /, (, ), [, ], {, }, |, <, >, = are allowed."
            )
        }

        val stack = mutableListOf<Char>()
        
        for (char in expression) {
            when (char) {
                '(', '[', '{', '|' -> stack.add(char)
                ')' -> if (stack.isEmpty() || stack.removeAt(stack.lastIndex) != '(')
                    throw IllegalArgumentException("Unmatched parentheses in the expression")
                ']' -> if (stack.isEmpty() || stack.removeAt(stack.lastIndex) != '[')
                    throw IllegalArgumentException("Unmatched brackets in the expression")
                '}' -> if (stack.isEmpty() || stack.removeAt(stack.lastIndex) != '{')
                    throw IllegalArgumentException("Unmatched braces in the expression")
                '|' -> if (stack.isEmpty() || stack.removeAt(stack.lastIndex) != '|')
                    throw IllegalArgumentException("Unmatched vertical bar in the expression")
            }
        }
        if (stack.isNotEmpty()) throw IllegalArgumentException("Unmatched groupings in the expression")

        val variable = expression.find { it.isLetter() }
            ?: throw IllegalArgumentException("No variable found in the expression")
        if (expression.count { it == variable } != 1) {
            throw IllegalArgumentException("Variable must be a single character")
        }

        val operatorRegex = Regex("(<=|>=|=|<|>)")
        val match = operatorRegex.find(expression)
            ?: throw IllegalArgumentException("No valid operator found")
        val operatorUsed = match.value

        val split = expression.split(operatorRegex)
        if (split.size != 2) throw IllegalArgumentException("Invalid equation format")

        val left = split[0].trim()
        val right = split[1].trim()
        if (left.isEmpty() || right.isEmpty()) {
            throw IllegalArgumentException("Both sides of the equation must be non-empty")
        }

        return ParsedExpression(left, operatorUsed, right, variable)
    }

    fun solve2expressions(expression: String = this.expression): String {
        val parsed = parseExpression(expression)
        val leftExpr = simplifyExpression(parsed.left)
        val rightExpr = simplifyExpression(parsed.right)

        // Pass both left and right to transposeTerms, returns left full of variables, right full of constants
        answer = "$leftExpr ${parsed.operator} $rightExpr"

        return answer
    }

    fun simplifyExpression(expression: String): String {

        // If there are no brackets or absolute values, just return as-is
        if (!expression.contains(Regex("[\\[\\{\\(|\\|]")))
            return expression

        // Pattern to match simplest inner groups: (), [], {}, or || for absolute values
        val pattern = Regex("""([a-zA-Z0-9]*)?([\(\[\{\|])([^()\[\]\{\}\|]*)[\)\]\}\|](\^([a-zA-Z0-9]+))?""")

        var newExpression = expression
        val simpleGroups = pattern.findAll(expression).toList()

        for (match in simpleGroups) {
            val coefficient = match.groups[1]?.value ?: ""
            val opening = match.groups[2]?.value ?: ""
            val innerExpr = match.groups[3]?.value ?: ""
            val exponent = match.groups[5]?.value ?: ""

            var computedInner = innerExpr

            // If this group is an absolute value, compute numeric value and make it positive
            if (opening == "|") {
                computedInner = absoluteValue(innerExpr)
            }

            // Try to compute numeric result if purely arithmetic
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

            // Replace the matched group with computed value
            newExpression = newExpression.replace(match.value, replacement)
        }

        return newExpression
    }

fun computeArithmetic(expression: String): Double {
    // Step 1: Tokenize
    val tokens = tokenize(expression)

    // Step 2: Resolve unary minus (e.g., -5, -x)
    val unaryFixed = handleUnary(tokens)

    // Step 3: Evaluate in precedence order
    val afterExponents = applyOperator(unaryFixed, setOf("^"), rightToLeft = true)
    val afterMulDiv = applyOperator(afterExponents, setOf("*", "/"), rightToLeft = false)
    val finalResult = applyOperator(afterMulDiv, setOf("+", "-"), rightToLeft = false)

    // At this point, only one token should remain
    return finalResult.first().toString().toDouble()
}

// --- Helper: Tokenizer ---
private fun tokenize(expr: String): MutableList<String> {
    val tokens = mutableListOf<String>()
    var numberBuffer = StringBuilder()

    for (char in expr) {
        when {
            char.isDigit() || char == '.' -> {
                numberBuffer.append(char)
            }
            char.isLetter() -> {
                // variable like x — push number buffer first if present
                if (numberBuffer.isNotEmpty()) {
                    tokens.add(numberBuffer.toString())
                    numberBuffer.clear()
                }
                tokens.add(char.toString())
            }
            char in setOf('+', '-', '*', '/', '^') -> {
                if (numberBuffer.isNotEmpty()) {
                    tokens.add(numberBuffer.toString())
                    numberBuffer.clear()
                }
                tokens.add(char.toString())
            }
            else -> throw IllegalArgumentException("Unexpected char '$char'")
        }
    }
    if (numberBuffer.isNotEmpty()) {
        tokens.add(numberBuffer.toString())
    }
    return tokens
}

// --- Helper: Handle unary minus ---
private fun handleUnary(tokens: MutableList<String>): MutableList<String> {
    val result = mutableListOf<String>()
    var i = 0
    while (i < tokens.size) {
        if (tokens[i] == "-" && (i == 0 || tokens[i - 1] in setOf("+", "-", "*", "/", "^"))) {
            // Merge with the next number
            val merged = "-" + tokens[i + 1]
            result.add(merged)
            i += 2
        } else {
            result.add(tokens[i])
            i++
        }
    }
    return result
}

// --- Helper: Apply operators of given precedence ---
private fun applyOperator(tokens: MutableList<String>, ops: Set<String>, rightToLeft: Boolean): MutableList<String> {
    if (rightToLeft) {
        var i = tokens.size - 1
        while (i >= 0) {
            if (tokens[i] in ops) {
                val left = tokens[i - 1].toDouble()
                val right = tokens[i + 1].toDouble()
                val res = when (tokens[i]) {
                    "^" -> Math.pow(left, right)
                    else -> throw IllegalStateException("Unsupported op")
                }
                tokens[i - 1] = res.toString()
                tokens.removeAt(i) // operator
                tokens.removeAt(i) // right operand
            }
            i--
        }
    } else {
        var i = 0
        while (i < tokens.size) {
            if (tokens[i] in ops) {
                val left = tokens[i - 1].toDouble()
                val right = tokens[i + 1].toDouble()
                val res = when (tokens[i]) {
                    "*" -> left * right
                    "/" -> left / right
                    "+" -> left + right
                    "-" -> left - right
                    else -> throw IllegalStateException("Unsupported op")
                }
                tokens[i - 1] = res.toString()
                tokens.removeAt(i)
                tokens.removeAt(i)
                i-- // step back after removal
            }
            i++
        }
    }
    return tokens
}


    fun transposeTerms(simplifiedLeft: String, simplifiedRight: String): Pair<String, String> {
        // Move constants to right, variables to left
        var left = simplifiedLeft
        var right = simplifiedRight

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

    fun absoluteValue(innerExpr: String): String {
        return if (innerExpr.matches(Regex("^[0-9+\\-*/.]+$"))) {
            val valNum = computeArithmetic(innerExpr).first()
            kotlin.math.abs(valNum).toString()
        } else {
            "abs($innerExpr)"
        }
    }

    fun rationalExpression(left: String, right: String): String {

        //parse the expression to see whats after the "/" but before the next term
        
        //multiply both sides of the expression by the LCD, especially if it has a variable

        //return the simplified version


        return "Rational solving not yet implemented."
    }
}
