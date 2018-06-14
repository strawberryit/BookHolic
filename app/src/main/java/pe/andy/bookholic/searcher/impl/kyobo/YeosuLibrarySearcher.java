package pe.andy.bookholic.searcher.impl.kyobo;

import lombok.Getter;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;

public class YeosuLibrarySearcher extends KyoboLibrarySearchTask {

	@Getter final String libraryCode = "20460";
	static final String libraryName = "여수시립도서관";
	static final String baseUrl = "http://yslibeb.yeosu.go.kr/";
	
	public YeosuLibrarySearcher(SearchQuery query) {
		super(libraryName, baseUrl, query);
	}

}
