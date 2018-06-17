package pe.andy.bookholic.searcher;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.model.SortBy;
import pe.andy.bookholic.util.EncodeUtil;
import pe.andy.bookholic.util.JsonParser;

public abstract class Yes24LibrarySearchTask extends LibrarySearchTask {
    String libraryName;
    String baseUrl;

    Function<Element, Ebook> ebookMapper;

    public Yes24LibrarySearchTask(MainActivity activity, String libraryName, String baseUrl) {
        super(activity, libraryName, baseUrl);

        this.libraryName = libraryName;
        this.baseUrl = baseUrl;

        this.setEncoding(Encoding_EUCKR);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String getField(SearchQuery query) {
        return SearchField.Yes24LibrarySearchField.getValue(query.getField());
    }

    String getSortBy(SearchQuery query) {
        return SortBy.Yes24LibrarySortBy.getValue(query.getSortBy());
    }

    @Override
    protected Response request(SearchQuery query) throws IOException {

        String url = this.baseUrl + "/ebook/search_list.asp";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("page_num", query.getPageString())
                .addQueryParameter("keyoption2", this.getField(query))
                .addQueryParameter("sort", this.getSortBy(query))
                .addEncodedQueryParameter("keyword", EncodeUtil.toEuckr(query.getKeyword()));

        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("accept", "text/html; charset=euc-kr")
                .addHeader("user-agent", userAgent)
                .build();

        Response resp = client.newCall(req).execute();
        return resp;
    }

    @Override
    protected List<Ebook> parse(String html) throws IOException {
        Document doc = Jsoup.parse(html);
        Elements elems = doc.select("div.sub_main_total");

        // meta info
        Element h2 = elems.select("h2").first();
        this.resultCount = JsonParser.parseOnlyIntFrom(h2, "strong");
        this.resultPageCount = JsonParser.parseOnlyIntFrom(h2, "span.normal", "(.*-| /)");

        // no result
        if (this.resultCount == 0)
            return new ArrayList<>();

        // content
        elems = elems.select("ul");

        List<Ebook> ebooks = elems.stream()
                .map(getEbookMapper())
                .collect(Collectors.toList());

        return ebooks;
    }

    public abstract Function<Element, Ebook> getEbookMapper();
}