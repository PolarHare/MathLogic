package com.polarnick.mathlogic.proofChecker.entities;

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

    @Override
    public boolean evaluate(Map<String, Boolean> values) {
        return !expression.evaluate(values);
    }

    @Override
    public void replace(Map<String, Expression> expForNamedAnyExpression) {
        if (expression.getClass() == NamedAnyExpression.class) {
            NamedAnyExpression named = (NamedAnyExpression) expression;
            expression = expForNamedAnyExpression.get(named.name);
        } else {
            expression.replace(expForNamedAnyExpression);
        }
    }

    final static String[] TRUE = new String[]{
            "A->!A->A",
            "!A->A",
            "!A->!A->!A",
            "(!A->!A->!A)->(!A->(!A->!A)->!A)->!A->!A",
            "(!A->(!A->!A)->!A)->!A->!A",
            "!A->(!A->!A)->!A",
            "!A->!A",
            "(!A->A)->(!A->!A)->!!A",
            "(!A->!A)->!!A",
            "!!A"
    };

    final static String[] FALSE = new String[]{
    };

    @Override
    public void addSteps(Map<String, Boolean> varValues, List<Expression> steps) {
        expression.addSteps(varValues, steps);
        Expression.addSteps(expression.evaluate(varValues) ? TRUE : FALSE, expression, null, steps);
    }
}
