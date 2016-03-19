package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import com.polarnick.mathlogic.assumptionCheckerAndConverter.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Date: 10.01.14 at 20:23
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Variable extends Expression {

    public final String name;
    public final List<Variable> variables;

    public Variable(String name, List<Variable> variables) {
        this.name = name;
        this.variables = variables;
    }

    public Variable(String name) {
        this(name, new ArrayList<>(0));
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(name);
        if (variables.size() > 0) {
            s.append("(");
            for (Variable var : variables) {
                s.append(var.toString());
            }
            s.append(")");
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Variable variable = (Variable) o;

        if (!name.equals(variable.name) || variables.size() != variable.variables.size()) {
            return false;
        }
        for (int i = 0; i < variables.size(); i++) {
            if (!variables.get(i).equals(variable.variables.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean compareToPattern(Expression pattern, Map<String, Expression> patternValues) {
        if (pattern.getClass() == getClass()) {
            return compareToExpression(pattern);
        }
        return super.compareToPattern(pattern, patternValues);
    }

    @Override
    public List<Pair<Expression, Expression>> diffToExpression(Expression expression) {
        List<Pair<Expression, Expression>> diff = new ArrayList<>(0);
        if (this == expression) {
            return diff;
        }
        if (!this.toString().equals(expression.toString())) {
            if (!(expression instanceof Variable)
                    || !name.equals(((Variable) expression).name)
                    || variables.size() != ((Variable) expression).variables.size()) {
                diff.add(new Pair<>(this, expression));
            } else {
                for (int i = 0; i < variables.size(); i++) {
                    diff.addAll(variables.get(i).diffToExpression(((Variable) expression).variables.get(i)));
                }
            }
        }
        return diff;
    }

    public List<Variable> getFreeVariables(List<Variable> busyVariables) {
        ArrayList<Variable> vars = new ArrayList<>();
        for (Variable v : this.getAllVariables()) {
            if (!busyVariables.contains(v)) {
                vars.add(v);
            }
        }
        return vars;
    }

    public List<Variable> getBusyVariables() {
        return new ArrayList<>();
    }

    public List<Variable> getAllVariables() {
        ArrayList<Variable> vars = new ArrayList<>();
        if (variables.size() == 0) {
            vars.add(this);
            return vars;
        } else {
            for (Variable v : variables) {
                vars.addAll(v.getAllVariables());
            }
            return vars;
        }
    }

    public Expression substitute(Variable x, Expression expression) {
        if (this.equals(x)) {
            return expression;
        } else {
            return this;
        }
    }

}
