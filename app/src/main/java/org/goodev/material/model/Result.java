package org.goodev.material.model;

import java.util.List;

/**
 * Created by yfcheng on 2015/12/15.
 */
public class Result {
    public List<Hit> hits;
    public long nbHits;
    public int page;
    public int nbPages;
    public int hitsPerPage;
    public int processingTimeMS;
    public boolean exhaustiveFacetsCount;
    public String query;
    public String params;
    public String index;

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }

    public long getNbHits() {
        return nbHits;
    }

    public void setNbHits(long nbHits) {
        this.nbHits = nbHits;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getNbPages() {
        return nbPages;
    }

    public void setNbPages(int nbPages) {
        this.nbPages = nbPages;
    }

    public int getHitsPerPage() {
        return hitsPerPage;
    }

    public void setHitsPerPage(int hitsPerPage) {
        this.hitsPerPage = hitsPerPage;
    }

    public int getProcessingTimeMS() {
        return processingTimeMS;
    }

    public void setProcessingTimeMS(int processingTimeMS) {
        this.processingTimeMS = processingTimeMS;
    }

    public boolean isExhaustiveFacetsCount() {
        return exhaustiveFacetsCount;
    }

    public void setExhaustiveFacetsCount(boolean exhaustiveFacetsCount) {
        this.exhaustiveFacetsCount = exhaustiveFacetsCount;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Result{" +
                "hits=" + hits.size() +
                ", nbHits=" + nbHits +
                ", page=" + page +
                ", nbPages=" + nbPages +
                ", hitsPerPage=" + hitsPerPage +
                ", processingTimeMS=" + processingTimeMS +
                ", exhaustiveFacetsCount=" + exhaustiveFacetsCount +
                ", query='" + query + '\'' +
                ", params='" + params + '\'' +
                ", index='" + index + '\'' +
                '}';
    }
}
