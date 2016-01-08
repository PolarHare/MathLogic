package com.polarnick.mathlogic.assumptionCheckerAndConverter.exceptions;

/**
 * Date: 08.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class FreeVariableException extends LineNumberException {

    public FreeVariableException(int lineNumber) {
        super(lineNumber);
    }

}
