package pe.andy.bookholic.searcher.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.util.SslTrust;
import pe.andy.bookholic.util.Str;

public class SeoulEduLibrarySearchTask extends LibrarySearchTask {

    @Getter
    final String libraryCode = "";
    static final String libraryName = "서울시교육청";
    //static final String baseUrl = "http://m.e-lib.sen.go.kr:8088";
    static final String baseUrl = "https://e-lib.sen.go.kr";

    public SeoulEduLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
        this.setEncoding(Encoding_EUCKR);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new SeoulEduLibrarySearchTask(this.mActivity));
        return ref.get();
    }

    @Override
    protected String getField(SearchQuery query) {
        if (query.getField() == SearchField.PUBLISHER)
            return "LOCATION";
        else
            return query.getField().toString();
    }

    private FastDateFormat formatter = FastDateFormat.getInstance("yyyyMMdd");

    @Override
    protected Response request(SearchQuery query) throws IOException {

        //String url = baseUrl + "/SeoulEduEbookLibraryMobileWeb/eduLib/ebook/result/ebookSearchListXmlResult.jsp";
        String url = baseUrl + "/wsearch/search_result.php";
        String keyword = query.getEucKRKeyword();

        Calendar cal = Calendar.getInstance();
        String endDate = formatter.format(cal.getTime());

        int startCount = (query.getPage() - 1) * 5;
        RequestBody formBody = new FormBody.Builder()
                .add("sort", "RANK")
                .add("collection", "ALL")
                .add("range", "A")
                .add("startDate", "19700101")
                .add("endDate", endDate)
                .add("searchField", "ALL")
                .add("reQuery", "1")
                .add("startCount", Integer.toString(startCount))
                .addEncoded("query", keyword)
                .build();

        Request req = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("accept", "text/html; charset=euc-kr")
                .addHeader("user-agent", userAgent)
                .post(formBody)
                .build();

        /*
        RequestBody formBody = new FormBody.Builder()
                .add("keyword", keyword)
                .add("searchField", getField(query))
                .add("currentCount", query.getPageString())
                .add("deviceType", "005")
                .add("serviceName", "MB_02_02_01_SERVICE")
                .add("sCollection", "ALL")
                .add("sortType", "1")
                .add("loanUseViewYn", "N")
                .add("keywordView", "N")
                .build();

        Request req = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("accept", "text/html;charset=UTF-8")
                .addHeader("user-agent", userAgent)
                .post(formBody)
                .build();
        */

        OkHttpClient client = SslTrust.trustAllSslClient(new OkHttpClient());
        Response resp = client
                .newCall(req)
                .execute();

        return resp;
    }

    @Override
    protected List<Ebook> parse(String html) throws IOException {
        Document doc = Jsoup.parse(html);

        this.resultCount = Str.extractInt(doc.select(".sub001 strong").text());

        Element lastPageElem = doc.select(".dataTables_paginate .paginate_button").last();
        if (lastPageElem == null) {
            this.resultPageCount = 1;
        }
        else {
            String href = lastPageElem.attr("href");
            this.resultPageCount = Str.extractInt(href);
        }

        if (resultCount == 0)
            return new ArrayList<>();

        Elements elems = doc.select(".elib li");
        List<Ebook> list = elems.stream()
                .map(this.elementParser)
                .collect(Collectors.toList());

        return list;
/*
        Elements elems = doc.select("div.contents_list");
        List<Ebook> list = elems.stream()
                .map( e -> {
                    Ebook ebook = new Ebook(this.libraryName);

                    ebook.setThumbnailUrl(e.select("img").first().attr("src"));
                    ebook.setTitle(     JsonParser.getTextOfFirstElement(e, ".contents_bookinfo_booktitle"));
                    ebook.setAuthor(	JsonParser.getTextOfFirstElement(e, ".contents_bookinfo_bookauthor"));
                    ebook.setPublisher(	JsonParser.getTextOfFirstElement(e, ".contents_bookinfo_bookpublisher"));
                    ebook.setPlatform(	JsonParser.getTextOfFirstElement(e, ".contents_bookinfo_comCode"));

                    try {
                        Element elem = e.select(".contents_bookinfo_state p").first();
                        String counts = elem.text();

                        String[] slices = StringUtils.split(counts, "/");
                        Queue<String> queue = new LinkedList<>(Arrays.asList(slices));

                        ebook.setCountRent(Integer.parseInt(queue.poll()));
                        ebook.setCountTotal(Integer.parseInt(queue.poll()));
                    }
                    catch (NumberFormatException exception){}
                    return ebook;
                })
                .collect(Collectors.toList());
*/
    }

    private Function<Element, Ebook> elementParser = li -> {
        Ebook ebook = new Ebook(this.libraryName);

        // Thumbnail Url
        String thumbnailUrl = li.select(".thumb img").attr("src");
        if (! StringUtils.startsWith(thumbnailUrl, "http")) {
            thumbnailUrl = baseUrl + thumbnailUrl;
        }
        ebook.setThumbnailUrl(thumbnailUrl);

        // Url
        String url = this.baseUrl + li.select(".thumb a").attr("href");
        ebook.setUrl(url);

        // Title
        Element titleElement = li.select(".list-body a").not(".btn").first();
        if (titleElement != null) {
            ebook.setTitle(titleElement.text().trim());
        }

        List<String> infos = li.select(".list-body .info-elib span").eachText();
        Queue<String> queue = new LinkedList<>(infos);
        ebook.setAuthor(queue.poll());
        queue.poll();
        ebook.setPublisher(queue.poll());
        queue.poll();
        ebook.setDate(queue.poll());

        String platform = "";
        infos = li.select(".list-body .meta span").eachText();
        if (infos.size() > 7) {
            platform = infos.get(6);
        }
        if (infos.size() > 9) {
            if (! StringUtils.startsWith(infos.get(8), "대출"))
                platform += " " + infos.get(8);
        }
        ebook.setPlatform(platform);

        // TODO 서울시교육청 전자도서관은 검색 리스트에서 도서 권수가 나오지 않는다.
        return ebook;
    };
}
