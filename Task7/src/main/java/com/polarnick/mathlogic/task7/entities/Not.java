package com.polarnick.mathlogic.task7.entities;

/**
 * Date: 28.03.16.
 *
 * @author Nickolay Polyarniy
 */
public class Not extends Expression {

    public final Expression expression;

    public Not(Expression expression) {
        this.expression = expression;
    }

    public Not rename(String variableKey, Expression value) {
        return new Not(this.expression.rename(variableKey, value));
    }

    @Override
    public String toString() {
        boolean withBrackets = true;
        String res = expression.toString();
        if (expression instanceof Not | expression instanceof Variable) {
            withBrackets = false;
        }
        if (withBrackets) {
            res = "(" + res + ")";
        }
        return "!" + res;
    }
}

