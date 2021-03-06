package com.polarnick.mathlogic.proofChecker.entities;

import java.util.Map;

/**
 * Date: 13.01.14 at 16:55
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class NamedAnyExpression extends Variable {

    public NamedAnyExpression(String name) {
        super(name);
    }

    @Override
    public boolean evaluate(Map<String, Boolean> values) {
        throw new UnsupportedOperationException("Patterns can not be evaluated!");
    }
}
