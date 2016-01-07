package com.polarnick.mathlogic.recursive.utils;

import com.polarnick.mathlogic.recursive.base.AbstractRecursiveFunction;

import java.util.*;

/**
 * Date: 08.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class FunctionsProfiler {

    private final Map<String, ProfileEntry> entries = new HashMap<>();
    private final long loggingPeriod;
    private long lastLoggingTime = 0;

    public FunctionsProfiler(long loggingPeriod) {
        this.loggingPeriod = loggingPeriod;
    }

    public AbstractRecursiveFunction profile(String key, AbstractRecursiveFunction function) {
        return new ProfiledFunction(key, function);
    }

    private void registerTime(String key, long time) {
        if (!entries.containsKey(key)) {
            entries.put(key, new ProfileEntry(key));
        }
        entries.put(key, entries.get(key).register(time));

        long currentTime = System.currentTimeMillis();
        if (loggingPeriod != -1 && currentTime - lastLoggingTime >= loggingPeriod) {
            printEntries();
            lastLoggingTime = currentTime;
        }
    }

    private void printEntries() {
        ArrayList<ProfileEntry> sorted = new ArrayList<>(entries.values());
        Collections.sort(sorted, (e1, e2) -> {
            if (e1.sumMillis > e2.sumMillis) {
                return 1;
            } else if (e1.sumMillis < e2.sumMillis) {
                return -1;
            } else {
                return 0;
            }
        });
        System.out.println("_____________________________________\nProfiler:");
        for (ProfileEntry entry:sorted) {
            System.out.println(entry.key + ":\t" + entry.number + " times\t" + entry.sumMillis + " ms");
        }
    }

    private class ProfileEntry {

        final String key;
        final int number;
        final long sumMillis;

        public ProfileEntry(String key, int number, long sumMillis) {
            this.key = key;
            this.number = number;
            this.sumMillis = sumMillis;
        }

        public ProfileEntry(String key) {
            this(key, 0, 0);
        }

        private ProfileEntry register(long time) {
            return new ProfileEntry(this.key, this.number + 1, this.sumMillis + time);
        }
    }

    private class ProfiledFunction extends AbstractRecursiveFunction {

        private final String key;
        private final AbstractRecursiveFunction function;

        public ProfiledFunction(String key, AbstractRecursiveFunction function) {
            this.key = key;
            this.function = function;
        }

        @Override
        public int execute(int... args) {
            long start = System.currentTimeMillis();
            int result = this.function.execute(args);
            long time = System.currentTimeMillis() - start;
            registerTime(this.key, time);
            return result;
        }

    }

}
