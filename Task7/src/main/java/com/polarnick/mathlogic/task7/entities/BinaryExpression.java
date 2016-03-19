package com.polarnick.mathlogic.task7.entities;

/**
 * Date: 10.01.14 at 20:26
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class BinaryExpression extends Expression {

    public final Expression left;
    public final Expression right;
    public final String operator;

    public BinaryExpression(Expression left, Expression right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public BinaryExpression rename(String variableKey, Expression value) {
        return new BinaryExpression(
                this.left.rename(variableKey, value),
                this.right.rename(variableKey, value),
                this.operator);
    }

    @Override
    public String toString() {
        return "(" + left + ")" + operator + "(" + right + ")";
    }

}
