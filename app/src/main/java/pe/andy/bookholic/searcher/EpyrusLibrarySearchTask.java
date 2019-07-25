package pe.andy.bookholic.searcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import pe.andy.bookholic.util.EncodeUtil;
import pe.andy.bookholic.util.JsonParser;

public abstract class EpyrusLibrarySearchTask extends LibrarySearchTask {

    @Getter
    final String libraryCode = "";
    String libraryName;
    String baseUrl;

    public EpyrusLibrarySearchTask(MainActivity activity, String libraryName, String baseUrl) {
        super(activity, libraryName, baseUrl);

        this.libraryName = libraryName;
        this.baseUrl = baseUrl;

        this.setEncoding(Encoding_EUCKR);
    }

    @Override
    protected String getField(SearchQuery query) {
        return SearchField.EpyrusLibrarySearchField.getValue(query.getField());
    }

    @Override
    protected Response request(SearchQuery query) throws IOException {

        String url = this.baseUrl + "/book/searchlist.asp";
        String keyword = EncodeUtil.toEuckr(query.getKeyword());
        String page = query.getPage() != null ? query.getPage().toString() : "1";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("SearchOption", this.getField(query))
                .addQueryParameter("pagenum", page)
                .addEncodedQueryParameter("SearchWord", keyword);

        Request req = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("accept", "text/html; charset=euc-kr")
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
        Elements elems = doc.select("td.contents > table");

        if (elems.isEmpty())
            return new ArrayList<>();

        this.resultCount = JsonParser.parseOnlyInt(
                elems.get(1).select("tr > td").get(1).select("div").get(1).select("div").first().text()
        );

        List<Ebook> ebooks = elems.stream()
                .skip(2)
                .map( e -> {
                    Elements cells = e.select("tr td");
                    if (cells.size() == 1)
                        return null;

                    Ebook ebook = new Ebook(this.libraryName);
                    ebook.setUrl(this.baseUrl + JsonParser.getAttrOfFirstElement(cells.get(0), "a", "href"));
                    ebook.setThumbnailUrl(JsonParser.getAttrOfFirstElement(cells.get(0), "a img", "src"));

                    Element elem = cells.get(1).select("div.listbooktitle").first();
                    ebook.setPlatform(elem.select("div").not(".listbooktitle").first().text());
                    ebook.setTitle(JsonParser.getTextOfFirstElement(elem, "a"));

                    Elements info = cells.get(1).select("div.listtextr");
                    ebook.setAuthor(	info.get(0).text());
                    ebook.setPublisher(	info.get(2).text());
                    ebook.setDate(		info.get(4).text());

                    ebook.setCountRent(JsonParser.parseOnlyInt(info.get(5).text()));
                    ebook.setCountTotal(JsonParser.parseOnlyInt(info.get(7).text()));

                    return ebook;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ebooks;
    }

}
