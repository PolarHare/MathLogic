package com.polarnick.mathlogic.recursive.utils;

/**
 * @author Polyarnyi Nickolay, PolarNick239
 */
public final class Common {

    public static int[] merge(int[] xs, int[] ys) {
        int[] res = new int[xs.length + ys.length];
        System.arraycopy(xs, 0, res, 0, xs.length);
        System.arraycopy(ys, 0, res, xs.length, ys.length);
        return res;
    }

    public static int[] merge(int[] xs, int y) {
        return merge(xs, new int[]{y});
    }

    public static int[] merge(int[] xs, int[] ys, int[] zs) {
        return merge(merge(xs, ys), zs);
    }

    public static int[] merge(int[] xs, int y, int[] zs) {
        return merge(merge(xs, y), zs);
    }

    public static int[] merge(int[] xs, int y, int z) {
        return merge(merge(xs, y), z);
    }

    public static int[] subArray(int[] xs, int from, int to) {
        int[] res = new int[to - from];
        System.arraycopy(xs, from + 0, res, 0, res.length);
        return res;
    }

}
