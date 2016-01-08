package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import java.util.Map;

/**
 * Date: 08.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class Exists extends Expression {

    private final Variable variable;
    private final Expression expression;

    public Exists(Variable variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "?" + expression;
    }

    @Override
    public boolean compareToPattern(Expression pattern, Map<String, Expression> patternValues) {
        if (pattern.getClass() == getClass()) {
            Exists existsPattern = (Exists) pattern;
            return variable.compareToPattern(existsPattern.variable, patternValues)
                    && expression.compareToPattern(existsPattern.expression, patternValues);
        }
        return super.compareToPattern(pattern, patternValues);
    }

    @Override
    public boolean compareToExpression(Expression expression) {
        return expression.getClass() == Exists.class
                && this.expression.compareToExpression(((Exists) expression).expression)
                && this.variable.compareToExpression(((Exists) expression).variable);
    }
}
