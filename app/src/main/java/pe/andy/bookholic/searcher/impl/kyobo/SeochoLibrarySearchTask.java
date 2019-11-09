package pe.andy.bookholic.searcher.impl.kyobo;

import org.jsoup.nodes.Document;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.util.JsonParser;

public class SeochoLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "SeochoLibrary";
    static final String libraryName = "서초구 전자도서관";
    static final String baseUrl = "http://ebook.seocholib.or.kr";

    public SeochoLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
        this.setEncoding(Encoding_UTF8);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new SeochoLibrarySearchTask(this.mActivity));
        return ref.get();
    }

    @Override
    protected void parseMetaCount(Document doc) {
        String text = JsonParser.getTextOfFirstElement(doc, "p.result")
                .replaceAll(".*\\(", "");
        this.resultCount = JsonParser.parseOnlyInt(text);

        text = doc.select(".total_count").text();
        this.resultPageCount = JsonParser.parseOnlyInt(text);
    }
}
