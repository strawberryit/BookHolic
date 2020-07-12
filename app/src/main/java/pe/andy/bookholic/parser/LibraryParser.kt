package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library

interface LibraryParser {

    fun Document.textOfFirst(cssQuery: String): String {
        return selectFirst(cssQuery)
                ?.text()
                ?.trim()
                ?: ""
    }

    fun Element.textOfFirst(cssQuery: String, excludeQuery: String = ""): String {
        var elements: Elements = select(cssQuery)
        if (excludeQuery.isNotBlank()) {
            elements = elements.not(excludeQuery)
        }
        return elements.first()
                ?.text()
                ?.trim()
                ?: ""
    }

    fun Element.attrOfFirst(cssQuery: String, attr: String): String {
        return selectFirst(cssQuery)
                ?.attr(attr)
                ?.trim()
                ?: ""
    }

    fun Elements.textOfFirst(cssQuery: String): String {
        return select(cssQuery)
                .first()
                ?.text()
                ?.trim()
                ?: ""
    }

    fun Elements.attrOfFirst(cssQuery: String, attr: String): String {
        return select(cssQuery)
                .first()
                ?.attr(attr)
                ?.trim()
                ?: ""
    }

    fun Elements.textAt(index: Int): String {
        return elementAtOrNull(index)
                ?.text()
                ?: ""
    }

    fun parse(element: Element, library: Library): Ebook

    fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int>
}