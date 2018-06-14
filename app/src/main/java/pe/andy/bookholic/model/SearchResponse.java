package pe.andy.bookholic.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchResponse {

    String libraryName;
    List<Ebook> list;
    boolean hasNext;

}
