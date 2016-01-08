package com.polarnick.mathlogic.assumptionCheckerAndConverter.parsers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.*;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.Exists;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.ForAll;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * ⟨выражение⟩ ::= ⟨дизъюнкция⟩ | ⟨дизъюнкция⟩ ‘->’ ⟨выражение⟩
 * ⟨дизъюнкция⟩ ::= ⟨конъюнкция⟩ | ⟨дизъюнкция⟩ ‘|’ ⟨конъюнкция⟩
 * ⟨конъюнкция⟩ ::= ⟨унарное⟩ | ⟨конъюнкция⟩ ‘&’ ⟨унарное⟩
 * ⟨унарное⟩ ::= ⟨предикат⟩ | ‘!’ ⟨унарное⟩ | ‘(’ ⟨выражение⟩ ‘)’ | (‘@’ | ‘?’) ⟨переменная⟩ ⟨унарное⟩
 * ⟨переменная⟩ ::= (‘a’...‘z’) {‘0’...‘9’}*
 * ⟨предикат⟩ ::= (‘A’...‘Z’) {‘0’...‘9’}* [‘(’ ⟨терм⟩ {‘,’⟨терм⟩}* ‘)’]
 * ⟨терм⟩ ::= (‘a’...‘z’) {‘0’...‘9’}* ‘(’ ⟨терм⟩ {‘,’⟨терм⟩}* ‘)’ | ⟨переменная⟩ | ‘(’ ⟨терм⟩ ‘)’
 */

/**
 * Date: 10.01.14 at 19:47
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ExpressionParser {

    protected static final String VARIABLE_REGEXP = "[a-z][0-9]*";
    protected static final String PREDICATE_REGEXP = "[A-Z][0-9]*";

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

    // ⟨конъюнкция⟩ ::= ⟨унарное⟩ | ⟨конъюнкция⟩ ‘&’ ⟨унарное⟩
    private Expression parseConjunction(StringWithBrackets source, int from, int to) {
        for (int i = to - 1; i >= from; i--) {
            if (source.charAt(i) == ')') {
                i = source.getOpeningBracket(i);
                continue;
            }
            if (source.charAt(i) == '&') {
                return new Conjunction(parseConjunction(source, from, i), parseUnary(source, i + 1, to));
            }
        }
        return parseUnary(source, from, to);
    }

    // ⟨унарное⟩ ::= ⟨предикат⟩ | ‘!’ ⟨унарное⟩ | ‘(’ ⟨выражение⟩ ‘)’ | (‘@’ | ‘?’) ⟨переменная⟩ ⟨унарное⟩
    private Expression parseUnary(StringWithBrackets source, int from, int to) {
        if (source.charAt(from) == '!') {
            return new Negation(parseUnary(source, from + 1, to));
        } else if (source.charAt(from) == '(') {
            Preconditions.checkState(source.getClosingBracket(from) == to - 1);
            return parseExpression(source, from + 1, to - 1);
        } else if (source.charAt(from) == '@' || source.charAt(from) == '?') {
            int variableLength = getMaxPrefixLength(source.string.substring(from + 1, to), VARIABLE_REGEXP);
            Variable variable = parseVariable(source, from + 1, from + 1 + variableLength);
            Expression unary = parseUnary(source, from + 1 + variableLength, to);

            if (source.charAt(from) == '@') {
                return new ForAll(variable, unary);
            } else {
                assert(source.charAt(from) == '?');
                return new Exists(variable, unary);
            }
        } else {
            return parsePredicate(source, from, to);
        }
    }

    // ⟨переменная⟩ ::= (‘a’...‘z’) {‘0’...‘9’}*
    private Variable parseVariable(StringWithBrackets source, int from, int to) {
        String name = source.string.substring(from, to);
        Preconditions.checkState(name.matches(VARIABLE_REGEXP), "Incorrect variable name: " + name);
        return createNamed(name);
    }

    // ⟨предикат⟩ ::= (‘A’...‘Z’) {‘0’...‘9’}* [‘(’ ⟨терм⟩ {‘,’⟨терм⟩}* ‘)’]
    private Expression parsePredicate(StringWithBrackets source, int from, int to) {
        int predicateLength = getMaxPrefixLength(source.string.substring(from, to), PREDICATE_REGEXP);
        String predicateName = source.string.substring(from, from + predicateLength);
        List<Variable> terms = parseTerms(source, from + predicateLength, to);
        return createNamed(predicateName, terms);
    }

    // ⟨терм⟩ ::= (‘a’...‘z’) {‘0’...‘9’}* ‘(’ ⟨терм⟩ {‘,’⟨терм⟩}* ‘)’ | ⟨переменная⟩ | ‘(’ ⟨терм⟩ ‘)’
    private Variable parseTerm(StringWithBrackets source, int from, int to) {
        if (source.charAt(from) == '(') {
            Preconditions.checkState(source.getClosingBracket(from) == to - 1);
            return parseTerm(source, from + 1, to - 1);
        } else {
            int variableLength = getMaxPrefixLength(source.string.substring(from, to), VARIABLE_REGEXP);
            Preconditions.checkState(variableLength > 0);
            String variableName = source.string.substring(from, from + variableLength);
            List<Variable> terms = parseTerms(source, from + variableLength, to);
            return createNamed(variableName, terms);
        }
    }

    // ‘(’ ⟨терм⟩ {‘,’⟨терм⟩}* ‘)’
    private List<Variable> parseTerms(StringWithBrackets source, int from, int to) {
        List<Variable> terms = new ArrayList<>();
        if (from != to) {
            Preconditions.checkState(source.charAt(from) == '(');
            Preconditions.checkState(source.charAt(to - 1) == ')');
            Preconditions.checkState(source.getClosingBracket(from) == to - 1);
            int termFrom = from + 1;
            for (int i = from + 1; i < to - 1; i++) {
                if (source.charAt(i) == '(') {
                    i = source.getClosingBracket(i);
                    continue;
                }
                if (source.charAt(i) == ',') {
                    terms.add(parseTerm(source, termFrom, i));
                    termFrom = i + 1;
                }
            }
            terms.add(parseTerm(source, termFrom, to - 1));
        }
        return terms;
    }

    final protected Variable createNamed(String name) {
        return createNamed(name, new ArrayList<>());
    }

    protected Variable createNamed(String name, List<Variable> terms) {
        Preconditions.checkState(name.matches(VARIABLE_REGEXP) || name.matches(PREDICATE_REGEXP),
                "Incorrect variable/predicate name: " + name);
        return new Variable(name, terms);
    }

    public static int getMaxPrefixLength(String string, String regexp) {
        Matcher m = Pattern.compile(regexp).matcher(string);
        if (!m.find() || m.start() != 0) {
            return -1;
        } else {
            return m.end();
        }
    }

}