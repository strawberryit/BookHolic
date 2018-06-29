package pe.andy.bookholic.model;

public enum SearchField {
	ALL, TITLE, AUTHOR, PUBLISHER;
	
	public static enum ZeroIndexSearchField{
		ALL(0), TITLE(1), AUTHOR(2), PUBLISHER(3);
		
		int value;
		private ZeroIndexSearchField(int value) {
			this.value = value;
		}

		public static Integer getValue(SearchField f){
			return valueOf(f.toString()).value;
		}
		
	}

	public static enum YeouiDigitalLibrarySearchField{
		ALL("title"), TITLE("title"), AUTHOR("author"), PUBLISHER("pub_name");
		
		String value;
		private YeouiDigitalLibrarySearchField(String value) {
			this.value = value;
		}

		public static String getValue(SearchField f){
			return valueOf(f.toString()).value;
		}
		
	}

	public static enum UijeongbuLibrarySearchField{
		ALL("all"), TITLE("bookname"), AUTHOR("bookauthor"), PUBLISHER("bookpubname");

		String value;
		private UijeongbuLibrarySearchField(String value) {
			this.value = value;
		}

		public static String getValue(SearchField f){
			return valueOf(f.toString()).value;
		}

	}

	public static enum Yes24LibrarySearchField{
		ALL("title"), TITLE("title"), AUTHOR("author"), PUBLISHER("pub_name");
		
		String value;
		private Yes24LibrarySearchField(String value) {
			this.value = value;
		}

		public static String getValue(SearchField f){
			return valueOf(f.toString()).value;
		}
		
	}
	
	public static enum EpyrusLibrarySearchField {
		ALL("integ"), TITLE("title"), AUTHOR("author"), PUBLISHER("publisher");
		
		String value;
		private EpyrusLibrarySearchField(String value) {
			this.value = value;
		}

		public static String getValue(SearchField f){
			return valueOf(f.toString()).value;
		}
		
	}

}
