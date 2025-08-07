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

        //remove whitespace
        var expression = expression.replace(" ", "")

        //validation:

        if (!expression.matches(Regex("^[a-zA-Z0-9^+\\-*/()\\[\\]{}<>=]+$"))) {
            throw IllegalArgumentException("Expression contains invalid characters. Only a-z, A-Z, 0-9, ^, +, -, *, /, (, ), [, ], {, }, <, >, = are allowed.")
        }

        val stack = mutableListOf<Char>()
        for (char in expression) {
            when (char) {
                '(', '[', '{' -> stack.add(char)
                ')' -> if (stack.isEmpty() || stack.removeAt(stack.lastIndex) != '(') throw IllegalArgumentException("Unmatched parentheses in the expression")
                ']' -> if (stack.isEmpty() || stack.removeAt(stack.lastIndex) != '[') throw IllegalArgumentException("Unmatched brackets in the expression")
                '}' -> if (stack.isEmpty() || stack.removeAt(stack.lastIndex) != '{') throw IllegalArgumentException("Unmatched braces in the expression")
            }
        }
        if (stack.isNotEmpty()) throw IllegalArgumentException("Unmatched groupings in the expression")

        val variable = expression.find { it.isLetter() } ?: throw IllegalArgumentException("No variable found in the expression")
        if (expression.count { it == variable } != 1) {
            throw IllegalArgumentException("Variable must be a single character")
        }

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

        return ParsedExpression(left, operatorUsed, right, variable)
    }

    fun solve2expressions(expression: String = this.expression): String {
       
        val parsed = parseExpression(expression)
        val leftExpr = simplifyExpression(parsed.left)
        val rightExpr = simplifyExpression(parsed.right)
        
        //pass both left and right to transposeTerms, returns left full of variables, right full of constants
        answer = "$leftExpr ${parsed.operator} $rightExpr"
       
        return answer
    }

    fun simplifyExpression(expression: String): String {

        //should not have * or / either
        if (!expression.contains(Regex("[\\[\\{\\(]")))
            return expression

        //collect all simplest groups
        //val pattern = Regex("""([a-zA-Z0-9]*)?([\(\[\{])([^()\[\]\{\}]*)[\)\]\}](\^([a-zA-Z0-9]+))?""")
        val simpleGroups = pattern.findAll(expression).toList()

        for (simpleGroups in simplestGroups) {

            // Compute the simplified form of the inner expression then replace entirely

            // if ^ is found directly afterwards, see if it has a grouping directly afterwards, // if grouped check if has exponent, if none, simplify to int, truncate decimal
            }

            // TODO: Check if what's to the left or right is not a sign or null, and if so, multiply or divide then simplify

            //transpose terms

            //compute arithmetic 
        
            //return in simplified form
        }


    fun computeArithmetic(expression: String): ArrayList<Double> {

        val terms = ArrayList<Double>()
        val simplifiedTerms = ArrayList<Double>()
        val outerList = ArrayList<ArrayList<Double>> = ArrayList()

        // separate the terms by exponents, should be in a linked list of linked lists place the coefficients as values (double), including the signs until there is nothing left in the original string
        // outer layer should be x^n with x^0 being the arraylist of arraylists holding the constant
        // add everything in the same list
        // reconstruct the string and save in expression by putting the stuff in the summed arraylist and adding the variable char at the end of each until x^2, x^1 should not have ^1, just x and nothing should be in the x^0
        // that should be simplest form

        return simplifiedTerms
    }

    fun transposeTerms(simplifiedLeft: ArrayList<Double>, simplifiedRight: ArrayList<Double>): Pair<String,String>{

    }

    fun expandExponent(base: String, exponent: Int): String {

        //just duplicate the base the number of times the string is

        return "Rational solving not yet implemented."
    }

    fun multiply(multiplicands: ArrayList): String{
        
    }

    fun divide()
}
