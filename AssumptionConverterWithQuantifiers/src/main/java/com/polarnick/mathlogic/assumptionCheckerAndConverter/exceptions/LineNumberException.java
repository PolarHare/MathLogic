package com.polarnick.mathlogic.assumptionCheckerAndConverter.exceptions;

/**
 * Date: 09.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class LineNumberException extends RuntimeException {

    public int lineNumber;

    public LineNumberException(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String toString() {
        return "Вывод некорректен начиная с формулы номер №" + lineNumber;
    }

}
