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

    @Override
    public String toString() {
        return "(" + left + ")" + operator + "(" + right + ")";
    }

}
