package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

/**
 * Date: 10.01.14 at 19:46
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Consecution extends BinaryExpression {

    public Consecution(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    String getOperator() {
        return "->";
    }

    public Consecution substitute(Variable x, Expression expression) {
        return new Consecution(
                this.getLeft().substitute(x, expression),
                this.getRight().substitute(x, expression));
    }

}
