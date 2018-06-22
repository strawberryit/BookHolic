package pe.andy.bookholic.searcher.impl;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.SoftReference;
import java.util.Queue;
import java.util.function.Function;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.searcher.Yes24LibrarySearchTask;
import pe.andy.bookholic.util.JsonParser;
import pe.andy.bookholic.util.Str;

public class GyeongsanLibrarySearchTask extends Yes24LibrarySearchTask {

    @Getter
    final String libraryCode = "gyeongsan";
    static final String libraryName = "경산시립도서관";
    static final String baseUrl = "http://elib.gbgs.go.kr";

    public GyeongsanLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new GyeongsanLibrarySearchTask(this.mActivity));
        return ref.get();
    }

    @Override
    public Function<Element, Ebook> getEbookMapper() {
        return ebookMapper;
    }

    final Function<Element, Ebook> ebookMapper = (e) -> {
        Ebook ebook = new Ebook(libraryName);

        // Book Link
        String url = JsonParser.getAttrOfFirstElement(e, "li.list_thumb > a", "href");
        if (! StringUtils.startsWith(url, "http"))
            url = baseUrl + url;
        ebook.setUrl(url);

        // Thumbnail url
        url = JsonParser.getAttrOfFirstElement(e, "li.list_thumb > a > img", "src");
        if (! StringUtils.startsWith(url, "http"))
            url = baseUrl + url;
        ebook.setThumbnailUrl(url);

        // Title
        Elements titleElement = e.select("li.list_info > dl dt");
        ebook.setTitle(titleElement.select("a").text());

        // Platform
        String platform = StringUtils.replaceAll(
                titleElement.select("img").attr("src"), ".*_|.gif", "");

        switch(platform) {
            case "KYOBO":
                platform = "교보문고"; break;
            case "CUBE":
                platform = "북큐브"; break;
            case "WOORI":
                platform = "우리전자책"; break;
        }
        ebook.setPlatform(platform);

        // Author, Publisher, Date
        Elements info = e.select("li.list_info dd.book_info");
        Queue<String> queue = Str.splitToQ(info.text(), "\\|");

        ebook.setAuthor(	StringUtils.trimToEmpty(queue.poll()));
        ebook.setPublisher(	StringUtils.trimToEmpty(queue.poll()));
        ebook.setDate(		StringUtils.trimToEmpty(queue.poll()));


        // Count
        info = e.select("li.list_info dd.condition strong");

        Element count = info.get(0);
        if (count != null)
            ebook.setCountRent(JsonParser.parseOnlyInt(count.text()));

        count = info.get(2);
        if (count != null)
            ebook.setCountTotal(JsonParser.parseOnlyInt(count.text()));

        return ebook;
    };

}
