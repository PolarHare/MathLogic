package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import java.util.List;
import java.util.Map;

/**
 * Date: 10.01.14 at 19:41
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Negation extends Expression {

    public Expression expression;

    public Negation(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "!" + expression;
    }

    @Override
    public boolean compareToPattern(Expression pattern, Map<String, Expression> patternValues) {
        if (pattern.getClass() == getClass()) {
            Negation negationPattern = (Negation) pattern;
            return expression.compareToPattern(negationPattern.expression, patternValues);
        }
        return super.compareToPattern(pattern, patternValues);
    }

    @Override
    public boolean compareToExpression(Expression expression) {
        return expression.getClass() == Negation.class && this.expression.compareToExpression(((Negation) expression).expression);
    }

}
