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
}
