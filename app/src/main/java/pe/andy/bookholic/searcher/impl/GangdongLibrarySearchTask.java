package pe.andy.bookholic.searcher.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import pe.andy.bookholic.util.JsonParser;
import pe.andy.bookholic.util.Str;
import pe.andy.bookholic.util.TextSlicer;

public class GangdongLibrarySearchTask extends LibrarySearchTask {

    @Getter
    public final String libraryCode = "";
    static final String libraryName = "강동구 전자도서관";
    static final String baseUrl = "http://ebook.gdlibrary.or.kr:8090";

    public GangdongLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);

        this.setEncoding(this.Encoding_EUCKR);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new GangdongLibrarySearchTask(this.mActivity));
        return ref.get();
    }

    @Override
    protected String getField(SearchQuery query) {
        return Str.def(query.getField().toString())
                    .toLowerCase();
    }

    @Override
    protected Response request(SearchQuery query) throws IOException {

        String url = baseUrl + "/search/";
        String keyword = query.getEncodedKeyword("EUC-KR");

        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("srch_order", this.getField(query))
                .addQueryParameter("page_num", query.getPageString())
                .addQueryParameter("view", "10")
                .addEncodedQueryParameter("src_key", keyword);


        Request req = new Request.Builder()
                .url(builder.build().toString())
                .addHeader("Accept", "text/html; charset=euc-kr")
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
        String text = doc.select(".totalpage").text();
        TextSlicer slicer = new TextSlicer(text, "[\\(/]");

        // Total Count
        this.resultCount = Str.extractInt(slicer.pop());
        slicer.pop();
        this.resultPageCount = Str.extractInt(slicer.pop());

        Elements elems = doc.select("div#booklist ul.list li.line");
        List<Ebook> list = elems.stream()
                .map(this.elementParse)
                .collect(Collectors.toList());

        return list;
    }

    private Function<Element, Ebook> elementParse = li -> {
        Ebook ebook = new Ebook(this.libraryName);

        // Thumbnail
        ebook.setThumbnailUrl(
                this.baseUrl + li.select("div.cover img").attr("src")
        );

        Element elem = li.select("div.info span.book_tit a").first();
        // Title
        ebook.setTitle(elem.text());
        // Url
        ebook.setUrl(this.baseUrl + elem.attr("href"));

        elem = li.select("div.info ul.book_txt").first();
        if (elem != null) {
            // Author
            ebook.setAuthor(JsonParser.getTextOfFirstElement(elem, "li[title='저자']"));
            // Publisher
            ebook.setPublisher(JsonParser.getTextOfFirstElement(elem, "li[title='출판사']"));
            // Date
            ebook.setDate(JsonParser.getTextOfFirstElement(elem, "li[title='출간일']"));
            // Platform
            ebook.setPlatform(JsonParser.getTextOfFirstElement(elem, "li[title='공급사']"));
        }

        // Count
        String info = li.select("div.info div.rentinfo").first().text();
        TextSlicer slicer = new TextSlicer(info, ",");

        ebook.setCountTotal(Str.extractInt(slicer.pop()));
        ebook.setCountRent(Str.extractInt(slicer.pop()));

        return ebook;
    };
}
