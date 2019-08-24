package pe.andy.bookholic.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Ebook {

	public Ebook(String libraryName) {
		this.libraryName = libraryName;
	}
	
	String seq;
	String title;
	String author;
	String publisher;
	String libraryName;
	String platform;
	String thumbnailUrl;
	String url;
	String isbn;
	public int countTotal = -1;
	public int countRent = -1;
	String date;

	String platformClass;
	boolean rentLibrary = false;

	public static final Comparator<Ebook> comparator = (book1, book2) -> {

		if (book1 == null || book2 == null || book1.equals(book2))
			return 0;

		if (book1.getCountTotal() <= 0 || book2.getCountTotal() <= 0)
			return 0;

		boolean isAvailableBook1 = ! (book1.getCountRent() == book1.getCountTotal());
		boolean isAvailableBook2 = ! (book2.getCountRent() == book2.getCountTotal());

		if (isAvailableBook1 && !isAvailableBook2)
			return -1;

		if (!isAvailableBook1 && isAvailableBook2)
			return 1;

		return 0;
	};

	public Ebook setPlatform(String platform) {
		this.platform = StringUtils.defaultString(platform);

		if (this.platform.contains("교보")) {
			platformClass = "label-success";
		}
		else if (this.platform.contains("북큐브")) {
			platformClass = "bg-orange";
		}
		else if (this.platform.contains("예스24") || this.platform.contains("YES24")) {
			platformClass = "label-primary";
		}
		else if (this.platform.contains("메키아") || this.platform.contains("MEKIA")) {
			platformClass = "bg-purple";
		}
		else if (this.platform.contains("알라딘")) {
			platformClass = "label-danger";
		}
		else {
			platformClass = "label-danger";
		}

		return this;
	}

	public boolean isRentable() {
		return this.countTotal > this.countRent;
	}

}
