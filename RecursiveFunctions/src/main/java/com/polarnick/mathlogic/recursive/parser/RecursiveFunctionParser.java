package com.polarnick.mathlogic.recursive.parser;

import com.polarnick.mathlogic.recursive.base.*;
import com.polarnick.mathlogic.recursive.utils.FunctionsProfiler;

import java.util.*;

/**
 * @author Polyarnyi Nickolay, PolarNick239
 */
public class RecursiveFunctionParser {

    private static boolean NATIVE_OPERATIONS = true;

    private static String emptyDelim = " \n\t";
    private static String delim = " ,<>()\n\t";

    private static FunctionsProfiler profiler = new FunctionsProfiler(1000);
    private static Map<String, AbstractRecursiveFunction> parsers = new HashMap<>();

    public static void registerFunction(String name, AbstractRecursiveFunction function) {
        parsers.put(name, profiler.profile(name, function));
    }

    public static void parseAndRegisterFunction(String name, String function) {
        registerFunction(name, new RecursiveFunctionParser(function).parse());
    }

    static {
        parseAndRegisterFunction("One", "S<N, Z>");

        // Add(a, b) = a + b
        parseAndRegisterFunction("Add", "R<U(1), S<N, U(3)>>");

        // Dec(a) = max(a - 1, 0)
        parseAndRegisterFunction("Dec", "R<Z, U(1)>");
        if (NATIVE_OPERATIONS) {
            registerFunction("Dec", new AbstractRecursiveFunction() {
                @Override
                public int execute(int... args) {
                    return Math.max(args[0] - 1, 0);
                }
            });
        }
        // Sub(a, b) = max(a - b, 0)
        parseAndRegisterFunction("Sub", "R<U(1), S<Dec, U(3)>>");

        parseAndRegisterFunction("If", "S<R<U(2), U(1)>, U(2), U(3), U(1)>");
        parseAndRegisterFunction("Not", "S<R<N, Z>, U(1), U(1)>");

        parseAndRegisterFunction("Mul", "R<Z, S<Add, U(1), U(3)>>");
        if (NATIVE_OPERATIONS) {
            registerFunction("Mul", new AbstractRecursiveFunction() {
                @Override
                public int execute(int... args) {
                    long a = args[0];
                    long b = args[1];
                    if (a * b > Integer.MAX_VALUE) {
                        throw new IllegalArgumentException("Integer overflow!");
                    }
                    return args[0] * args[1];
                }
            });
        }
        parseAndRegisterFunction("Pow", "R<One, S<Mul, U(1), U(3)>>");

        parseAndRegisterFunction("Equal0", "R<One, Z>");
        if (NATIVE_OPERATIONS) {
            registerFunction("Equal0", new AbstractRecursiveFunction() {
                @Override
                public int execute(int... args) {
                    if (args[0] == 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        parseAndRegisterFunction("EqualLess", "S<Equal0, Sub>");
        parseAndRegisterFunction("Equal", "S<Mul, S<EqualLess, U(1), U(2)>, S<EqualLess, U(2), U(1)>>");

        // MaxDivisor(limit, b) = max a such that (a <= limit) and (a % b == 0)
        parseAndRegisterFunction("MaxDivisor", "S<R<Z, S<If, S<Equal, S<Sub, S<N, U(2)>, U(3)>, U(1)>, S<N, U(2)>, U(3)>>, U(2), U(1)>");
        // Div(a, b) = a // b
        parseAndRegisterFunction("Div", "S<R<Z, S<Add, U(3), S<Equal, S<MaxDivisor, S<N, U(2)>, U(1)>, S<N, U(2)>>>>, U(2), U(1)>");
        if (NATIVE_OPERATIONS) {
            registerFunction("Div", new AbstractRecursiveFunction() {
                @Override
                public int execute(int... args) {
                    if (args[1] == 0) {
                        return 0;
                    }
                    return args[0] / args[1];
                }
            });
        }

        // Mod(a, m) = a % m
        parseAndRegisterFunction("Mod", "S<Sub, U(1), S<Mul, Div, U(2)>>");
        if (NATIVE_OPERATIONS) {
            registerFunction("Mod", new AbstractRecursiveFunction() {
                @Override
                public int execute(int... args) {
                    if (args[1] == 0) {
                        return args[0];
                    }
                    return args[0] % args[1];
                }
            });
        }

        // IsPrime(p) = 1 if p is prime else 0
        parseAndRegisterFunction("IsPrime", "S<Not, S<Sub, S<R<Z, S<Add, U(3), S<Equal0, S<Mod, U(1), U(2)>>>>, U(1), U(1)>, One>>");

        parseAndRegisterFunction("NextPrime", "S<Add, M<S<Not, S<IsPrime, S<Add, S<N, U(1)>, U(2)>>>>, S<N, U(1)>>");
        // nthPrime(n) = (n-1)-th prime, nthPrime(0) = 2
        parseAndRegisterFunction("NthPrime", "R<S<N, S<N, Z>>, S<NextPrime, U(2)>>");
        // plog(p, x) = max (r >= 0) such that (x % (p^r) == 0), plog(3, 12) = 2, plog(5, 7) = 0
        parseAndRegisterFunction("PLog", "S<Dec, M<S<Equal0, S<Mod, U(2), S<Pow, U(1), U(3)>>>> >");

        for (String[] s: new String[][]{
                {"Two", "S<N, One>"},
                {"Two2", "S<Two, U(1)>"},
                {"StackSize", "S<Dec, S<PLog, Two, U(1)>>"},
                {"PushWithoutChangeStack", "S<Mul, U(1), S<Pow, S<NthPrime, S<N, S<StackSize, U(1)>>>, S<N, U(2)>>>"},
                {"Push", "S<Mul, Two2, PushWithoutChangeStack>"},
                {"Push2", "S<Push, S<Push, U(1), U(2)>, U(3)>"},
                {"Push3", "S<Push, S<Push, S<Push, U(1), U(2)>, U(3)>, U(4)>"},
                {"Top", "S<Dec, S<PLog, S<NthPrime, StackSize>, U(1)>>"},
                {"Top2", "S<Dec, S<PLog, S<NthPrime, S<Dec, StackSize>>, U(1)>>"},
                {"Pop", "S<Div, S<Div, U(1), S<Pow, S<NthPrime,  StackSize>, S<N, Top>>>, Two>"},
                {"Pop2", "S<Pop, S<Pop, U(1)>>"},
                {"First", "S<Push, Pop2, S<N, Top>>"},
                {"Second", "S<Push2, Pop2, S<Dec, Top2>, One>"},
                {"Third", "S<Push3, Pop2, S<Dec, Top2>, Top2, S<Dec, Top>>"},
                {"Other", "S<If, S<Equal, Top, Z>, Second, Third>"},
                {"AckermannStep", "S<If, S<Equal, Top2, Z>, First, Other>"},
                {"Init", "S<Push2, Two, U(1), U(2)>"},
                {"AckermannKSteps", "R<Init, S<AckermannStep, U(4)>>"},
                {"StackSizeKSteps", "S<StackSize, S<AckermannKSteps, U(1), U(2), U(3)>>"},
                {"NumberOfSteps", "M<S<Sub, S<StackSizeKSteps, U(1), U(2), U(3)>, One>>"},
                {"Ackermann", "S<Top, S<AckermannKSteps, U(1), U(2), S<NumberOfSteps, U(1), U(2)>>>"}
        }) {
            String name = s[0];
            String value = s[1];
            parseAndRegisterFunction(name, value);
        }
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
