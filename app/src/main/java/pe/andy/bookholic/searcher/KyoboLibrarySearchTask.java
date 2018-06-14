package pe.andy.bookholic.searcher;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.model.SortBy;
import pe.andy.bookholic.util.EncodeUtil;
import pe.andy.bookholic.util.JsonParser;
import pe.andy.bookholic.util.Slicer;

public abstract class KyoboLibrarySearchTask extends LibrarySearchTask {
	
	public KyoboLibrarySearchTask(String libraryName, String baseUrl, SearchQuery query) {
		super(libraryName, baseUrl, query);
		this.setEncoding(Encoding_EUCKR);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Integer getField(SearchQuery query) {
		return SearchField.ZeroIndexSearchField.getValue(query.getField());
	}
	
	String getSortBy(SearchQuery query) {
		return SortBy.KyoboLibrarySortBy.getValue(query.getSortBy());
	}

	@Override
	protected Response request(SearchQuery query) throws IOException {

		String url = baseUrl + "/Kyobo_T3/Content/Content_Search.asp";
		String keyword = query.getKeyword();
		int page = query.getPage() != null ? query.getPage().intValue() : 1;

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		urlBuilder.addQueryParameter("search_product_cd", "001");
		urlBuilder.addQueryParameter("content_all", "Y");
		urlBuilder.addQueryParameter("search_type", Integer.toString(this.getField(query)));
		urlBuilder.addQueryParameter("order_key", this.getSortBy(query));
		urlBuilder.addQueryParameter("now_page", Integer.toString(query.getPage()));
		urlBuilder.addEncodedQueryParameter("search_keyword", EncodeUtil.toEuckr(query.getKeyword()));

		OkHttpClient client = new OkHttpClient();

		Request req = new Request.Builder()
				.url(urlBuilder.build().toString())
				.addHeader("accept", "text/html; charset=euc-kr")
				.addHeader("user-agent", userAgent)
				.build();

		Response resp = client.newCall(req).execute();
		return resp;
	}

	@Override
	protected List<Ebook> parse(String html) throws IOException {
		Document doc = Jsoup.parse(html);
		
		this.resultCount = JsonParser.parseOnlyInt(doc.select(".list_sorting span.list_result strong").text());
		this.resultPageCount = JsonParser.parseOnlyInt(doc.select(".list_sorting div.page_move form.form_pagemove span.total_count").text());
		 
		Elements elems = doc.select("div#list_books > ul > li");
		List<Ebook> ebooks = elems.stream()
			.map( e -> {
				Ebook ebook = new Ebook(this.libraryName);
				
				Element elem = e.select("p.pic a").first();
				ebook.setUrl(elem.attr("href"));
				ebook.setThumbnailUrl(this.baseUrl + JsonParser.getAttrOfFirstElement(elem, "img", "src"));
				
				elem = e.select("dl > dt").first();
				ebook.setPlatform(JsonParser.getAttrOfFirstElement(elem, "span.ico img", "alt"));
				ebook.setTitle(JsonParser.getTextOfFirstElement(elem, "a"));

				String textAuthorPublisherDate = StringUtils.replacePattern(e.select("dl > dd > em").first().text(), "(\\[|\\])", "");
				Slicer slicer = new Slicer(textAuthorPublisherDate, "/").trim();
				ebook.setAuthor(	slicer.pop());
				ebook.setPublisher(	slicer.pop());
				ebook.setDate(		slicer.pop());

				String textCounts = JsonParser.getTextOfFirstElement(e, "div.service ul li.loan span.num");
				slicer = new Slicer(textCounts, "/").trim();
				ebook.setCountRent(JsonParser.parseOnlyInt(slicer.pop()));
				ebook.setCountTotal(JsonParser.parseOnlyInt(slicer.pop()));

				return ebook;
			})
			.collect(Collectors.toList());

		return ebooks;
	}

}
