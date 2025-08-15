class OneVariable(val expression: String) {

    // Stores the simplified left and right expressions
    private lateinit var expressions: Pair<String, String>
    // Stores the operator used in the equation (=, <, >, etc.)
    private lateinit var operatorUsed: String
    // Stores the final answer after solving
    private var answer: String = ""

    // Initialize and solve the equation upon object creation
    init {
        solve2expressions(expression)
    }

    // Data class to hold parsed equation components
    data class ParsedExpression(
        val left: String,
        val operator: String,
        val right: String,
        val variable: Char
    )

    /**
     * Parses the input equation string into left/right sides, operator, and variable.
     * Validates for allowed characters and correct grouping.
     */
    private fun parseExpression(expression: String): ParsedExpression {
        // Remove whitespace
        var expression = expression.replace(" ", "")

        // Validate allowed characters
        if (!expression.matches(Regex("^[a-zA-Z0-9^+\\-*/()\\[\\]{}<>=|]+$"))) {
            throw IllegalArgumentException(
                "Expression contains invalid characters. Only a-z, A-Z, 0-9, ^, +, -, *, /, (, ), [, ], {, }, |, <, >, = are allowed."
            )
        }

        // Validate groupings (parentheses, brackets, braces, absolute value)
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

        // Find the variable (must be a single character)
        val variable = expression.find { it.isLetter() }
            ?: throw IllegalArgumentException("No variable found in the expression")
        if (expression.count { it == variable } != 1) {
            throw IllegalArgumentException("Variable must be a single character")
        }

        // Find the operator (=, <, >, <=, >=)
        val operatorRegex = Regex("(<=|>=|=|<|>)")
        val match = operatorRegex.find(expression)
            ?: throw IllegalArgumentException("No valid operator found")
        val operatorUsed = match.value

        // Split into left and right sides
        val split = expression.split(operatorRegex)
        if (split.size != 2) throw IllegalArgumentException("Invalid equation format")

        val left = split[0].trim()
        val right = split[1].trim()
        if (left.isEmpty() || right.isEmpty()) {
            throw IllegalArgumentException("Both sides of the equation must be non-empty")
        }

        return ParsedExpression(left, operatorUsed, right, variable)
    }

    /**
     * Main entry point to solve the equation.
     * Simplifies both sides and prepares the answer.
     */
    fun solve2expressions(expression: String = this.expression): String {
        val parsed = parseExpression(expression)
        val leftExpr = simplifyExpression(parsed.left)
        val rightExpr = simplifyExpression(parsed.right)

        // If rational (contains /), handle specially
        if (leftExpr.contains("/") || rightExpr.contains("/")) {
            answer = rationalExpression(leftExpr, rightExpr)
        } else {
            // Transpose terms so variables on left, constants on right
            val transposed = transposeTerms(leftExpr, rightExpr)
            answer = "${transposed.first} ${parsed.operator} ${transposed.second}"
        }

        return answer
    }

    /**
     * Simplifies an expression by evaluating inner groups (parentheses, brackets, braces, absolute value).
     * Handles coefficients and exponents.
     */
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
                computedInner = computeArithmetic(innerExpr).toString()
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

    /**
     * Computes a purely arithmetic expression (no variables).
     * Handles operator precedence and unary minus.
     */
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
        return finalResult.first().toDouble()
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

    /**
     * Moves all constants to the right and variables to the left.
     * Returns a pair of (left, right) expressions.
     */
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

    /**
     * Expands an exponent by multiplication, e.g., (x)^3 => x*x*x
     */
    fun expandExponent(base: String, exponent: Int): String {
        return if (exponent <= 1) base else List(exponent) { base }.joinToString("*")
    }

    /**
     * Computes the absolute value of an arithmetic expression.
     * If not purely numeric, wraps in abs().
     */
    fun absoluteValue(innerExpr: String): String {
        return if (innerExpr.matches(Regex("^[0-9+\\-*/.]+$"))) {
            val valNum = computeArithmetic(innerExpr)
            kotlin.math.abs(valNum).toString()
        } else {
            "abs($innerExpr)"
        }
    }

    /**
     * Solves rational equations (with fractions).
     * Multiplies both sides by the least common denominator (LCD).
     */
    fun rationalExpression(left: String, right: String): String {
        // Find denominators in both sides
        val denomPattern = Regex("""/([a-zA-Z0-9]+)""")
        val leftDenoms = denomPattern.findAll(left).map { it.groupValues[1] }.toSet()
        val rightDenoms = denomPattern.findAll(right).map { it.groupValues[1] }.toSet()
        val allDenoms = leftDenoms + rightDenoms

        // If no denominators, just return as-is
        if (allDenoms.isEmpty()) return "$left = $right"

        // Build LCD (for now, just multiply all denominators)
        val lcd = allDenoms.joinToString("*")

        // Multiply both sides by LCD
        val leftMultiplied = multiplyByLCD(left, lcd)
        val rightMultiplied = multiplyByLCD(right, lcd)

        // Return the new equation
        return "$leftMultiplied = $rightMultiplied"
    }

    /**
     * Multiplies every term in the expression by the LCD.
     * Handles simple fractions.
     */
    private fun multiplyByLCD(expr: String, lcd: String): String {
        // Replace a/b with a*lcd/b
        val fractionPattern = Regex("""([a-zA-Z0-9]+)\s*/\s*([a-zA-Z0-9]+)""")
        return expr.replace(fractionPattern) { matchResult ->
            val numerator = matchResult.groupValues[1]
            val denominator = matchResult.groupValues[2]
            "($numerator*$lcd/$denominator)"
        }
    }
}
