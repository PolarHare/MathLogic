package com.polarnick.mathlogic.proofChecker.entities;

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

}
