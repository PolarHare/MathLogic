package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import java.util.Map;

/**
 * Date: 08.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class ForAll extends Expression {

    private final Variable variable;
    private final Expression expression;

    public ForAll(Variable variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "!" + expression;
    }

    @Override
    public boolean compareToPattern(Expression pattern, Map<String, Expression> patternValues) {
        if (pattern.getClass() == getClass()) {
            ForAll forAllPattern = (ForAll) pattern;
            return variable.compareToPattern(forAllPattern.variable, patternValues)
                    && expression.compareToPattern(forAllPattern.expression, patternValues);
        }
        return super.compareToPattern(pattern, patternValues);
    }

    @Override
    public boolean compareToExpression(Expression expression) {
        return expression.getClass() == ForAll.class
                && this.expression.compareToExpression(((ForAll) expression).expression)
                && this.variable.compareToExpression(((ForAll) expression).variable);
    }
}
