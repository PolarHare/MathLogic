package com.polarnick.mathlogic.task7;

import com.polarnick.mathlogic.task7.entities.*;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Date: 09.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class Generator {

    private final PrintStream out;

    public Generator(PrintStream out) {
        this.out = out;
    }

    // Proofs:   (a=0 | a=1 | ... | a=n) -> a <= n
    // equal to: (a=0 | a=1 | ... | a=n) -> (?b(a+b=n))
    public void printWholeProof(int n) {
        Predicate toProof = newExists("b", newEquals(newSum(newVar("a"), newVar("b")), newVal(n)));  // ?b(a+b=n)
        List<Expression> aEualIs = new ArrayList<>(n + 1);  // {a=0, a=1, ... a=n}
        for (int i = 0; i <= n; i++) {
            BinaryExpression implication = printProofForI(i, n);  // a=i -> (?b(a+b=n))
            BinaryExpression aEqualI = (BinaryExpression) implication.left;  // a=i
            assert implication.right.equals(toProof);  // ?b(a+b=n)
            aEualIs.add(aEqualI);
        }
        printMergedProof(aEualIs, toProof);  // (a=0 | a=1 | ... | a=n) -> (?b(a+b=n))
    }

    // Proofs:   a=i -> a <= n
    // equal to: a=i -> (?b(a+b=n))
    // Post:
    //           a=a+0 -> a=i -> a+0=i
    //
    //           a=i -> a+0=i
    //           ... for (j=0...(n-i-1)):
    //           a+j=(i+j) -> (a+j)'=(i+j+1)
    //           (a+j)'=(i+j+1) -> a+(j+1)=(i+j+1)
    //           ... merging implications:
    //           a=i -> a+(n-i)=n
    //
    //           a=i -> (?b(a+b=n))
    // Example:  (i=2, n=4)
    //           a=a+0 -> a=2 -> a+0=2
    //
    //           a=2 -> a+0=2
    //           a+0=2 -> (a+0)'=3
    //           (a+0)'=3 -> a+1=3
    //           a+1=3 -> (a+1)'=4
    //           (a+1)'=4 -> a+2=4
    //           ... merging implications:
    //           a=2 -> a+2=4
    //
    //           a=2 -> (?b(a+b=4))
    public BinaryExpression printProofForI(int i, int n) {
        Variable a = newVar("a");
//        BinaryExpression aEqualI = newEquals(a, newVal(i));  // a=i

//        Expression tmp = newImpl(aEqualI, newEquals(newSum(a, newVal(0)), newVal(i)));  // a=i -> a+0=i
        //       a=a+0 -> a=i -> a+0=i
        BinaryExpression proved1 = printlnArithmeticAxiom2(a, newSum(a, newVal(0)), newVal(i)); // A2: a=b->a=c=>b=c | a=a, b=a+0, c=i

        //       a+0=a  // A6
        BinaryExpression aPlusZeroA = printlnArithmeticAxiom6(a);
        //       a=a+0  // a=b->b=a
        printlnSwapped(aPlusZeroA);
        //       a=i -> a+0=i
        println(proved1.right); // Modus ponens

        Expression aZeroEqI = newEquals(newSum(a, newVal(0)), newVal(i));  // a+0=i
        BinaryExpression aEqualI = newEquals(a, newVal(i));  // a=i

        List<Expression> steps = new ArrayList<>();
        steps.add(aZeroEqI);
        for (int j = 0; j <= n - i - 1; j++) {
            //   a+j=<i+j> -> (a+j)'=<i+j+1>
            printlnArithmeticAxiom1(newSum(a, newVal(j)), newVal(i + j)); // A1: a=b->a'=b' | a=a+j, b=i+j

            //   a+<j+1>=(a+j)'
            BinaryExpression aj = printlnArithmeticAxiom5(a, newVal(j)); // A5: a+b'=(a+b)'
            //   (a+j)'=a+<j+1>
            printlnSwapped(aj);

            //   (a+j)'=a+<j+1> -> (a+j)'=<i+j+1> -> a+<j+1>=<i+j+1>  | A2: a=b->a=c->b=c
            BinaryExpression abacbc = printlnArithmeticAxiom2(newInc(newSum(a, newVal(j))), newSum(a, newVal(j + 1)), newVal(i + j + 1));
            //   (a+j)'=<i+j+1> -> a+<j+1>=<i+j+1>
            println(abacbc.right);

            BinaryExpression acbc = (BinaryExpression) abacbc.right;
            steps.add(acbc.left);
            steps.add(acbc.right);
        }

        // aEqualI: a=i
        // steps:   {x0, x1, ... xn}
        // Pre:
        //          a -> x0
        //          x0 -> x1
        //          ...
        //          xi -> x(i+1)
        //          ...
        //          x(n-1) -> xn
        // Post:
        //          ... merging implications:
        //          a -> xn
        for (int j = 0; j < steps.size() - 1; j++) {
            // Pre:
            //      x1 -> x2
            //      x2 -> x3
            // Post:
            //      ...
            //      x1 -> x3
            Expression x2 = steps.get(j);
            Expression x3 = steps.get(j + 1);
            printMergedImplications(aEqualI, x2, x3);
        }

        Expression exampleSolution = steps.get(steps.size() - 1); // a+(n-i)=n
        Expression existsSolution = newExists("b", newEquals(newSum(a, newVar("b")), newVal(n))); // (?b(a+b=n))

        //           (a+(n-i)=n) -> (?b(a+b=n))
        Expression tmp1 = println(newImpl(exampleSolution, existsSolution));
        //           ((a+(n-i)=n) -> (?b(a+b=n))) -> a=i -> ((a+(n-i)=n) -> (?b(a+b=n))))
        println(newImpl(tmp1, newImpl(aEqualI, tmp1)));
        //           a=i -> ((a+(n-i)=n) -> (?b(a+b=n)))
        Expression tmp2 = println(newImpl(aEqualI, tmp1));

        BinaryExpression tmp3 = newImpl(tmp2, newImpl(aEqualI, existsSolution));
        //           (a=i -> (a+(n-i)=n)) -> (a=i -> ((a+(n-i)=n) -> (?b(a+b=n)))) -> (a=i -> (?b(a+b=n)))
        println(newImpl(newImpl(aEqualI, exampleSolution), tmp3));
        //           (a=i -> ((a+(n-i)=n) -> (?b(a+b=n)))) -> (a=i -> (?b(a+b=n)))
        println(tmp3);
        //           (a=i -> (?b(a+b=n)))
        return (BinaryExpression) println(newImpl(aEqualI, existsSolution));
    }

    // Proofs:   (x0 | x1 | ... | xn) -> y
    // Pre:
    //           x0 -> y
    //           x1 -> y
    //           ...
    //           xn -> y
    // Post:
    //           (x0 -> y) -> (x1 -> y) -> (x0 | x1 -> y)
    //           (x1 -> y) -> (x0 | x1 -> y)
    //           (x0 | x1) -> y
    //
    //           (x0 | x1 -> y) -> (x2 -> y) -> (x0 | x1 | x2 -> y)
    //           (x2 -> y) -> (x0 | x1 | x2 -> y)
    //           (x0 | x1 | x2) -> y
    //
    //           ...
    //           (x0 | x1 | ... | xi -> y) -> (x(i+1) -> y) -> (x0 | x1 | ... | x(i+1) -> y)
    //           (x(i+1) -> y) -> (x0 | x1 | ... | x(i+1) -> y)
    //           (x0 | x1 | ... | x(i+1)) -> y
    //           ...
    //           (x0 | x1 | ... | xn) -> y
    public BinaryExpression printMergedProof(List<Expression> xs, Expression y) {
        Expression[] xsOrs = new Expression[xs.size()];  // xsOrs[i] = (x0 | x1 | ... | xi)
        xsOrs[0] = xs.get(0);
        for (int i = 1; i < xs.size(); i++) {
            xsOrs[i] = newOr(xsOrs[i - 1], xs.get(i));
        }
        BinaryExpression implication = newImpl(xsOrs[xs.size() - 1], y);  // (x0 | x1 | ... | xn) -> y
        for (int i = 1; i < xs.size(); i++) {
            Expression tmp2 = newImpl(xs.get(i), y);  // x(i+1) -> y
            Expression tmp3 = newImpl(xsOrs[i], y);  // (x0 | x1 | ... | x(i+1)) -> y
            //   (x0 | x1 | ... | xi -> y) -> (x(i+1) -> y) -> ((x0 | x1 | ... | x(i+1)) -> y)
            println(newImpl(newImpl(xsOrs[i - 1], y), newImpl(tmp2, tmp3)));
            //   (x(i+1) -> y) -> (x0 | x1 | ... | x(i+1) -> y)
            println(newImpl(tmp2, tmp3));
            //   (x0 | x1 | ... | x(i+1)) -> y
            println(tmp3);
        }
        return implication;
    }

    // Proofs:   a -> c
    // Pre:
    //           a -> b
    //           b -> c
    // Post:
    //           a -> (b -> c)
    //           (a -> b) -> (a -> b -> c) -> (a -> c)
    //           (a -> b -> c) -> (a -> c)
    //           a -> c
    public BinaryExpression printMergedImplications(Expression a, Expression b, Expression c) {
        BinaryExpression ab = newImpl(a, b);
        BinaryExpression bc = newImpl(b, c);
        BinaryExpression ac = newImpl(a, c);
        BinaryExpression abc = newImpl(a, bc);
        // (b -> c) -> a -> (b -> c)
        println(newImpl(bc, abc));
        // a -> (b -> c)
        println(abc);
        // (a -> b) -> (a -> b -> c) -> (a -> c)
        println(newImpl(ab, newImpl(abc, ac)));
        // (a -> b -> c) -> (a -> c)
        println(newImpl(abc, ac));
        // a -> c
        println(ac);
        return ac;
    }


    public Expression println(Expression e) {
        System.out.println(e.toString());
        out.println(e.toString());
        return e;
    }

    // Proofs: expr[key := value]
    // Pre:
    //         expr
    public Expression printlnRename(Expression expr, String key, Expression value) {
        if (key.equals(value.toString())) {
            return expr;
        }
        Expression someFact = newImpl(newNot(newNot(newVar("X"))), newVar("X"));
        // Pre: expr
        println(someFact);  // someFact := x=y->x'=y'
        println(newImpl(expr, newImpl(someFact, expr)));  // expr->someFact->expr
        println(newImpl(someFact, expr));  // someFact->expr
        println(newImpl(someFact, newForEach(key, expr)));  // someFact->@key(expr)
        println(newForEach(key, expr));  // @key(expr)
        Expression renamedExpr = expr.rename(key, value);
        println(newImpl(newForEach(key, expr), renamedExpr));  // @key(expr)->expr[key:=value]
        println(renamedExpr);  // expr[key:=value]
        return renamedExpr;
    }

    // Proofs: b=a
    // Pre:
    //         a=b
    public BinaryExpression printlnSwapped(BinaryExpression ab) {
        Expression a = ab.left;
        Expression b = ab.right;

        printlnArithmeticAxiom6(newVar("a"));  // a+0=a
        BinaryExpression a2 = printlnArithmeticAxiom2(newSum(newVar("a"), newVal(0)), newVar("a"), newVar("a"));  // a+0=a->a+0=a->a=a
        println(a2.right);  // a+0=a->a=a
        println(newEquals(newVar("a"), newVar("a")));  // a=a

        printlnRename(newEquals(newVar("a"), newVar("a")), "a", a);  // a=a
        BinaryExpression abaaba = printlnArithmeticAxiom2(a, b, a);  // a=b->a=a->b=a
        println(abaaba.right);  // a=a->b=a
        println(newEquals(b, a));  // b=a
        return newEquals(b, a);
    }

    // A1: a=b->a'=b'
    public BinaryExpression printlnArithmeticAxiom1(Expression a, Expression b) {
        Expression a1 = newImpl(newEquals(newVar("a"), newVar("b")), newEquals(newInc(newVar("a")), newInc(newVar("b"))));
        println(a1);
        a1 = printlnRename(a1, "a", a);
        a1 = printlnRename(a1, "b", b);
        return (BinaryExpression) a1;
    }

    // A2: a=b->a=c->b=c
    public BinaryExpression printlnArithmeticAxiom2(Expression a, Expression b, Expression c) {
        Expression a2 = newImpl(
                newEquals(newVar("a"), newVar("b")),
                newImpl(newEquals(newVar("a"), newVar("c")),
                        newEquals(newVar("b"), newVar("c"))));
        println(a2);
        a2 = printlnRename(a2, "a", a);
        a2 = printlnRename(a2, "b", b);
        a2 = printlnRename(a2, "c", c);
        return (BinaryExpression) a2;
    }

    // A5: a+b'=(a+b)'
    public BinaryExpression printlnArithmeticAxiom5(Expression a, Expression b) {
        Expression a5 = newEquals(newSum(newVar("a"), newInc(newVar("b"))), newInc(newSum(newVar("a"), newVar("b"))));
        println(a5);
        a5 = printlnRename(a5, "a", a);
        a5 = printlnRename(a5, "b", b);
        return (BinaryExpression) a5;
    }

    // A6: a+0=a
    public BinaryExpression printlnArithmeticAxiom6(Variable a) {
        BinaryExpression eq = newEquals(newSum(a, newVal(0)), a);
        println(eq);
        return eq;
    }

    // BinaryExpressions:

    public BinaryExpression newEquals(Expression left, Expression right) {
        return new BinaryExpression(left, right, "=");
    }

    public BinaryExpression newSum(Expression left, Expression right) {
        return new BinaryExpression(left, right, "+");
    }

    public BinaryExpression newImpl(Expression left, Expression right) {
        return new BinaryExpression(left, right, "->");
    }

    public BinaryExpression newOr(Expression left, Expression right) {
        return new BinaryExpression(left, right, "|");
    }

    //Predicates:

    public Predicate newForEach(String variable, Expression expression) {
        return new Predicate(new Variable(variable), expression, "@");
    }

    public Predicate newExists(String variable, Expression expression) {
        return new Predicate(new Variable(variable), expression, "?");
    }

    //Variable:

    public Variable newVar(String variable) {
        return new Variable(variable);
    }

    // Inc:

    public Inc newInc(Expression expression) {
        return new Inc(expression);
    }

    public Not newNot(Expression expression) {
        return new Not(expression);
    }

    public Expression newVal(int value) {
        Expression res = new Variable("0");
        for (int i = 1; i <= value; i++) {
            res = new Inc(res);
        }
        return res;
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Print N:");
        int n = new Scanner(System.in).nextInt();
        new Generator(new PrintStream("output.txt")).printWholeProof(n);
        System.out.println("Proof in output.txt!");
    }

}
