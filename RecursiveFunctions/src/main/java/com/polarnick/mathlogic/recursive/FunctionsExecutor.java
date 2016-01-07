package com.polarnick.mathlogic.recursive;

import com.polarnick.mathlogic.recursive.base.AbstractRecursiveFunction;
import com.polarnick.mathlogic.recursive.parser.RecursiveFunctionParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Date: 10.01.14 at 20:48
 *
 * @author Nickolay Polyarniy
 */
public class FunctionsExecutor {

    public static void main(String[] args) throws IOException {
        String fooFilename;
        String paramsFilename = null;
        if (args.length == 1) {
            fooFilename = args[0];
        } else if (args.length == 2) {
            fooFilename = args[0];
            paramsFilename = args[1];
        } else {
            System.out.println("Usage: inputFile [paramsFile]");
            return;
        }
        BufferedReader in = new BufferedReader(new FileReader(fooFilename));
        String input = in.readLine();
        long[] intArgs = new long[0];

        if (paramsFilename != null) {
            BufferedReader paramsIn = new BufferedReader(new FileReader(paramsFilename));
            StringTokenizer tok = new StringTokenizer(paramsIn.readLine());
            List<Long> params = new ArrayList<>();
            while (tok.hasMoreTokens()) {
                params.add(Long.parseLong(tok.nextToken()));
            }
            intArgs = new long[params.size()];
            for (int i = 0; i < params.size(); i++) {
                intArgs[i] = params.get(i);
            }
        }

        AbstractRecursiveFunction foo = new RecursiveFunctionParser(input).parse();
        long result = foo.execute(intArgs);
        System.out.println(result);
    }

}
