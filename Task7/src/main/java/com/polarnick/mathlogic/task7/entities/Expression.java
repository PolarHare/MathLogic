package com.polarnick.mathlogic.task7.entities;

import java.util.Objects;

/**
 * Date: 10.01.14 at 19:39
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public abstract class Expression {

    public abstract String toString();

    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }
}
