package pe.andy.bookholic;

import android.webkit.JavascriptInterface;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutionException;

import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.model.SearchResponse;
import pe.andy.bookholic.searcher.impl.SeoulLibrarySearchTask;

public class AndroidBridge {

    @JavascriptInterface
    public String getResult() {
        return "Hello";
    }

    @JavascriptInterface
    public String search(final String keyword, final String field, final int page) throws ExecutionException, InterruptedException {

        SearchField sField = SearchField.valueOf(field);
        SearchQuery query = SearchQuery.builder()
                .keyword(keyword)
                .field(sField)
                .page(page)
                .build();

        SeoulLibrarySearchTask t = new SeoulLibrarySearchTask(query);
        t.execute();
        List<Ebook> books = t.get();

        SearchResponse r = SearchResponse.builder()
                .libraryName(t.getLibraryName())
                .list(books)
                .hasNext(false)
                .build();

        Gson gson = new Gson();
        return gson.toJson(r);
    }
}
