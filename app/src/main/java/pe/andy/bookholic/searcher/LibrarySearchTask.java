package pe.andy.bookholic.searcher;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.Response;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.service.SearchService;
import pe.andy.bookholic.ui.BookRecyclerUi;

import static pe.andy.bookholic.searcher.LibrarySearchTask.LibrarySearchStatus.DONE;
import static pe.andy.bookholic.searcher.LibrarySearchTask.LibrarySearchStatus.FAIL;
import static pe.andy.bookholic.searcher.LibrarySearchTask.LibrarySearchStatus.INITIAL;
import static pe.andy.bookholic.searcher.LibrarySearchTask.LibrarySearchStatus.PROGRESS;

@Accessors(chain = true)
public abstract class LibrarySearchTask extends AsyncTask<Void, Void, List<Ebook>> {

    protected MainActivity mActivity;

    public static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

    @Getter
    protected String libraryName;
    protected String baseUrl;
    protected SearchQuery mQuery;
    public SearchQuery getQuery() { return this.mQuery; }
    public void setQuery(SearchQuery query) { this.mQuery = query; }

    protected static final Charset Encoding_UTF8 = Charset.forName("UTF-8");
    protected static final Charset Encoding_EUCKR = Charset.forName("EUC-KR");

    @Getter @Setter
    Charset encoding = Encoding_UTF8;

    public LibrarySearchTask(MainActivity activity, String libraryName, String baseUrl) {
        this.mActivity = activity;
        this.libraryName = libraryName;
        this.baseUrl = baseUrl;
    }

    @Getter @Setter protected int resultCount = -1;
    @Getter @Setter protected int resultPageCount = -1;

    @Getter Response response;
    @Getter boolean isError = false;

    @Override
    protected void onPreExecute() {
        this.setSearchStatus(PROGRESS);
        mActivity.getLibraryRecyclerUi().refresh();
    }

    @Override
    protected List<Ebook> doInBackground(Void... voids) {
        try {
            response = request(this.mQuery);
            String text = this.getResponseText();
            if (text == null) {
                this.isError = true;
                cancel(true);
                return null;
            }

            List<Ebook> list = parse(text);
            return list;

        } catch (SocketTimeoutException e) {
            this.isError = true;
        } catch (IOException e) {
            //e.printStackTrace();
            this.isError = true;
        }

        cancel(true);
        return null;
    }

    private String getResponseText() throws IOException {
        if (encoding.compareTo(Encoding_UTF8) == 0){
            return response.body().string();
        } else {
            try {
                return new BufferedReader(new InputStreamReader(response.body().byteStream(), encoding))
                        .lines().collect(Collectors.joining("\n"));
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    protected void onPostExecute(List<Ebook> books) {

        // Update Library list
        this.setSearchStatus(DONE);
        mActivity.getLibraryRecyclerUi().refresh();

        // Update Book list
        BookRecyclerUi bookRecyclerUi = mActivity.getBookRecyclerUi();
        bookRecyclerUi.add(books);

        //Log.d("BookHolic", this.libraryName + ": hasNext - " + this.hasNext());
        if (this.hasNext()) {
            bookRecyclerUi.hideLoadProgress();
            bookRecyclerUi.showLoadMore();
        }
        else {
            SearchService service = mActivity.getSearchService();
            boolean isFinished = service.isFinished();
            boolean isAllLastPage = service.isAllLastPage();

            //Log.d("BookHolic", this.libraryName + ": isFinished - " + isFinished + ", isAllLastPage - " + isAllLastPage);

            if (isFinished && isAllLastPage) {
                bookRecyclerUi.hideLoadProgress();
                bookRecyclerUi.hideLoadMore();
            }
        }
    }

    @Override
    protected void onCancelled() {
        this.setSearchStatus(FAIL);
        mActivity.getLibraryRecyclerUi().refresh();
    }

    public boolean hasNext() {
        return this.resultCount > 0 && this.mQuery.getPage() < this.resultPageCount;
    }

    protected abstract <T> T getField(SearchQuery query);
    protected abstract Response request(SearchQuery query) throws IOException;
    protected abstract List<Ebook> parse(final String resp) throws IOException;

    public abstract String getLibraryCode();
    public abstract LibrarySearchTask create();

    @Getter @Setter LibrarySearchStatus searchStatus = INITIAL;
    public enum LibrarySearchStatus {
        INITIAL, PROGRESS, DONE, FAIL
    }

    public boolean isProgress() {
        return this.searchStatus == PROGRESS;
    }

    public boolean isTaskPending() {
        return this.getStatus() == AsyncTask.Status.PENDING;
    }
}
