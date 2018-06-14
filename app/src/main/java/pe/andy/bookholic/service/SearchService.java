package pe.andy.bookholic.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.searcher.impl.SeoulLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.YeosuLibrarySearcher;

public class SearchService {

    static int timeout = 10;

    public List<Ebook> search(final SearchQuery query) {

        List<LibrarySearchTask> tasks = Arrays.asList(
                //new SeoulLibrarySearchTask(query),
                new YeosuLibrarySearcher(query)
                );

        List<Ebook> books = tasks.stream()
                .parallel()
                .map(t -> {
                    try {
                        t.execute();
                        List<Ebook> found = t.get(timeout, TimeUnit.SECONDS);
                        return found;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(x -> x != null)
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());

        return books;
    }

}
