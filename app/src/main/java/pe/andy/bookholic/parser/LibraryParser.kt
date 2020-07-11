package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

interface LibraryParser {

    fun Document.textOfFirst(cssQuery: String): String {
        return selectFirst(cssQuery)
                ?.text()
                ?.trim()
                ?: ""
    }

    fun Element.textOfFirst(cssQuery: String): String {
        return selectFirst(cssQuery)
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
}