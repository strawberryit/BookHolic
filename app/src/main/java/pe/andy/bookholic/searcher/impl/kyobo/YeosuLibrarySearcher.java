package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.searcher.impl.yes24.GyeongjuLibrarySearchTask;

public class YeosuLibrarySearcher extends KyoboLibrarySearchTask {

	@Getter final String libraryCode = "20460";
	static final String libraryName = "여수시립도서관";
	static final String baseUrl = "http://yslibeb.yeosu.go.kr/";
	
	public YeosuLibrarySearcher(MainActivity activity) {
		super(activity, libraryName, baseUrl);
	}

	@Override
	public LibrarySearchTask create() {
		SoftReference<LibrarySearchTask> ref = new SoftReference<>(new YeosuLibrarySearcher(this.mActivity));
		return ref.get();
	}

}
