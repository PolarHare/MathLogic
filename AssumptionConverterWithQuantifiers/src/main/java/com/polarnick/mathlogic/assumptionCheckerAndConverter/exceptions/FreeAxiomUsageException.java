package com.polarnick.mathlogic.assumptionCheckerAndConverter.exceptions;

import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.Expression;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.Variable;

/**
 * Date: 08.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class FreeAxiomUsageException extends LineNumberException {
    public final Variable x;
    public final Expression assumption;

    public FreeAxiomUsageException(Variable x, Expression assumption) {
        super(-1);
        this.x = x;
        this.assumption = assumption;
    }

    @Override
    public String toString() {
        return super.toString() + ": используется правило с квантором по переменной " + x
                + ", входящей свободно в допущение " + assumption + ".";
    }
}
