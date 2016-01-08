package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

/**
 * Date: 10.01.14 at 19:46
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Consecution extends BinaryExpression {

    public Consecution(Expression left, Expression right) {
        super(left, right);
        if (toString().equals("(!B->((A->B)->((A->B)->(!B->(A->B)))))")) {
            int x = 239;
        }
    }

    @Override
    String getOperator() {
        return "->";
    }

}
