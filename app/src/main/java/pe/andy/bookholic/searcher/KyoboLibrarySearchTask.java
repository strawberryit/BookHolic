package pe.andy.bookholic.searcher;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.model.SortBy;
import pe.andy.bookholic.util.EncodingUtil;
import pe.andy.bookholic.util.JsonParser;
import pe.andy.bookholic.util.TextSlicer;

public abstract class KyoboLibrarySearchTask extends LibrarySearchTask {

	public KyoboLibrarySearchTask(MainActivity activity, String libraryName, String baseUrl) {
		super(activity, libraryName, baseUrl);
		this.setEncoding(Encoding_EUCKR);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Integer getField(SearchQuery query) {
		return SearchField.ZeroIndexSearchField.Companion.getValue(query.getField());
	}
	
	String getSortBy(SearchQuery query) {
		return SortBy.KyoboLibrarySortBy.Companion.getValue(query.getSortBy());
	}

	@Override
	protected Response request(SearchQuery query) throws IOException {

		String url = baseUrl + "/Kyobo_T3/Content/Content_Search.asp";
		String keyword = query.getKeyword();
		if (this.encoding == Encoding_EUCKR) {
			//keyword = EncodeUtil.toEuckr(query.getKeyword());
			keyword = EncodingUtil.toEuckr(query.getKeyword());
		}
		int page = query.getPage() != null ? query.getPage().intValue() : 1;

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		urlBuilder.addQueryParameter("search_product_cd", "001");
		urlBuilder.addQueryParameter("content_all", "Y");
		urlBuilder.addQueryParameter("search_type", Integer.toString(this.getField(query)));
		urlBuilder.addQueryParameter("order_key", this.getSortBy(query));
		urlBuilder.addQueryParameter("now_page", Integer.toString(query.getPage()));
		urlBuilder.addQueryParameter("layout", Integer.toString(2));
		urlBuilder.addEncodedQueryParameter("search_keyword", keyword);

		String accept = this.encoding == Encoding_UTF8 ? "text/html; charset=utf-8" : "text/html; charset=euc-kr";
		OkHttpClient client = new OkHttpClient();

		Request req = new Request.Builder()
				.url(urlBuilder.build().toString())
				.addHeader("accept", accept)
				.addHeader("user-agent", userAgent)
				.build();

		Response resp = client.newCall(req).execute();
		return resp;
	}

	@Override
	protected List<Ebook> parse(String html) throws IOException {
		Document doc = Jsoup.parse(html);

		this.parseMetaCount(doc);

		Elements elems = doc.select("div#list_books > ul > li");
		List<Ebook> ebooks = elems.stream()
			.map( e -> {
				Ebook ebook = new Ebook(this.libraryName);
				
				Element elem = e.select("p.pic a").first();
				ebook.setUrl(elem.attr("href"));

				String tUrl = JsonParser.getAttrOfFirstElement(elem, "img", "src");
				if (! StringUtils.startsWith(tUrl, "http")) {
					tUrl = this.baseUrl + "/" + tUrl;
				}
				ebook.setThumbnailUrl(tUrl);
				
				elem = e.select("dl > dt").first();
				String platform = JsonParser.getAttrOfFirstElement(elem, "span.ico img", "alt");
				if (platform.isEmpty()) {
					platform = JsonParser.getAttrOfFirstElement(elem, "span.ico img", "src");
					platform = platform.replaceAll(".*_|\\.png", "");
				}
				ebook.setPlatform(platform);
				ebook.setTitle(JsonParser.getTextOfFirstElement(elem, "a"));

				// textAuthorPublisherDate: "   Author / [ Publisher / 2019-01-01 ]   "
				String textAuthorPublisherDate =
						JsonParser.getTextOfFirstElement(e, "dl > dd > em")
							.replaceAll("[\\[\\]]", "");

				TextSlicer slicer = new TextSlicer(textAuthorPublisherDate, "/");
				ebook.setAuthor(	slicer.pop());
				ebook.setPublisher(	slicer.pop());
				ebook.setDate(		slicer.pop());

				// textCounts: 0/10
				String textCounts =
						JsonParser.getTextOfFirstElement(e, "div.service ul li.loan span.num");
				slicer = new TextSlicer(textCounts, "/");
				ebook.setCountRent(JsonParser.parseOnlyInt(slicer.pop()));
				ebook.setCountTotal(JsonParser.parseOnlyInt(slicer.pop()));

				return ebook;
			})
			.collect(Collectors.toList());

		return ebooks;
	}

	protected void parseMetaCount (Document doc) {
		this.resultCount = JsonParser.parseOnlyInt(doc.select(".list_sorting span.list_result strong").text());
		this.resultPageCount = JsonParser.parseOnlyInt(doc.select(".list_sorting span.total_count").text());
	}
}
