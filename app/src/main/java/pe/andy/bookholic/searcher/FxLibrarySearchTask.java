package pe.andy.bookholic.searcher;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
import pe.andy.bookholic.util.Str;

public abstract class FxLibrarySearchTask extends LibrarySearchTask {

    public FxLibrarySearchTask(MainActivity activity, String libraryName, String baseUrl) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    protected Integer getField(SearchQuery query) {
        return SearchField.ZeroIndexSearchField.getValue(query.getField());
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
    protected List<Ebook> parse(String html) throws IOException {

        Document doc = Jsoup.parse(html);
        Elements elems = doc.select("div#detail_list > ul.book_list > li");

        if (elems.select("div.noresult").size() > 0)
            return new ArrayList<>();

        List<Ebook> list = elems.stream()
                .map(this.elementParser)
                .collect(Collectors.toList());

        return list;
    }

    private Function<Element, Ebook> elementParser = li -> {
        Ebook ebook = new Ebook(this.libraryName);

        // Thumbnail
        ebook.setThumbnailUrl(JsonParser.getAttrOfFirstElement(li, "p.thumb img", "src"));

        // Title, Url
        Element elem = li.select("ul.book_info").first();
        ebook.setTitle(JsonParser.getTextOfFirstElement(elem, "li.subject a"))
                .setUrl(this.baseUrl + JsonParser.getAttrOfFirstElement(elem, "li.subject a", "href"));

        Elements infos = elem.select("li.inner_info");

        Elements spans = infos.first().select("span");
        Queue<String> queue = new LinkedList<>(spans.eachText());
        ebook.setAuthor(StringUtils.replacePattern(queue.poll(), " 저$", ""))
                .setPublisher(Str.def(queue.poll()))
                .setDate(Str.def(queue.poll()));

        String platform = StringUtils.replacePattern(queue.poll(), "(공급 : | \\([\\d\\-]+?\\)$|\\(주\\)|네트웍스| 전자책)", "");
        ebook.setPlatform(platform);

        // Count
        String text = StringUtils.replacePattern(infos.get(1).select("span").first().text(), "대출 ", "");
        queue = Str.splitToQ(text, "/");
        ebook.setCountRent(JsonParser.parseOnlyInt(queue.poll()))
                .setCountTotal(JsonParser.parseOnlyInt(queue.poll()));

        return ebook;
    };
}
