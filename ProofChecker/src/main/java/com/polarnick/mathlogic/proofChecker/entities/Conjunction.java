package com.polarnick.mathlogic.proofChecker.entities;

/**
 * Date: 10.01.14 at 19:40
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Conjunction extends BinaryExpression {

    public Conjunction(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    String getOperator() {
        return "&";
    }

}
