package pe.andy.bookholic.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class SearchQuery {

	@Getter @Setter String keyword;
	@Getter @Setter SearchField field;
	@Getter @Setter Integer page;
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
}
