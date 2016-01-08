package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import com.polarnick.mathlogic.assumptionCheckerAndConverter.utils.Pair;

import java.util.ArrayList;
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
    public List<Pair<Expression, Expression>> diffToExpression(Expression expression) {
        List<Pair<Expression, Expression>> diff = new ArrayList<>(0);
        if (this == expression) {
            return diff;
        }
        if (getClass() == expression.getClass() && getOperator().equals(((BinaryExpression) expression).getOperator())) {
            BinaryExpression binExpression = (BinaryExpression) expression;
            diff.addAll(left.diffToExpression(binExpression.left));
            diff.addAll(right.diffToExpression(binExpression.right));
        } else {
            diff.add(new Pair<>(this, expression));
        }
        return diff;
    }

    public List<Variable> getFreeVariables(List<Variable> busyVariables) {
        List<Variable> vars = new ArrayList<>();
        vars.addAll(left.getFreeVariables(busyVariables));
        vars.addAll(right.getFreeVariables(busyVariables));
        return vars;
    }

    public List<Variable> getBusyVariables() {
        List<Variable> vars = new ArrayList<>();
        vars.addAll(left.getBusyVariables());
        vars.addAll(right.getBusyVariables());
        return vars;
    }

    public List<Variable> getAllVariables() {
        List<Variable> vars = new ArrayList<>();
        vars.addAll(left.getAllVariables());
        vars.addAll(right.getAllVariables());
        return vars;
    }

}
