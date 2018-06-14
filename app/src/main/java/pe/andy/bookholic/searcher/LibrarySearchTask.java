package pe.andy.bookholic.searcher;

import android.os.AsyncTask;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Response;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchQuery;

public abstract class LibrarySearchTask extends AsyncTask<Void, Void, List<Ebook>> {

    public static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

    @Getter
    protected String libraryName;
    protected String baseUrl;
    SearchQuery query;

    static final Charset Encoding_UTF8 = Charset.forName("UTF-8");
    static final Charset Encoding_EUCKR = Charset.forName("EUC-KR");

    @Getter @Setter
    Charset encoding = Encoding_UTF8;

    public LibrarySearchTask(String libraryName, String baseUrl, SearchQuery query) {
        this.libraryName = libraryName;
        this.baseUrl = baseUrl;
        this.query = query;
    }

    @Getter @Setter protected int resultCount = -1;
    @Getter @Setter protected int resultPageCount = -1;

    @Getter Response response;
    @Getter boolean isError = false;

    @Override
    protected List<Ebook> doInBackground(Void... voids) {
        try {
            response = request(this.query);
            String text = this.getResponseText();
            List<Ebook> list = parse(text);
            return list;

        } catch (IOException e) {
            e.printStackTrace();
            this.isError = true;
        }

        cancel(true);
        return null;
    }

    private String getResponseText() throws IOException {
        if (encoding.compareTo(Encoding_UTF8) == 0){
            return response.body().string();
        } else {
            return new BufferedReader(new InputStreamReader(response.body().byteStream(), encoding))
                    .lines().collect(Collectors.joining("\n"));
        }
    }

    protected abstract <T> T getField(SearchQuery query);
    protected abstract Response request(SearchQuery query) throws IOException;
    protected abstract List<Ebook> parse(String resp) throws IOException;

    String getFirstElementText(Element e, String css){
        return StringUtils.trimToEmpty(
                e.select(css)
                        .first()
                        .text()
        );
    }

    String getFirstElementAttr(Element e, String css, String attr){
        return StringUtils.trimToEmpty(
                e.select(css)
                        .first()
                        .attr(attr)
        );
    }

    int parseCount(String text){
        try {
            return Integer.parseInt(
                    StringUtils.replaceAll(text, "\\D*", ""));
        } catch (NumberFormatException e){
            return -1;
        }
    }

    public abstract String getLibraryCode();
}
