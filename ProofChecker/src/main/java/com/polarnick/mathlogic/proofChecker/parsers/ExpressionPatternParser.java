package com.polarnick.mathlogic.proofChecker.parsers;

import com.google.common.base.Preconditions;
import com.polarnick.mathlogic.proofChecker.entities.Expression;
import com.polarnick.mathlogic.proofChecker.entities.NamedAnyExpression;

/**
 * Date: 13.01.14 at 17:36
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ExpressionPatternParser extends ExpressionParser {

    protected Expression createNamed(String name) {
        Preconditions.checkState(name.matches("[A-Z][0-9]*"), "Incorrect variable name: " + name);
        return new NamedAnyExpression(name);
    }

}
