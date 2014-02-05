package com.polarnick.mathlogic.proofer;

import com.google.common.collect.Lists;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.Checker;
import com.polarnick.mathlogic.proofChecker.entities.*;
import com.polarnick.mathlogic.proofChecker.parsers.ExpressionParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 10.01.14 at 20:48
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Proofer {

    /**
     * ⊢ (α → β) → (¬β → ¬α)
     */
    private List<Expression> lemma4_4(Expression alpha, Expression beta) {
        Checker checker = new Checker();
        Expression NB = new Negation(beta);
        Expression NA = new Negation(alpha);
        Expression AB = new Consecution(alpha, beta);
        Expression ANB = new Consecution(alpha, NB);
        Expression ANB_NA = new Consecution(ANB, NA);
        Expression NB_ANB = new Consecution(NB, ANB);

        List<Expression> steps = Lists.newArrayList(
                new Consecution(AB, ANB_NA),
                AB,
                ANB_NA,
                NB_ANB,
                NB,
                ANB,
                NA
        );

        List<Expression> result = checker.totalyDeduct(
                Lists.newArrayList(AB, new Negation(beta)),
                new Negation(alpha),
                steps);
        return result;
    }

    /**
     * ⊢ ¬(α ∨ ¬α) → ¬α
     */
    private List<Expression> lemma4_5(Expression alpha) {
        Expression NA = new Negation(alpha);
        Expression NNA = new Negation(NA);
        Expression AorNA = new Disjunction(alpha, NA);
        Expression A_AorNA = new Consecution(alpha, AorNA);
        Expression N_AorNA = new Negation(AorNA);
        Expression NN_AorNA = new Negation(N_AorNA);
        Expression NA_AorNA = new Consecution(NA, AorNA);
        Expression N_AorNA__NA = new Consecution(N_AorNA, NA);
        Expression N_AorNA__NNA = new Consecution(N_AorNA, NNA);
        Expression N_AorNA__NNA______NN_AorNA = new Consecution(N_AorNA__NNA, NN_AorNA);

        List<Expression> result = new ArrayList<Expression>();

        result.add(A_AorNA);
        result.addAll(lemma4_4(alpha, AorNA));
        result.add(N_AorNA__NA);

        result.add(NA_AorNA);
        result.addAll(lemma4_4(NA, AorNA));
        result.add(N_AorNA__NNA);

        result.add(new Consecution(N_AorNA__NA, N_AorNA__NNA______NN_AorNA));
        result.add(N_AorNA__NNA______NN_AorNA);
        result.add(NN_AorNA);
        result.add(new Consecution(NN_AorNA, AorNA));
        result.add(AorNA);
        return result;
    }

    /**
     * Пусть справедливо Γ, ρ ⊢ α и Γ, ¬ρ ⊢ α. Тогда
     * также справедливо Γ ⊢ α.
     */
    private Checker.ProofWithAssumptions lemma4_6(Checker.ProofWithAssumptions ifP,
                                                  Checker.ProofWithAssumptions ifNP) {
        List<Expression> steps = new ArrayList<Expression>();
        Checker checker = new Checker();
        Checker.ProofWithAssumptions fromPtoA = checker.useDeductionConvertion(ifP);
        checker = new Checker();
        Checker.ProofWithAssumptions fromNPtoA = checker.useDeductionConvertion(ifNP);

        Expression P = ifP.alphaAssum;
        Expression A = ifP.toBeProofed;

        Expression NP = new Negation(P);
        Expression NP_A = new Consecution(NP, A);
        Expression PorNP = new Disjunction(P, NP);
        Expression PorNP_A = new Consecution(PorNP, A);
        Expression NP_A____PorNP_A = new Consecution(NP_A, PorNP_A);

        steps.addAll(fromPtoA.steps);
        steps.addAll(fromNPtoA.steps);
        steps.addAll(lemma4_5(P));
        steps.add(new Consecution(new Consecution(P, A), NP_A____PorNP_A));
        steps.add(NP_A____PorNP_A);
        steps.add(PorNP_A);
        steps.add(A);

        List<Expression> assumptions = new ArrayList<Expression>();
        for (int i = 0; i < ifP.assumptions.size() - 1; i++) {
            assumptions.add(ifP.assumptions.get(i));
        }
        Expression alpha = null;
        if (ifP.assumptions.size() >= 1) {
            alpha = ifP.assumptions.get(ifP.assumptions.size() - 1);
        }
        return new Checker.ProofWithAssumptions(assumptions, alpha, A, steps);
    }

    private Checker.ProofWithAssumptions getProof(int varIndex, Expression exp, List<String> varNames, Map<String, Boolean> varValues) {
        if (varIndex < varNames.size()) {
            varValues.put(varNames.get(varIndex), false);
            Checker.ProofWithAssumptions proofF = getProof(varIndex + 1, exp, varNames, varValues);
            varValues.put(varNames.get(varIndex), true);
            Checker.ProofWithAssumptions proofT = getProof(varIndex + 1, exp, varNames, varValues);
            return lemma4_6(proofT, proofF);
        } else {
            List<Expression> assums = new ArrayList<Expression>();
            for (int i = 0; i < varNames.size() - 1; i++) {
                String name = varNames.get(i);
                if (varValues.get(name)) {
                    assums.add(new Variable(name));
                } else {
                    assums.add(new Negation(new Variable(name)));
                }
            }
            Expression alphaAsum = null;
            String name = varNames.get(varValues.size() - 1);
            if (varValues.get(name)) {
                alphaAsum = new Variable(name);
            } else {
                alphaAsum = new Negation(new Variable(name));
            }
            List<Expression> steps = new ArrayList<Expression>();
            addSteps(exp, varValues, steps);
            return new Checker.ProofWithAssumptions(assums, alphaAsum, exp, steps);
        }
    }

    private void addSteps(Expression exp, Map<String, Boolean> varValues, List<Expression> steps) {
        exp.addSteps(varValues, steps);
    }

    private String checkProof(BufferedReader in, PrintWriter out) throws IOException {
        ExpressionParser parser = new ExpressionParser();
        String curLine = in.readLine();
        Expression exp = parser.parseExpression(curLine);
        List<String> varNames = parser.getAllVariables(curLine);
        boolean[] cur = new boolean[varNames.size()];
        Map<String, Boolean> failValues = null;
        do {
            Map<String, Boolean> values = new HashMap<String, Boolean>();
            for (int i = 0; i < varNames.size(); i++) {
                values.put(varNames.get(i), cur[i]);
            }
            if (!exp.evaluate(values)) {
                failValues = values;
                break;
            }

        } while (next(cur));
        if (failValues != null) {
            StringBuilder verdict = new StringBuilder("Expression is false at ");
            for (int i = 0; i < varNames.size(); i++) {
                verdict.append(varNames.get(i) + "=" + failValues.get(varNames.get(i)));
                if (i != varNames.size() - 1) {
                    verdict.append(',');
                }
            }
            out.println(verdict.toString());
            return verdict.toString();
        }
        HashMap<String, Boolean> varValues = new HashMap<String, Boolean>();
        for (String name : varNames) {
            varValues.put(name, false);
        }
        Checker.ProofWithAssumptions proof = getProof(0, exp, varNames, varValues);
        for (Expression step : proof.steps) {
            out.println(step.toString());
        }
        return "proofed";
    }

    private boolean next(boolean[] cur) {
        for (int i = cur.length - 1; i >= 0; i--) {
            if (!cur[i]) {
                cur[i] = true;
                for (int j = i + 1; j < cur.length; j++) {
                    cur[j] = false;
                }
                return true;
            }
        }
        return false;
    }

    private static final String DEFAULT_INPUT_FILE = "toProof.txt";
    private static final String END_OF_PROOF_PREFIX = "//END_OF_PROOF";

    public static void main(String[] args) throws IOException {
        String filename;
        if (args.length == 0) {
            filename = DEFAULT_INPUT_FILE;
        } else if (args.length == 1) {
            filename = args[0];
        } else {
            System.out.println("Usage: [inputFile], or no arguments - than default file will be readed - " + DEFAULT_INPUT_FILE);
            return;
        }
        BufferedReader in = new BufferedReader(new FileReader(filename));
        PrintWriter out = new PrintWriter("resultProof.txt");
        int proofNumber = 1;
        while (in.ready()) {
            Proofer proofer = new Proofer();
            String verdict = proofer.checkProof(in, out);
            out.println(END_OF_PROOF_PREFIX);
            out.println();
            System.out.println("Expression #" + proofNumber + ": " + verdict);
            proofNumber++;
        }
        out.close();
    }

}
