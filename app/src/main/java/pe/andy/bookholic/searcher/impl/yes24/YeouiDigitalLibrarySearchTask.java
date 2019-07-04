package pe.andy.bookholic.searcher.impl.yes24;


import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.searcher.Yes24LibrarySearchTask;
import pe.andy.bookholic.searcher.impl.GangdongLibrarySearchTask;
import pe.andy.bookholic.util.JsonParser;

@Deprecated
public class YeouiDigitalLibrarySearchTask extends Yes24LibrarySearchTask {

    @Getter
    final String libraryCode = "";
    static final String libraryName = "여의도 전자책도서관";
    static final String baseUrl = "http://ebook.yulib.or.kr/";

    public YeouiDigitalLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new YeouiDigitalLibrarySearchTask(this.mActivity));
        return ref.get();
    }

    @Override
    public Function<Element, Ebook> getEbookMapper() {
        Function<Element, Ebook> ebookMapper = (e) -> {
            Ebook ebook = new Ebook(libraryName);

            ebook.setUrl(			baseUrl + JsonParser.getAttrOfFirstElement(e, "li.list_thumb > a", "href"));
            ebook.setThumbnailUrl(	baseUrl + JsonParser.getAttrOfFirstElement(e, "li.list_thumb > a > img", "src"));

            Elements info = e.select("li.list_info > dl").select("dt, dd");
            ebook.setPlatform(		StringUtils.replacePattern(JsonParser.getAttrOfFirstElement(info.get(0), "img", "src"),
                    "(.*_|\\.gif)", ""));
            ebook.setTitle(			JsonParser.getTextOfFirstElement(info.get(1), "a"));


            String[] slices = StringUtils.split(info.get(2).text(), "|");
            Queue<String> queue = new LinkedList<>(Arrays.asList(slices));

            ebook.setAuthor(	StringUtils.trimToEmpty(queue.poll()));
            ebook.setPublisher(	StringUtils.trimToEmpty(queue.poll()));
            ebook.setDate(		StringUtils.trimToEmpty(queue.poll()));

            slices = StringUtils.split(info.last().text(), ",");
            ebook.setCountRent(JsonParser.parseOnlyInt(slices[0]));
            ebook.setCountTotal(JsonParser.parseOnlyInt(slices[2]));

            return ebook;
        };
        return ebookMapper;
    }
}
