package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension
import pe.andy.bookholic.util.TextSlicer

object KyoboSubscriptionParser: LibraryParser, StringExtension {

    override fun parse(element: Element, library: Library): Ebook {
        return Ebook(library.name).apply {
            thumbnailUrl = element.selectFirst(".img img")
                    ?.attr("src") ?: ""

            // add scheme
            if (thumbnailUrl.startsWith("//")) {
                val scheme = library.url.replace("""//.*""".toRegex(), "")
                thumbnailUrl = "${scheme}${thumbnailUrl}"
            }

            platform = element.selectFirst(".store")?.text() ?: ""

            title = element.selectFirst(".tit")?.text() ?: ""
            url = parseUrl(element, library)

            element.selectFirst(".writer")?.html()?.also {
                val text = it.replace("<span>", "|")
                        .replace("</span>", "|")

                TextSlicer(text, """\|""").apply {
                    author = pop()
                    publisher = pop()
                    date = pop()
                }
            }

            // 대출: 0/10
            element.textOfFirst("p.use span strong")
                .also {
                    TextSlicer(it, "/").apply {
                        countRent = pop().toIntOnly(-1)
                        countTotal = pop().toIntOnly(-1)
                    }
                }

            platform = "교보문고"
        }
    }

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {

        val count = doc.select(".book_resultTxt strong")
                .getOrNull(1)
                ?.text()
                ?.toIntOnly() ?: 0

        val page = when {
            count <= 0 -> 0
            else -> (count - 1) / 20 + 1
        }

        return Pair(count, page)
    }

    private fun parseUrl(element: Element, library: Library): String {
        /**
         * HTML 구조
         * <li class="tit">
         *     <a href="/content/contentView.ink"
         *        title="[책 제목] | [도서관 이름]"
         *        onclick="javascript:searchList.fnContentClick(
         *            this,            (target)
         *            '001',           (cttsDvsnCode) (필수)
         *            '4801165215744', (brcd) (필수)
         *            '2411',          (ctgrId)
         *            '1112178903',    (sntnAuthCode)
         *            'N',             (adltYN)
         *            '5'              (spenDvsnCode)
         *        ); event.preventDefault();">
         *        [책 제목]
         *     </a>
         * </li>
         */
        val onclick = element.selectFirst(".tit a")?.attr("onclick") ?: ""
        val slices = onclick.replace(""".*fnContentClick\(|\).*|'|\s""".toRegex(), "")
            .split(",")

        val cttsDvsnCode = slices[1]
        val brcd = slices[2]

        val baseUrl = listOf(
                library.url,
                library.path,
                "/content/contentView.ink"
            )
            .filter { it.isNotEmpty() }
            .joinToString("")

        return "${baseUrl}?cttsDvsnCode=${cttsDvsnCode}&brcd=${brcd}"
    }
}