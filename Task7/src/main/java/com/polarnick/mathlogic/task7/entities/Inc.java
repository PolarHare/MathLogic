package com.polarnick.mathlogic.task7.entities;

/**
 * Date: 09.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class Inc extends Expression {

    public final Expression expression;

    public Inc(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        boolean withBrackets = true;
        String res = expression.toString();
        if (expression instanceof Inc | expression instanceof Variable) {
            withBrackets = false;
        }
        if (withBrackets) {
            res = "(" + res + ")";
        }
        return res + "'";
    }
}
