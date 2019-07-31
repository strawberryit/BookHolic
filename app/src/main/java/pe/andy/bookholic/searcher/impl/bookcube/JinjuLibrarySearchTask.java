package pe.andy.bookholic.searcher.impl.bookcube;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.FxLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class JinjuLibrarySearchTask extends FxLibrarySearchTask {

    @Getter String libraryCode = "JinjuLibrary";
    static final String libraryName = "진주시립도서관";
    static final String baseUrl = "http://125.135.250.131:8080";

    public JinjuLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new JinjuLibrarySearchTask(this.mActivity));
        return ref.get();
    }
}
