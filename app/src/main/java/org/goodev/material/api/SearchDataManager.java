/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.goodev.material.api;

import android.content.Context;

import org.goodev.material.model.Hit;
import org.goodev.material.util.UI;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Responsible for loading search results from dribbble and designer news. Instantiating classes are
 * responsible for providing the {code onDataLoaded} method to do something with the data.
 */
public abstract class SearchDataManager implements DataLoadingSubject {

    // state
    private String query = "";
    private boolean loadingDribbble = false;
    private int page = 0;
    private boolean more = true;
    Context context;

    @Override
    public boolean hasMore() {
        return more;
    }

    public SearchDataManager(Context context) {
        this.context = context;
    }

    public abstract void onDataLoaded(List<Hit> data);

    @Override
    public boolean isDataLoading() {
        return loadingDribbble;
    }

    public void searchFor(String query) {
        if (!this.query.equals(query)) {
            clear();
            this.query = query;
        } else {
            page++;
        }
        searchDribbble(query, page);
    }

    public void loadMore() {
        searchFor(query);
    }

    public void clear() {
        query = "";
        page = 0;
        more = true;
        loadingDribbble = false;

    }

    public int getPage() {
        return page;
    }

    public String getQuery() {
        return query;
    }


    private void searchDribbble(final String query, final int page) {
        loadingDribbble = true;
        Api.getApiService().search(Api.getSearchBody(query, page))
                .map(searchRes -> Api.getSearchResult(searchRes))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result != null) {
                        onDataLoaded(result.hits);
                    } else {
                        onDataLoaded(null);
                        more = false;
                    }
                    loadingDribbble = false;
                }, throwable -> {
                    if (context != null) {
                        UI.toastError(context, throwable);
                    }
                });

    }

}
