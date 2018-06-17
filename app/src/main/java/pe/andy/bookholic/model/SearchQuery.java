package pe.andy.bookholic.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Builder
public class SearchQuery {

	@Getter @Setter String keyword;
	@Getter @Setter SearchField field;
	@Builder.Default
	@Getter	@Setter Integer page = 1;
	@Getter @Setter SortBy sortBy;
	
	public String getEncodedKeyword(){
		try {
			return URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	public String getEncodedKeyword(String encoding){
		try {
			return URLEncoder.encode(keyword, encoding);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public String getPageString() {
		return this.page != null ? Integer.toString(this.page) : "1";
	}

	public SearchQuery nextPage() {
		page += 1;
		return this;
	}
}
