package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 10.01.14 at 19:39
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public abstract class Expression {

    public boolean compareToPattern(Expression pattern, Map<String, Expression> patternValues) {
        if (pattern.getClass() == NamedAnyExpression.class) {
            NamedAnyExpression namedExpression = (NamedAnyExpression) pattern;
            Expression expression = patternValues.get(namedExpression.getName());
            if (expression != null) {
                return compareToExpression(expression);
            } else {
                patternValues.put(namedExpression.getName(), this);
                return true;
            }
        }
        return false;
    }

    public abstract boolean compareToExpression(Expression expression);
}
