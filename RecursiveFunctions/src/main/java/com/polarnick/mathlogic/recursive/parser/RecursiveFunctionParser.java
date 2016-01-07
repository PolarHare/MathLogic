package com.polarnick.mathlogic.recursive.parser;

import com.polarnick.mathlogic.recursive.base.*;

import java.util.*;

/**
 * @author Polyarnyi Nickolay, PolarNick239
 */
public class RecursiveFunctionParser {

    private static String emptyDelim = " \n\t";
    private static String delim = " ,<>()\n\t";

    private static Map<String, AbstractRecursiveFunction> parsers = new HashMap<>();

    static {
        parsers.put("One", new RecursiveFunctionParser("S<N, Z>").parse());

        // Add(a, b) = a + b
        parsers.put("Add", new RecursiveFunctionParser("R<U(1), S<N, U(3)>>").parse());

        // Dec(a) = max(a - 1, 0)
        parsers.put("Dec", new RecursiveFunctionParser("R<Z, U(1)>").parse());
        // Sub(a, b) = max(a - b, 0)
        parsers.put("Sub", new RecursiveFunctionParser("R<U(1), S<Dec, U(3)>>").parse());

        parsers.put("If", new RecursiveFunctionParser("S<R<U(2), U(1)>, U(2), U(3), U(1)>").parse());
        parsers.put("Not", new RecursiveFunctionParser("S<R<N, Z>, U(1), U(1)>").parse());

        parsers.put("Mul", new RecursiveFunctionParser("R<Z, S<Add, U(1), U(3)>>").parse());
        parsers.put("Pow", new RecursiveFunctionParser("R<One, S<Mul, U(1), U(3)>>").parse());

        parsers.put("Equal0", new RecursiveFunctionParser("R<One, Z>").parse());
        parsers.put("EqualLess", new RecursiveFunctionParser("S<Equal0, Sub>").parse());
        parsers.put("Equal", new RecursiveFunctionParser("S<Mul, S<EqualLess, U(1), U(2)>, S<EqualLess, U(2), U(1)>>").parse());

        // MaxDivisor(limit, b) = max a such that (a <= limit) and (a % b == 0)
        parsers.put("MaxDivisor", new RecursiveFunctionParser("S<R<Z, S<If, S<Equal, S<Sub, S<N, U(2)>, U(3)>, U(1)>, S<N, U(2)>, U(3)>>, U(2), U(1)>").parse());
        // Div(a, b) = a // b
        parsers.put("Div", new RecursiveFunctionParser("S<R<Z, S<Add, U(3), S<Equal, S<MaxDivisor, S<N, U(2)>, U(1)>, S<N, U(2)>>>>, U(2), U(1)>").parse());

        // Mod(a, m) = a % m
        parsers.put("Mod", new RecursiveFunctionParser("S<Sub, U(1), S<Mul, Div, U(2)>>").parse());

        // IsPrime(p) = 1 if p is prime else 0
        parsers.put("IsPrime", new RecursiveFunctionParser("S<Not, S<Sub, S<R<Z, S<Add, U(3), S<Equal0, S<Mod, U(1), U(2)>>>>, U(1), U(1)>, One>>").parse());

        parsers.put("NextPrime", new RecursiveFunctionParser("S<Add, M<S<Not, S<IsPrime, S<Add, S<N, U(1)>, U(2)>>>>, S<N, U(1)>>").parse());
        // nthPrime(n) = (n-1)-th prime, nthPrime(0) = 2
        parsers.put("NthPrime", new RecursiveFunctionParser("R<S<N, S<N, Z>>, S<NextPrime, U(2)>>").parse());
        // plog(p, x) = max (r >= 0) such that (x % (p^r) == 0), plog(3, 12) = 2, plog(5, 7) = 0
        parsers.put("PLog", new RecursiveFunctionParser("S<Dec, M<S<Equal0, S<Mod, U(2), S<Pow, U(1), U(3)>>>> >").parse());
    }

    private final String input;
    private final StringTokenizer tokenizer;

    public RecursiveFunctionParser(String input) {
        this.input = input;
        this.tokenizer = new StringTokenizer(this.input, delim, true);
    }

    public AbstractRecursiveFunction parse() {
        return this.parseFunction();
    }

    private String nextNonEmptyToken() {
        String token;
        do {
            token = this.tokenizer.nextToken();
        } while (emptyDelim.contains(token));
        return token;
    }

    private void skipExpectedToken(String expected) {
        String token = this.tokenizer.nextToken();
        if (!token.equals(expected)) {
            throw new IllegalArgumentException("Found token: '" + token + "', while expected: '" + expected + "'.");
        }
    }

    private void skipNextNonEmptyToken(String expected) {
        String token = this.nextNonEmptyToken();
        if (!token.equals(expected)) {
            throw new IllegalArgumentException("Found token: '" + token + "', while expected: '" + expected + "'.");
        }
    }

    private AbstractRecursiveFunction parseFunction() {
        String token = this.nextNonEmptyToken();
        if (token.equals("Z")) {
            return new Zero();
        } else if (token.equals("N")) {
            return new Next();
        } else if (token.equals("U")) {
            this.skipExpectedToken("(");
            int index = Integer.parseInt(this.tokenizer.nextToken());
            this.skipExpectedToken(")");

            return new UProjection(index - 1);
        } else if (token.equals("S")) {
            AbstractRecursiveFunction[] fAndGs = this.parseTemplateFunctions();
            return new Substitution(fAndGs);
        } else if (token.equals("R")) {
            AbstractRecursiveFunction[] fg = this.parseTemplateFunctions();
            return new Recursion(fg);
        } else if (token.equals("M")) {
            AbstractRecursiveFunction[] f = this.parseTemplateFunctions();
            return new Minimization(f);
        } else {
            AbstractRecursiveFunction f = parsers.get(token);
            if (f != null) {
                return f;
            } else {
                throw new IllegalArgumentException("Unexpected token: '" + token + "'!");
            }
        }
    }

    private AbstractRecursiveFunction[] parseTemplateFunctions() {
        this.skipNextNonEmptyToken("<");
        List<AbstractRecursiveFunction> fs = new ArrayList<AbstractRecursiveFunction>();
        while(true) {
            fs.add(parseFunction());
            String token = this.nextNonEmptyToken();
            if (token.equals(">")) {
                break;
            } else {
                assert token.equals(",");
            }
        }
        return fs.toArray(new AbstractRecursiveFunction[fs.size()]);
    }

}
