package pe.andy.bookholic.searcher.impl.yes24;

import org.apache.commons.lang3.RegExUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
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
import pe.andy.bookholic.searcher.Yes24LibrarySearchTask;
import pe.andy.bookholic.util.EncodeUtil;
import pe.andy.bookholic.util.JsonParser;
import pe.andy.bookholic.util.Slicer;

public class UljuLibrarySearchTask extends Yes24LibrarySearchTask {

    @Getter final String libraryCode = "";
    static final String libraryName = "울주통합도서관";
    static final String baseUrl = "http://uljuebook.ulju.ulsan.kr";

    public UljuLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new UljuLibrarySearchTask(this.mActivity));
        return ref.get();
    }

    @Override
    protected Response request(SearchQuery query) throws IOException {

        String url = this.baseUrl + "/search/";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("page_num", query.getPageString())
                .addQueryParameter("srch_order", "total")
                .addEncodedQueryParameter("src_key", EncodeUtil.toEuckr(query.getKeyword()));

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

        // meta info
        this.resultCount = 0;
        Element elem = doc.select("#list_tab em").first();
        if (elem != null) {
            this.resultCount = JsonParser.parseOnlyInt(elem.text());
        }

        String text = doc.select("#list_tab").text();
        text = RegExUtils.replaceAll(text, ".*\\/| 페이지.*", "");
        this.resultPageCount = JsonParser.parseOnlyInt(text);

        // no result
        if (this.resultCount == 0)
            return Collections.emptyList();

        // content
        Elements elems = doc.select("#booklist .line");

        List<Ebook> ebooks = elems.stream()
                .map(getEbookMapper())
                .collect(Collectors.toList());

        return ebooks;
    }

    @Override
    public Function<Element, Ebook> getEbookMapper() {
        return (e) -> {
            Ebook ebook = new Ebook(libraryName);

            ebook.setUrl(			baseUrl + JsonParser.getAttrOfFirstElement(e, ".thumb2 > a", "href"));
            ebook.setThumbnailUrl(	baseUrl + JsonParser.getAttrOfFirstElement(e, ".thumb2 > a > img", "src"));

            String title = e.select(".info > .book_tit > a").text();
            ebook.setTitle(title);

            Elements info = e.select(".info > .book_txt li");

            if (info.size() > 3) {
                String author = info.get(0).text();
                ebook.setAuthor(author);

                String publisher = info.get(1).text();
                ebook.setPublisher(publisher);

                String date = info.get(2).text();
                ebook.setDate(date);

                String platform = info.get(3).text();
                ebook.setPlatform(platform);
            }

            String txt = e.select(".info > .rentinfo").text();
            Slicer slicer = new Slicer(txt, ",").trim();

            int totalCount = JsonParser.parseOnlyInt(slicer.pop());
            ebook.setCountTotal(totalCount);

            int rentCount = JsonParser.parseOnlyInt(slicer.pop());
            ebook.setCountRent(rentCount);

            return ebook;
        };
    }
}
