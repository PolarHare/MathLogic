package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import java.util.List;
import java.util.Map;

/**
 * Date: 10.01.14 at 20:26
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public abstract class BinaryExpression extends Expression {

    private Expression left;
    private Expression right;

    public BinaryExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    abstract String getOperator();

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + getOperator() + right + ")";
    }

    @Override
    public boolean compareToPattern(Expression pattern, Map<String, Expression> patternValues) {
        if (this == pattern) {
            return true;
        }
        if (getClass() == pattern.getClass()) {
            BinaryExpression binaryExpressionPattern = (BinaryExpression) pattern;
            return getOperator().equals(binaryExpressionPattern.getOperator())
                    && left.compareToPattern(binaryExpressionPattern.left, patternValues)
                    && right.compareToPattern(binaryExpressionPattern.right, patternValues);
        }
        return super.compareToPattern(pattern, patternValues);
    }

    @Override
    public boolean compareToExpression(Expression expression) {
        if (this == expression) {
            return true;
        }
        if (getClass() == expression.getClass()) {
            BinaryExpression binExpression = (BinaryExpression) expression;
            return getOperator().equals(binExpression.getOperator())
                    && left.compareToExpression(binExpression.left)
                    && right.compareToExpression(binExpression.right);
        }
        return false;
    }

    @Override
    public boolean evaluate(Map<String, Boolean> values) {
        return evaluate(left.evaluate(values), right.evaluate(values));
    }

    protected abstract boolean evaluate(boolean left, boolean right);

    @Override
    public void replace(Map<String, Expression> expForNamedAnyExpression) {
        if (left.getClass() == NamedAnyExpression.class) {
            NamedAnyExpression named = (NamedAnyExpression) left;
            left = expForNamedAnyExpression.get(named.name);
        } else {
            left.replace(expForNamedAnyExpression);
        }
        if (right.getClass() == NamedAnyExpression.class) {
            NamedAnyExpression named = (NamedAnyExpression) right;
            right = expForNamedAnyExpression.get(named.name);
        } else {
            right.replace(expForNamedAnyExpression);
        }
    }

    @Override
    public void addSteps(Map<String, Boolean> varValues, List<Expression> steps) {
        left.addSteps(varValues, steps);
        right.addSteps(varValues, steps);
        Expression.addSteps(getSolution(left.evaluate(varValues), right.evaluate(varValues)),
                left, right, steps);
    }

    protected abstract String[] getSolution(boolean left, boolean right);
}
