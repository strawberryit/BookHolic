package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension
import pe.andy.bookholic.util.TextSlicer

object Yes24TypeAParser: LibraryParser, StringExtension {

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {
        val elems = doc.select("div.sub_main_total")

        // meta info
        val count = elems.textOfFirst("h2 strong")
                .toIntOnly(-1)

        val page = elems.textOfFirst("h2 span.normal")
                .replace(".*-".toRegex(), "")
                .toIntOnly()

        return Pair(count, page)
    }

    override fun parse(element: Element, library: Library): Ebook {
        return Ebook(library.name).apply {
            // Book URL
            val href = element.attrOfFirst("li.list_thumb > a", "href")
            url = "${library.url}/${href}"

            // Thumbnail
            val src = element.attrOfFirst("li.list_thumb > a > img", "src")
            thumbnailUrl = "${library.url}/${src}"

            // Platform
            platform = "YES24"
            val info: Elements = element.select("li.list_info > dl").select("dt, dd")
            info.elementAtOrNull(0)
                    ?.attrOfFirst("img", "src")
                    ?.let {
                        platform = when {
                            it.contains("BC") -> "북큐브"
                            it.contains("KYOBO") -> "교보문고"
                            else -> "YES24"
                        }
                    }

            val hasPlatformIcon = info.first()?.hasClass("comcode") ?: false
            if (hasPlatformIcon) {
                info.removeAt(0)
            }

            title = info.textOfFirst("a")

            val text = info.htmlAt(1).replace("\n", "")
            TextSlicer(text, regexp = """\|""").apply {
                author = pop()
                publisher = pop()
                date = pop().replace("""</.*>""".toRegex(), "")
            }

            // Counts
            TextSlicer(info.last()?.text() ?: "", regexp = ",").apply {
                countRent = pop().toIntOnly(-1)
                pop()
                countTotal = pop().toIntOnly(-1)
            }
        }
    }
}