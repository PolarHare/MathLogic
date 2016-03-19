package com.polarnick.mathlogic.task7.entities;

/**
 * Date: 09.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class Variable extends Expression {

    public final String name;

    public Variable(String name) {
        this.name = name;
    }

    public Expression rename(String variableKey, Expression value) {
        if (this.name.equals(variableKey)) {
            return value;
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return name;
    }

}