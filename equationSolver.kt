/*🔸 2. Module: EquationSolver.kt
Solve ax + b = 0, ax^2 + bx + c = 0

Solve inequalities like x^2 - 4 > 0

Show solution steps */


class EquationsInOneVar(){ val equation: String = readlnorNull("Give the equation to asses whether inconsistent, conditional or identity, use arithmetic operators: +, -, /, and = : ") ?: ""
    
    // parse the left and right side of the equation
    private val leftSide: String = equation.substringBefore('=').trim()
    private val rightSide: String = equation.substringAfter('=').trim()

    fun parseExpression(expression: String): Pair<arrayListOf<String>(), Double> {

        // distribute parethesis, including exponents
        //regex
        // have everything to its simplest form, everything
        // parse by terms

        // create an arraylist and store the terms with the variables there including the signs and exponents
        

        //return the arraylist of string and the constants stored in the double


    }

    fun solveEquation (variables: arrayListOf<String>(), constants: Double){
        // now that everything is parsed into terms, solve (per exponent)
        // we can use a loop then store in array list again the final answer

        // if answer is zero then delete from arraylist
        // if arraylist is empty, identity

        //
    }


    
}

/*You're on the **right track**, Liam — this is a solid first attempt at structuring an algebra solver module. You're doing the following right:

---

## ✅ What's Good in Your Code

* **User input is immediately handled** — grabbing the equation once as a `String`
* You're **splitting the equation** into `leftSide` and `rightSide` using `substringBefore`/`After`, which is a clean way to isolate both expressions
* You've planned for:

  * **Expression parsing** using regex
  * **Storing terms** with signs, variables, and exponents
  * **Classifying** solutions (identity, conditional, inconsistent)
* You’re thinking in **terms of math logic**, not just code. That’s rare and valuable.

---

## 🔧 What You Can Do Going Forward

Here’s a practical breakdown of how you can progress — step by step:

---

### 🔸 1. Normalize the Expression

You want both sides simplified to this form:

```
ax^2 + bx + c = 0
```

**Goal:** Combine like terms and bring everything to one side.

👉 You'll need:

* A **symbolic parser** that turns `"3x^2 - 2x + 5"` into structured data.

---

### 🔸 2. Use Useful Data Structures

Here’s what will help the most:

#### ✅ `data class Term`

Create something like:

```kotlin
data class Term(val coefficient: Double, val variable: String, val exponent: Int)
```

To store terms like `3x^2`, `-2x`, `+5`.

---

#### ✅ `List<Term>` or `MutableList<Term>`

Once parsed, store your equation as a list of these `Term` objects.

You can use this list to:

* Sort by exponent (highest to lowest)
* Combine like terms
* Check which type of equation it is (linear, quadratic)

---

#### ✅ `Map<String, Double>` for Constants or Grouped Coefficients

You can also group terms by variable and exponent:

```kotlin
Map<String, Double> // key: "x^2", value: coefficient
```

Use this if you want to simplify combining like terms.

---

#### ✅ Regex + String Builders

To **tokenize** the equation, you’ll need:

* Regex to find terms (`±number`, optional variable, optional exponent)
* StringBuilder or similar to reconstruct simplified expressions or steps

---

### 🔸 3. Equation Type Detection

Once terms are parsed and combined, check:

* If only constants remain (e.g., `0 = 5`) → **Inconsistent**
* If all terms cancel out (`0 = 0`) → **Identity**
* If linear or quadratic remains → **Solve it**

---

### 🔸 4. Inequality Support

After you’re done with `=`, upgrade to:

* `>`, `<`, `>=`, `<=`

These can reuse most of your logic, just with added logic for solution ranges:

* e.g., `x^2 - 4 > 0` becomes `(x < -2) ∪ (x > 2)`

---

### 🔸 5. Keep Step Tracking

If you want step-by-step output:

* Store each transformation in a `List<String>` as a **solution trace** or `steps: MutableList<String>`

Example:

```text
1. Original: 3x + 6 = 9
2. Subtract 6: 3x = 3
3. Divide by 3: x = 1
```

---

## 🧠 Summary

| Thing                  | Use                                                     |
| ---------------------- | ------------------------------------------------------- |
| `data class Term(...)` | For parsing and manipulating equation terms             |
| `MutableList<Term>`    | To store and combine simplified terms                   |
| `Regex`                | To tokenize expressions like `-2x^2`, `+x`, `-7`        |
| `Map<String, Double>`  | To group like terms by variable and exponent            |
| `List<String>`         | To record solution steps                                |
| `when` or `if`         | To decide if the equation is identity/inconsistent/etc. |

---

Let me know if you want to **walk through designing the parser or combining like terms** next — it's one of the trickiest but most important parts.
 */