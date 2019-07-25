package pe.andy.bookholic.searcher.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.util.Str;

public class UijeongbuLibrarySearchTask extends LibrarySearchTask {

    @Getter
    public final String libraryCode = "";
    static final String libraryName = "의정부 전자도서관";
    static final String baseUrl = "http://ebook.uilib.go.kr:8082";

    public UijeongbuLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new UijeongbuLibrarySearchTask(this.mActivity));
        return ref.get();
    }

    @Override
    protected String getField(SearchQuery query) {
        return Str.def(query.getField().toString()).toLowerCase();
    }

    @Override
    protected Response request(SearchQuery query) throws IOException {

        String url = baseUrl + "/books/search.php";
        String keyword = query.getKeyword();

        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("page", query.getPageString())
                .addQueryParameter("wrd", keyword);


        Request req = new Request.Builder()
                .url(builder.build().toString())
                .addHeader("Accept", "text/html; charset=UTF-8")
                .addHeader("user-agent", userAgent)
                .build();

        Response resp = new OkHttpClient()
                .newCall(req)
                .execute();

        return resp;
    }

    @Override
    protected List<Ebook> parse(String html) throws IOException {
        Document doc = Jsoup.parse(html);

        // .totalpage
        this.resultCount = Str.extractInt(doc.select(".sub001 .strong").text());

        // Total Count
        this.resultPageCount = (this.resultCount - 1) / 5 + 1;


        Elements elems = doc.select(".sub22ListMain");
        List<Ebook> list = elems.stream()
                .map(this.elementParse)
                .collect(Collectors.toList());

        return list;
    }

    private Function<Element, Ebook> elementParse = item -> {
        Ebook ebook = new Ebook(this.libraryName);

        // Thumbnail
        ebook.setThumbnailUrl(
                this.baseUrl + item.select(".bookList img").attr("src")
        );

        Element elem = item.select(".daList01 a").first();
        if (elem != null) {
            // Title
            ebook.setTitle(elem.text());

            // Url
            ebook.setUrl(this.baseUrl + elem.attr("href"));
        }


        elem = item.select(".daList01").first();
        if (elem != null) {
            List<TextNode> nodes = elem.textNodes();

            // Author
            if (nodes.size() > 0)
                ebook.setAuthor(nodes.get(0).text());

            // Publisher
            if (nodes.size() > 1)
                ebook.setPublisher(nodes.get(1).text());

            // Date
            if (nodes.size() > 2)
                ebook.setDate(nodes.get(2).text());
        }

        Elements elems = item.select(".list_tb li");

        // Platform
        elem = elems.get(0);
        if (elem != null) {
            ebook.setPlatform(elem.select(".sec").text());
        }

        // Count
        elem = elems.get(1);
        if (elem != null) {
            ebook.setCountTotal(Str.extractInt(elem.select(".sec").text()));
        }

        elem = elems.get(2);
        if (elem != null) {
            ebook.setCountRent(Str.extractInt(elem.select(".sec").text()));
        }

        elems = null;
        elem = null;
        return ebook;
    };
}
