package pe.andy.bookholic.searcher.impl;

import org.apache.commons.lang3.RegExUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
import pe.andy.bookholic.util.Str;
import pe.andy.bookholic.util.TextSlicer;

public class GyunggidoCyberLibrarySearchTask extends LibrarySearchTask {

    @Getter final String libraryCode = "";
    static final String libraryName = "경기도사이버도서관";
    static final String baseUrl = "http://www.library.kr";

    public GyunggidoCyberLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new GyunggidoCyberLibrarySearchTask(this.mActivity));
        return ref.get();
    }

    @Override
    protected String getField(SearchQuery query) {
        if (query.getField() == SearchField.ALL)
            return "text_idx";
        else
            return query.getField().toString();
    }

    @Override
    protected Response request(SearchQuery query) throws IOException {

        String url = baseUrl + "/cyber/ebook/ebookList.do";
        String keyword = query.getKeyword();

        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("searchKeyword", keyword)
                .addQueryParameter("searchCondition", this.getField(query))
                .addQueryParameter("currentPageNo", query.getPageString())
                .addQueryParameter("viewType", "desc")
                .addQueryParameter("recordPagePerCount", "30");

        Request req = new Request.Builder()
                .url(builder.build().toString())
                .addHeader("Content-Type", "charset=UTF-8")
                .addHeader("accept", "text/html; charset=UTF-8")
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

        Elements elem = doc.select("p.resultCount b");
        this.resultCount = Str.extractInt(elem.text());

        elem = doc.select(".btn-paging.last");
        this.resultPageCount = Str.extractInt(elem.attr("onclick"));


        Elements elems = doc.select("ul.resultList.descType > li");
        List<Ebook> ebooks = elems.stream()
                .map(this.elementParse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ebooks;
    }

    private Function<Element, Ebook> elementParse = li -> {
        Ebook ebook = new Ebook(this.libraryName);

        // Thumbnail
        String thumbnailUrl = li.select("div.thumb img").attr("src");
        ebook.setThumbnailUrl(thumbnailUrl);

        Elements wrap = li.select(".bookDataWrap");

        // Platform
        Element elem = wrap.select(".ebookSupport span").first();
        if (elem != null) {
            String platform = elem.text();
            ebook.setPlatform(platform);
        }

        // Url
        String bookId = wrap.select("strong.tit a").attr("href");
        bookId = RegExUtils.replaceAll(bookId, "\\D*", "");
        ebook.setUrl("https://www.library.kr/cyber/ebook/ebookDetail.do?bookId=" + bookId);

        // Title
        String title = wrap.select("strong.tit .searchKwd").text();
        ebook.setTitle(title);

        List<String> list = wrap.select(".sdot-list li").eachText();
        Queue<String> queue = new LinkedList<>(list);

        // Author
        ebook.setAuthor(Str.def(queue.poll())
                .replaceAll(".*: ", ""));

        // Publisher
        ebook.setPublisher(Str.def(queue.poll())
                .replaceAll(".*: ", ""));

        // Date
        ebook.setDate(Str.def(queue.poll())
                .replaceAll(".*: ", ""));

        // Count
        String counts = wrap.select(".btnArea span").text();
        TextSlicer slicer = new TextSlicer(counts, "/");
        ebook.setCountRent(Str.extractInt(slicer.pop()));
        ebook.setCountTotal(Str.extractInt(slicer.pop()));

        return ebook;
    };
}
