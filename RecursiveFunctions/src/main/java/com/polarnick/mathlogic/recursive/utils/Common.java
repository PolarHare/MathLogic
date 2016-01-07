package com.polarnick.mathlogic.recursive.utils;

/**
 * @author Polyarnyi Nickolay, PolarNick239
 */
public final class Common {

    public static long[] merge(long[] xs, long[] ys) {
        long[] res = new long[xs.length + ys.length];
        System.arraycopy(xs, 0, res, 0, xs.length);
        System.arraycopy(ys, 0, res, xs.length, ys.length);
        return res;
    }

    public static long[] merge(long[] xs, long y) {
        return merge(xs, new long[]{y});
    }

    public static long[] merge(long[] xs, long[] ys, long[] zs) {
        return merge(merge(xs, ys), zs);
    }

    public static long[] merge(long[] xs, long y, long[] zs) {
        return merge(merge(xs, y), zs);
    }

    public static long[] merge(long[] xs, long y, long z) {
        return merge(merge(xs, y), z);
    }

    public static long[] subArray(long[] xs, int from, int to) {
        long[] res = new long[to - from];
        System.arraycopy(xs, from + 0, res, 0, res.length);
        return res;
    }

}
