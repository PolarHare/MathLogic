package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

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

    @Override
    protected boolean evaluate(boolean left, boolean right) {
        return left && right;
    }

    final static String[] TRUE_TRUE = {
            "A->B->A&B",
            "B->A&B",
            "A&B"
    };
    final static String[] TRUE_FALSE = {
            "!B->A&B->!B",
            "A&B->!B",
            "A&B->B",
            "(A&B->B)->(A&B->!B)->!(A&B)",
            "(A&B->!B)->!(A&B)",
            "!(A&B)"
    };
    final static String[] FALSE_TRUE = {
            "!A->A&B->!A",
            "A&B->!A",
            "A&B->A",
            "(A&B->A)->(A&B->!A)->!(A&B)",
            "(A&B->!A)->!(A&B)",
            "!(A&B)"
    };
    final static String[] FALSE_FALSE = {
            "!B->A&B->!B",
            "A&B->!B",
            "A&B->B",
            "(A&B->B)->(A&B->!B)->!(A&B)",
            "(A&B->!B)->!(A&B)",
            "!(A&B)"
    };

    @Override
    protected String[] getSolution(boolean left, boolean right) {
        if (left) {
            if (right) {
                return TRUE_TRUE;
            } else {
                return TRUE_FALSE;
            }
        } else {
            if (right) {
                return FALSE_TRUE;
            } else {
                return FALSE_FALSE;
            }
        }
    }

}
