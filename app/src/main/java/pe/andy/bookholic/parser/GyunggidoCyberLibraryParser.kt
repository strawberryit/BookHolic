package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension
import pe.andy.bookholic.util.TextSlicer


object GyunggidoCyberLibraryParser: LibraryParser, StringExtension {

    override fun parse(element: Element, library: Library): Ebook {
        return Ebook(library.name).apply {

            // Thumbnail
            thumbnailUrl = element.attrOfFirst("div.thumb img", attr = "src")

            val wrapper: Elements = element.select(".bookDataWrap")

            // Platform
            platform = wrapper.textOfFirst(".ebookSupport span")

            // Url
            val bookId = wrapper.attrOfFirst("strong.tit a", attr = "href")
                    .replace("""\D*""".toRegex(), "")
            url = "${library.url}/cyber/ebook/ebookDetail.do?bookId=${bookId}"

            // Title
            title = wrapper.textOfFirst("strong.tit .searchKwd")

            // Metadata
            val infos = wrapper.select(".sdot-list li")
            author = infos.textAt(0)
                    .replace(".*: ".toRegex(), "")

            publisher = infos.textAt(1)
                    .replace(".*: ".toRegex(), "")

            date = infos.textAt(2)
                    .replace(".*: ".toRegex(), "")

            // Count
            val text: String = wrapper.select(".btnArea span").text()
            TextSlicer(text, "/").let {
                countRent = it.pop().toIntOnly(-1)
                countTotal = it.pop().toIntOnly(-1)
            }
        }
    }

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {

        val count = doc.select("p.resultCount b")
                .text()
                .toIntOnly(0)

        val page = doc.select(".btn-paging.last")
                .attr("onclick")
                .toIntOnly(-1)

        return Pair(count, page)
    }
}