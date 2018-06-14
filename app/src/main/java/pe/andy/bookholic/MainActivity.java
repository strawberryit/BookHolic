package pe.andy.bookholic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.webkit.WebView;

import java.util.Arrays;
import java.util.List;

import pe.andy.bookholic.adapter.BookAdapter;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.impl.SeoulLibrarySearchTask;
import pe.andy.bookholic.service.SearchService;

public class MainActivity extends AppCompatActivity {

    RecyclerView booklistView;
    SearchService searchService = new SearchService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        booklistView = (RecyclerView) findViewById(R.id.booklist);
        booklistView.setHasFixedSize(true);
        booklistView.setLayoutManager(new LinearLayoutManager(this));

        SearchField sField = SearchField.valueOf("TITLE");
        SearchQuery query = SearchQuery.builder()
                .keyword("과학")
                .field(sField)
                .page(1)
                .build();

        List<Ebook> books =
                //generateTestBooks();
                searchService.search(query);

        BookAdapter adapter = new BookAdapter(this, books);
        booklistView.setAdapter(adapter);

        /*
        try {
            SearchField sField = SearchField.valueOf("TITLE");
            SearchQuery query = SearchQuery.builder()
                    .keyword("과학")
                    .field(sField)
                    .page(1)
                    .build();

            SeoulLibrarySearchTask t = new SeoulLibrarySearchTask(query);
            t.execute();
            List<Ebook> books = t.get();
            books.stream()
                    .forEach(b -> {
                        Log.d("BookHolic", b.getTitle());
                    });

            BookAdapter adapter = new BookAdapter(this, books);
            booklistView.setAdapter(adapter);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        */
        /*
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new BookWebViewClient());
        webview.setWebChromeClient(new BookWebChromeClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new AndroidBridge(), "Ebook");

        WebView.setWebContentsDebuggingEnabled(true);

        webview.loadUrl("file:///android_asset/main.html");
        */
    }

    List<Ebook> generateTestBooks() {
        Ebook b1 = new Ebook("테스트 도서관")
                .setSeq("1234")
                .setTitle("인프라 엔지니어의 교과서-시스템 구축과 관리편 테스트")
                .setAuthor("기술평론사 편집부")
                .setPublisher("길벗")
                .setLibraryName("테스트 도서관")
                .setPlatform("교보문고")
                .setThumbnailUrl("http://seoullib.barob.co.kr/resources/images/Lsize/PRD000115946.jpg")
                .setUrl("http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946")
                .setCountTotal(5)
                .setCountRent(2)
                .setDate("2018-01-01");

        Ebook b2 = new Ebook("테스트 도서관")
                .setSeq("1234")
                .setTitle("인프라 엔지니어의 교과서-시스템 구축과 관리편")
                .setAuthor("기술평론사 편집부")
                .setPublisher("길벗")
                .setLibraryName("테스트 도서관")
                .setPlatform("교보문고")
                .setThumbnailUrl("http://elib.seoul.go.kr/resources/images/YES24/Lsize/8638926.jpg")
                .setUrl("http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946")
                .setCountTotal(5)
                .setCountRent(5)
                .setDate("2018-01-01");

        Ebook b3 = new Ebook("테스트 도서관")
                .setSeq("1234")
                .setTitle("인프라 엔지니어의 교과서-시스템 구축과 관리편")
                .setAuthor("기술평론사 편집부")
                .setPublisher("길벗")
                .setLibraryName("테스트 도서관")
                .setPlatform("교보문고")
                .setThumbnailUrl("http://elib.seoul.go.kr/resources/images/YES24/Lsize/7042064.jpg")
                .setUrl("http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946")
                .setCountTotal(5)
                .setCountRent(5)
                .setDate("2018-01-01");

        return Arrays.asList(b1, b2, b3, b1, b2, b3);
    }

}
