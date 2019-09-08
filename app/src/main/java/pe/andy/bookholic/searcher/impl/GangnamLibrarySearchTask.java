package pe.andy.bookholic.searcher.impl;

import org.apache.commons.lang3.RegExUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.util.JsonParser;
import pe.andy.bookholic.util.SslTrust;
import pe.andy.bookholic.util.Str;

public class GangnamLibrarySearchTask extends LibrarySearchTask {

    @Getter final String libraryCode = "";
    static final String libraryName = "강남구 전자도서관";
    static final String baseUrl = "http://ebook.gangnam.go.kr";

    public GangnamLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);

        this.setEncoding(Encoding_EUCKR);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new GangnamLibrarySearchTask(this.mActivity));
        return ref.get();
    }

    @Override
    protected String getField(SearchQuery query) {
        Map<SearchField, String> fieldMap = new HashMap<>();
        fieldMap.put(SearchField.ALL, "도서명");
        fieldMap.put(SearchField.TITLE, "도서명");
        fieldMap.put(SearchField.AUTHOR, "저자명");
        fieldMap.put(SearchField.PUBLISHER, "출판사명");

        String field = fieldMap.get(query.getField());
        return field;
    }

    @Override
    protected Response request(SearchQuery query) throws IOException {

        String url = this.baseUrl + "/books/book_info.asp";
        String keyword = query.getEncodedKeyword("EUC-KR");

        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("page_num", query.getPageString())
                .addQueryParameter("list_num", "20")
                .addQueryParameter("ldav", "off")
                .addEncodedQueryParameter("bsc1", this.getEuckrString("도서검색"))
                .addEncodedQueryParameter("bsc2", this.getEuckrString(this.getField(query)))
                .addEncodedQueryParameter("sw", keyword);

        Request req = new Request.Builder()
                .url(builder.build().toString())
                .addHeader("Accept", "text/html; charset=euc-kr")
                .addHeader("User-Agent", userAgent)
                .build();

        OkHttpClient client = SslTrust.trustAllSslClient(new OkHttpClient());
        Response resp = client
                .newCall(req)
                .execute();

        return resp;
    }

    private String getEuckrString(String text) {
        try {
            return URLEncoder.encode(text, "EUC-KR");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Override
    protected List<Ebook> parse(String html) throws IOException {
        Document doc = Jsoup.parse(html);
        Elements wrapper = doc.select(".book_list");

        // 전체 수량
        Elements elems = wrapper.select("p.location");
        elems.select("font").remove();
        this.resultCount = Str.extractInt(elems.text());

        // 페이지 수
        elems = wrapper.select(".paginate a");
        Element elem = elems.last();
        if (elem != null) {
            this.resultPageCount = Str.extractInt(elem.attr("href"));
        }
        else {
            this.resultPageCount = 1;
        }

        elems = wrapper.select("ul li .book");
        List<Ebook> list = elems.stream()
                .map(this.elementParser)
                .collect(Collectors.toList());
        return list;
    }

    private Function<Element, Ebook> elementParser = li -> {
        Ebook ebook = new Ebook(this.libraryName);

        Element elem = li.select("p.book_cover a").first();
        ebook.setUrl("https://ebook.gangnam.go.kr/books/" + elem.attr("href"));
        ebook.setThumbnailUrl(JsonParser.getAttrOfFirstElement(elem, "img", "src"));

        if (li.select("p.application span").size() > 1){
            elem = li.select("p.application span").get(1);
            ebook.setPlatform(elem.text());
        } else {
            ebook.setPlatform("");
        }

        ebook.setTitle(JsonParser.getTextOfFirstElement(li, "h1.title a"));
        ebook.setAuthor(JsonParser.getTextOfFirstElement(li, "h2.writer"));
        ebook.setPublisher(JsonParser.getTextOfFirstElement(li, "h3.publisher"));
        ebook.setDate(RegExUtils.replaceAll(JsonParser.getTextOfFirstElement(li, "h3.date"), "출판일", ""));

        Elements spans = li.select("p.state span.number");
        Queue<String> queue = new LinkedList<>(spans.eachText());
        ebook.setCountTotal(Str.extractInt(Str.def(queue.poll())));
        ebook.setCountRent(Str.extractInt(Str.def(queue.poll())));

        return ebook;
    };
}
