package com.polarnick.mathlogic.proofChecker.parsers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.polarnick.mathlogic.proofChecker.entities.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * ⟨выражение⟩ ::= ⟨дизъюнкция⟩ | ⟨дизъюнкция⟩ ‘->’ ⟨выражение⟩
 * ⟨дизъюнкция⟩ ::= ⟨конъюнкция⟩ | ⟨дизъюнкция⟩ ‘|’ ⟨конъюнкция⟩
 * ⟨конъюнкция⟩ ::= ⟨отрицание⟩ | ⟨конъюнкция⟩ ‘&’ ⟨отрицание⟩
 * ⟨отрицание⟩ ::= (‘A’ : : : ‘Z’) {‘0’ : : : ‘9’}∗ | ‘!’ ⟨отрицание⟩ | ‘(’ ⟨выражение⟩ ‘)’
 */

/**
 * Date: 10.01.14 at 19:47
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ExpressionParser {

    public List<String> getAllVariables(String line) {
        Set<String> matches = new HashSet<String>();
        String regexp = "[A-Z][0-9]*";
        Matcher m = Pattern.compile("(?=(" + regexp + "))").matcher(line);
        while (m.find()) {
            matches.add(m.group(1));
        }
        return Lists.newArrayList(matches);
    }

    private static class StringWithBrackets {
        private final String string;
        private final int[] indexOfPairBracket;

        private StringWithBrackets(String string) {
            this.string = string;
            this.indexOfPairBracket = new int[string.length()];
            Stack<Integer> stack = new Stack<Integer>();
            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) == '(') {
                    stack.push(i);
                } else if (string.charAt(i) == ')') {
                    Preconditions.checkArgument(!stack.empty(), "Input string has no pair opening bracket for bracket at index=" + i);
                    int openIndex = stack.pop();
                    indexOfPairBracket[openIndex] = i;
                    indexOfPairBracket[i] = openIndex;
                }
            }
            Preconditions.checkArgument(stack.empty(), "Input string has unclosed bracket");
        }

        public char charAt(int index) {
            return string.charAt(index);
        }

        public int getClosingBracket(int openIndex) {
            Preconditions.checkArgument(string.charAt(openIndex) == '(', "This is not an opening bracket");
            return indexOfPairBracket[openIndex];
        }

        public int getOpeningBracket(int closeIndex) {
            Preconditions.checkArgument(string.charAt(closeIndex) == ')', "This is not an closing bracket");
            return indexOfPairBracket[closeIndex];
        }
    }

    public Expression parseExpression(String source) {
        return parseExpression(new StringWithBrackets(source), 0, source.length());
    }

    // ⟨выражение⟩ ::= ⟨дизъюнкция⟩ | ⟨дизъюнкция⟩ ‘->’ ⟨выражение⟩
    private Expression parseExpression(StringWithBrackets source, int from, int to) {
        for (int i = from; i < to; i++) {
            if (source.charAt(i) == '(') {
                i = source.getClosingBracket(i);
                continue;
            }
            if (source.charAt(i) == '-') {
                Preconditions.checkArgument(source.charAt(i + 1) == '>', "Incomplete symbol '->'");
                return new Consecution(parseDisjunction(source, from, i), parseExpression(source, i + 2, to));
            }
        }
        return parseDisjunction(source, from, to);
    }

    // ⟨дизъюнкция⟩ ::= ⟨конъюнкция⟩ | ⟨дизъюнкция⟩ ‘|’ ⟨конъюнкция⟩
    private Expression parseDisjunction(StringWithBrackets source, int from, int to) {
        for (int i = to - 1; i >= from; i--) {
            if (source.charAt(i) == ')') {
                i = source.getOpeningBracket(i);
                continue;
            }
            if (source.charAt(i) == '|') {
                return new Disjunction(parseDisjunction(source, from, i), parseConjunction(source, i + 1, to));
            }
        }
        return parseConjunction(source, from, to);
    }

    // ⟨конъюнкция⟩ ::= ⟨отрицание⟩ | ⟨конъюнкция⟩ ‘&’ ⟨отрицание⟩
    private Expression parseConjunction(StringWithBrackets source, int from, int to) {
        for (int i = to - 1; i >= from; i--) {
            if (source.charAt(i) == ')') {
                i = source.getOpeningBracket(i);
                continue;
            }
            if (source.charAt(i) == '&') {
                return new Conjunction(parseConjunction(source, from, i), parseNegation(source, i + 1, to));
            }
        }
        return parseNegation(source, from, to);
    }

    // ⟨отрицание⟩ ::= (‘A’ : : : ‘Z’) {‘0’ : : : ‘9’}∗ | ‘!’ ⟨отрицание⟩ | ‘(’ ⟨выражение⟩ ‘)’
    private Expression parseNegation(StringWithBrackets source, int from, int to) {
        if (source.charAt(from) == '!') {
            return new Negation(parseNegation(source, from + 1, to));
        } else if (source.charAt(from) == '(') {
            Preconditions.checkState(source.getClosingBracket(from) == to - 1);
            return parseExpression(source, from + 1, to - 1);
        } else {
            return createNamed(source.string.substring(from, to));
        }
    }

    protected Expression createNamed(String name) {
        Preconditions.checkState(name.matches("[A-Z][0-9]*"), "Incorrect variable name: " + name);
        return new Variable(name);
    }

}