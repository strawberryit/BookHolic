package pe.andy.bookholic.model;

public enum SortBy {
	TITLE, AUTHOR, RECENT;

	
	public static enum GangdongLibrarySortBy {
		TITLE("title"), AUTHOR("title"), RECENT("pubdt");
		
		String value;
		private GangdongLibrarySortBy(String value) {
			this.value = value;
		}

		public static String getValue(SearchField f) {
			return valueOf(f.toString()).value;
		}
	}
	
	public static enum HanyangLibrarySortBy {
		TITLE(1), AUTHOR(5), RECENT(3);
		
		int value;
		private HanyangLibrarySortBy(int value) {
			this.value = value;
		}

		public static int getValue(SearchField f){
			return valueOf(f.toString()).value;
		}
	}
	

	public static enum SeoulEduLibrarySortBy{
		TITLE(2), AUTHOR(3), RECENT(1);
		
		int value;
		private SeoulEduLibrarySortBy(int value) {
			this.value = value;
		}

		public static int getValue(SortBy s){
			return valueOf(s.toString()).value;
		}
	}

	public static enum SeoulLibrarySortBy{
		TITLE(1), AUTHOR(2), RECENT(3);
		
		int value;
		private SeoulLibrarySortBy(int value) {
			this.value = value;
		}

		public static int getValue(SortBy s){
			return valueOf(s.toString()).value;
		}	
	}

	public static enum YeouiDigitalLibrarySortBy{
		TITLE("title"), AUTHOR("title"), RECENT("pubdt");
		
		String value;
		private YeouiDigitalLibrarySortBy(String value) {
			this.value = value;
		}

		public static String getValue(SortBy s){
			if (s == null)
				return TITLE.value;
			else
				return valueOf(s.toString()).value;
		}	
	}

	public static enum Yes24LibrarySortBy{
		TITLE("title"), AUTHOR("title"), RECENT("pubdt");
		
		String value;
		private Yes24LibrarySortBy(String value) {
			this.value = value;
		}

		public static String getValue(SortBy s){
			if (s == null)
				return TITLE.value;
			else
				return valueOf(s.toString()).value;
		}	
	}

	public static enum KyoboLibrarySortBy{
		TITLE("product_nm_kr"), AUTHOR("text_author_nm"), RECENT("pub_ymd");
		
		String value;
		private KyoboLibrarySortBy(String value) {
			this.value = value;
		}

		public static String getValue(SortBy s){
			if (s == null)
				return TITLE.value;
			else
				return valueOf(s.toString()).value;
		}	
	}

}
