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
        BinaryExpression aEqualI = newEquals(a, newVal(i));  // a=i

        Expression tmp = newImpl(aEqualI, newEquals(newSum(a, newVal(0)), newVal(i)));  // a=i -> a+0=i
        //       a=a+0 -> a=i -> a+0=i
        println(newImpl(newEquals(a, newSum(a, newVal(0))), tmp));

        //       a=i -> a+0=i
        Expression tmp0 = newEquals(newSum(a, newVal(0)), newVal(i));  // a+0=i
        println(newImpl(aEqualI, tmp0));

        List<Expression> steps = new ArrayList<>();
        steps.add(tmp0);
        for (int j=0;j <= n-i-1; j++) {
            Expression tmp1 = newEquals(newSum(a, newVal(j)), newVal(i + j));  // a+j=<i+j>
            Expression tmp2 = newEquals(newInc(newSum(a, newVal(j))), newVal(i + j + 1));  // (a+j)'=<i+j+1>
            Expression tmp3 = newEquals(newSum(a, newVal(j+1)), newVal(i + j + 1));  // (a+j)'=<i+j+1>
            //   a+j=(i+j) -> (a+j)'=(i+j+1)
            println(newImpl(tmp1, tmp2));
            //   (a+j)'=(i+j+1) -> a+(j+1)=(i+j+1)
            println(newImpl(tmp2, tmp3));

            steps.add(tmp2);
            steps.add(tmp3);
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

        //           a=i -> (?b(a+b=n))
        BinaryExpression res = newImpl(aEqualI, newExists("b", newEquals(newSum(a, newVar("b")), newVal(n))));
        println(res);
        return res;
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
            Expression tmp3 = newImpl(xsOrs[i], y);  // (x0 | x1 | ... | x(i+1) -> y)
            //   (x0 | x1 | ... | xi -> y) -> (x(i+1) -> y) -> (x0 | x1 | ... | x(i+1)) -> y
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


    public void println(Expression e) {
        out.println(e.toString());
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

    public Expression newVal(int value) {
        Expression res = new Variable("0");
        for (int i=1;i <= value; i++) {
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
