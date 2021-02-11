package pe.andy.bookholic.model

enum class SearchField {
    ALL, TITLE, AUTHOR, PUBLISHER;

    enum class ZeroIndexSearchField(val value: Int) {
        ALL(0),
        TITLE(1),
        AUTHOR(2),
        PUBLISHER(3);

        companion object {

            fun getValue(f: SearchField): Int {
                return valueOf(f.toString()).value
            }
        }
    }

    enum class YeouiDigitalLibrarySearchField(val value: String) {
        ALL("title"),
        TITLE("title"),
        AUTHOR("author"),
        PUBLISHER("pub_name");

        companion object {

            fun getValue(f: SearchField): String {
                return valueOf(f.toString()).value
            }
        }
    }

    enum class Yes24LibrarySearchField(val value: String) {
        ALL("title"),
        TITLE("title"),
        AUTHOR("author"),
        PUBLISHER("pub_name");

        companion object {

            fun getValue(f: SearchField): String {
                return valueOf(f.toString()).value
            }
        }
    }

    enum class EpyrusLibrarySearchField(val value: String) {
        ALL("integ"),
        TITLE("title"),
        AUTHOR("author"),
        PUBLISHER("publisher");

        companion object {

            fun getValue(f: SearchField): String {
                return valueOf(f.toString()).value
            }
        }
    }

}
