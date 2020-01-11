package pe.andy.bookholic.searcher;

import org.apache.commons.lang3.RegExUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.model.SortBy;
import pe.andy.bookholic.util.EncodingUtil;
import pe.andy.bookholic.util.JsonParser;
import pe.andy.bookholic.util.TextSlicer;

public abstract class Yes24TypeBLibrarySearchTask extends LibrarySearchTask {
    String libraryName;
    String baseUrl;

    public Yes24TypeBLibrarySearchTask(MainActivity activity, String libraryName, String baseUrl) {
        super(activity, libraryName, baseUrl);

        this.libraryName = libraryName;
        this.baseUrl = baseUrl;

        this.setEncoding(Encoding_EUCKR);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String getField(SearchQuery query) {
        return SearchField.Yes24LibrarySearchField.Companion.getValue(query.getField());
    }

    String getSortBy(SearchQuery query) {
        return SortBy.Yes24LibrarySortBy.Companion.getValue(query.getSortBy());
    }

    @Override
    protected Response request(SearchQuery query) throws IOException {

        String url = this.baseUrl + "/search/";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("page_num", query.getPageString())
                .addQueryParameter("srch_order", "total")
                .addEncodedQueryParameter("src_key", EncodingUtil.toEuckr(query.getKeyword()));

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

    protected Function<Element, Ebook> getEbookMapper() {
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

            // text: 보유 1, 대출 0, 예약 0, ...
            String text = JsonParser.getTextOfFirstElement(e, ".info > .rentinfo");
            TextSlicer slicer = new TextSlicer(text, ",");
            int totalCount = JsonParser.parseOnlyInt(slicer.pop());
            ebook.setCountTotal(totalCount);

            int rentCount = JsonParser.parseOnlyInt(slicer.pop());
            ebook.setCountRent(rentCount);

            return ebook;
        };
    }
}