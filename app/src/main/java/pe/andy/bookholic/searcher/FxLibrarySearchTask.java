package pe.andy.bookholic.searcher;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
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
import pe.andy.bookholic.util.JsonParser;
import pe.andy.bookholic.util.TextSlicer;

public abstract class FxLibrarySearchTask extends LibrarySearchTask {

    public FxLibrarySearchTask(MainActivity activity, String libraryName, String baseUrl) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    protected Integer getField(SearchQuery query) {
        return SearchField.ZeroIndexSearchField.Companion.getValue(query.getField());
    }

    @Override
    protected Response request(SearchQuery query) throws IOException {

        String url = this.baseUrl + "/FxLibrary/product/list";
        String keyword = query.getKeyword();

        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("keyoption2", this.getField(query).toString())
                .addQueryParameter("keyword", keyword)
                .addQueryParameter("page", query.getPageString())
                .addQueryParameter("itemCount", "20")
                .addQueryParameter("searchoption", "1")
                .addQueryParameter("searchType", "search");

        Request req = new Request.Builder()
                .url(builder.build().toString())
                .addHeader("Accept", "text/html; charset=utf-8")
                .addHeader("User-agent", userAgent)
                .build();

        Response resp = new OkHttpClient()
                .newCall(req)
                .execute();

        return resp;
    }

    @Override
    protected List<Ebook> parse(String html) {

        Document doc = Jsoup.parse(html);
        Elements elems = doc.select("#container > h2 > span > em");

        this.resultCount = JsonParser.parseOnlyInt(
            RegExUtils.replaceAll(elems.text(), ".*\\(|,|권\\).*", "")
        );

        elems = doc.select(".paging a.last");
        this.resultPageCount = JsonParser.parseOnlyInt(
            RegExUtils.replaceAll(elems.attr("href"), ".*,'|'\\).*", "")
        );

        if (this.resultCount == 0) {
            return Collections.emptyList();
        }
        else {
            elems = doc.select("#detail_list .item");
            return elems.stream()
                    .map(this.elementParser)
                    .collect(Collectors.toList());
        }
    }

    private Function<Element, Ebook> elementParser = li -> {
        Ebook ebook = new Ebook(this.libraryName);

        // Thumbnail
        ebook.setThumbnailUrl(JsonParser.getAttrOfFirstElement(li, ".thumb img", "src"));

        // Title, Url
        Element elem = li.select(".subject a").first();
        ebook.setTitle(elem.text())
                .setUrl(this.baseUrl + elem.attr("href"));

        Elements items = li.select(".info .i1 li");

        String author = JsonParser.getTextOf(items, 0);
        author = RegExUtils.replaceAll(author, " 저$", "");

        String publisher = JsonParser.getTextOf(items, 1);
        String date = JsonParser.getTextOf(items, 2);
        String platform = JsonParser.getTextOf(items, 3);
        platform = StringUtils.trimToEmpty(
                RegExUtils.replaceAll(platform, "(공급 : | \\(.+?\\)|네트웍스| 전자책)", "")
        );

        ebook.setAuthor(author)
                .setPublisher(publisher)
                .setDate(date)
                .setPlatform(platform);


        items = li.select(".info .i2 li");
        String text = JsonParser.getTextOf(items, 0);

        // text: "대출 0/1"
        TextSlicer slicer = new TextSlicer(text, " |/");
        slicer.pop();
        int countRent = JsonParser.parseOnlyInt(slicer.pop());
        int countTotal = JsonParser.parseOnlyInt(slicer.pop());

        ebook.setCountRent(countRent);
        ebook.setCountTotal(countTotal);

        return ebook;
    };
}
