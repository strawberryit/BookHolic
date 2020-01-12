package pe.andy.bookholic.model

enum class SortBy {
    TITLE, AUTHOR, RECENT;


    enum class GangdongLibrarySortBy(val value: String) {
        TITLE("title"),
        AUTHOR("title"),
        RECENT("pubdt");

        companion object {

            fun getValue(f: SearchField): String {
                return valueOf(f.toString()).value
            }
        }
    }

    enum class HanyangLibrarySortBy(val value: Int) {
        TITLE(1),
        AUTHOR(5),
        RECENT(3);

        companion object {

            fun getValue(f: SearchField): Int {
                return valueOf(f.toString()).value
            }
        }
    }

    enum class SeoulEduLibrarySortBy(val value: Int) {
        TITLE(2),
        AUTHOR(3),
        RECENT(1);

        companion object {

            fun getValue(s: SortBy): Int {
                return valueOf(s.toString()).value
            }
        }
    }

    enum class SeoulLibrarySortBy(val value: Int) {
        TITLE(1),
        AUTHOR(2),
        RECENT(3);

        companion object {

            fun getValue(s: SortBy): Int {
                return valueOf(s.toString()).value
            }
        }
    }

    enum class YeouiDigitalLibrarySortBy(val value: String) {
        TITLE("title"),
        AUTHOR("title"),
        RECENT("pubdt");

        companion object {

            fun getValue(s: SortBy): String {
                return valueOf(s.toString()).value
            }
        }
    }

    enum class Yes24LibrarySortBy(val value: String) {
        TITLE("title"),
        AUTHOR("title"),
        RECENT("pubdt");

        companion object {

            fun getValue(s: SortBy): String {
                return valueOf(s.toString()).value
            }
        }
    }

    enum class KyoboLibrarySortBy(val value: String) {
        TITLE("product_nm_kr"),
        AUTHOR("text_author_nm"),
        RECENT("pub_ymd");

        companion object {

            fun getValue(s: SortBy): String {
                return valueOf(s.toString()).value
            }
        }
    }
}
