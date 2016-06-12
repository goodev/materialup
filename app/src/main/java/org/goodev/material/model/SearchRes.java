package org.goodev.material.model;

import java.util.List;

/**
 * Created by yfcheng on 2015/12/15.
 */
public class SearchRes {
    public List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "SearchRes{" +
                "results=" + results.size() +
                '}';
    }
}
