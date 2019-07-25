package pe.andy.bookholic.searcher.impl.yes24;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.SoftReference;
import java.util.function.Function;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.searcher.Yes24LibrarySearchTask;
import pe.andy.bookholic.util.JsonParser;

public class UljuLibrarySearchTask extends Yes24LibrarySearchTask {

    @Getter final String libraryCode = "";
    static final String libraryName = "울주통합도서관";
    static final String baseUrl = "http://ebook.uljulib.or.kr/";

    public UljuLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new UljuLibrarySearchTask(this.mActivity));
        return ref.get();
    }

    @Override
    public Function<Element, Ebook> getEbookMapper() {
        return (e) -> {
            Ebook ebook = new Ebook(libraryName);

            ebook.setUrl(			baseUrl + JsonParser.getAttrOfFirstElement(e, "li.list_thumb > a", "href"));
            ebook.setThumbnailUrl(	baseUrl + JsonParser.getAttrOfFirstElement(e, "li.list_thumb > a > img", "src"));

            Elements info = e.select("li.list_info > dl").select("dt, dd");
            ebook.setPlatform(		RegExUtils.replacePattern(JsonParser.getAttrOfFirstElement(info.get(0), "img", "src"),
                    "(.*_|\\.gif)", ""));
            ebook.setTitle(			JsonParser.getTextOfFirstElement(info.get(0), "a"));


            String[] slices = StringUtils.split(info.get(1).text(), "|");
            ebook.setAuthor(	StringUtils.trimToEmpty(slices[0]));
            ebook.setPublisher(	StringUtils.trimToEmpty(slices[1]));
            ebook.setDate(		StringUtils.trimToEmpty(slices[2]));

            slices = StringUtils.split(info.last().text(), ",");
            ebook.setCountRent(JsonParser.parseOnlyInt(slices[0]));
            ebook.setCountTotal(JsonParser.parseOnlyInt(slices[2]));

            return ebook;
        };
    }
}
