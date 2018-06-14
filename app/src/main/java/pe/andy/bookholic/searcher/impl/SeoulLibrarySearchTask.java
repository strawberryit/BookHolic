package pe.andy.bookholic.searcher.impl;

import java.io.IOException;
import java.lang.Integer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.util.JsonParser;

public class SeoulLibrarySearchTask extends LibrarySearchTask {

    @Getter
    final String libraryCode = "seoul";
    static final String libraryName = "서울시 전자도서관";
    static final String baseUrl = "http://elib.seoul.go.kr";

    public SeoulLibrarySearchTask(SearchQuery query) {
        super(libraryName, baseUrl, query);
    }

    @Override
    protected Integer getField(SearchQuery query) {
        return SearchField.ZeroIndexSearchField.getValue(query.getField());
    }

    @Override
    protected Response request(SearchQuery query) throws IOException {

        String url = baseUrl + "/ebooks/ContentsSearch.do";
        String keyword = query.getKeyword();
        int page = query.getPage() != null ? query.getPage().intValue() : 0;

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("libCode", Integer.toString(111314));
        urlBuilder.addQueryParameter("searchKeyword", keyword);
        urlBuilder.addQueryParameter("currentCount", Integer.toString(page));
        urlBuilder.addQueryParameter("searchOption", Integer.toString(getField(query)));
        urlBuilder.addQueryParameter("pageCount", Integer.toString(20));
        urlBuilder.addQueryParameter("userId", "nologin");
        urlBuilder.addQueryParameter("sType", "TT");
        urlBuilder.addQueryParameter("sortOption", Integer.toString(1));

        OkHttpClient client = new OkHttpClient();

        Request req = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("accept", "application/json;charset=UTF-8")
                .addHeader("user-agent", userAgent)
                .build();

        Response resp = client.newCall(req).execute();
        return resp;
    }


    @Override
    protected List<Ebook> parse(String json) throws IOException {

        this.resultCount = JsonParser.parseIntValue(json, "Contents", "TotalCount");
        this.resultPageCount = JsonParser.parseIntValue(json, "Contents", "TotalPage");

        List<Map<String, String>> list;
        list = JsonParser.parseJsonKVList(json, "Contents", "ContentDataList");

        List<Ebook> books = list.stream()
                .map( map -> {
                    Ebook ebook = new Ebook(libraryName);

                    ebook.setSeq(map.get("ContentKey"));
                    ebook.setTitle(map.get("ContentTitle"));
                    ebook.setAuthor(map.get("ContentAuthor"));
                    ebook.setThumbnailUrl(map.get("ContentCoverUrlS"));
                    ebook.setDate(map.get("ContentPubDate"));

                    ebook.setPlatform(map.get("OwnerCodeDesc"));
                    ebook.setUrl("http://elib.seoul.go.kr/ebooks/detail.do?no=" + map.get("ContentKey"));

                    return ebook;
                })
                .collect(Collectors.toList());

        return books;
    }
}
