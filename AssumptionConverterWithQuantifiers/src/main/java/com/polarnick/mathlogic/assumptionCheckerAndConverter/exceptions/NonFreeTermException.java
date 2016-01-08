package com.polarnick.mathlogic.assumptionCheckerAndConverter.exceptions;

import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.Expression;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.Variable;

/**
 * Date: 08.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class NonFreeTermException extends LineNumberException {
    public Expression a;
    public Variable x;
    public Expression newX;

    public NonFreeTermException(Expression a, Variable x, Expression newX) {
        super(-1);
        this.a = a;
        this.x = x;
        this.newX = newX;
    }

    @Override
    public String toString() {
        return super.toString() + ": терм " + newX + " не свободен для подстановки в формулу " + a
                + " вместо переменной " + x + ".";
    }
}
