package org.goodev.material.model;

/**
 * Created by yfcheng on 2015/12/2.
 */
public class Upvote {
    public int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Upvote{" +
                "count=" + count +
                '}';
    }
}
