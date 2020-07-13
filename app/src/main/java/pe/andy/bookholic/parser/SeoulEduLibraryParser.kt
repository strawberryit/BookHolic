package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension
import java.util.*


object SeoulEduLibraryParser: LibraryParser, StringExtension {

    override fun parse(element: Element, library: Library): Ebook {
        return Ebook(library.name).apply {

            // Thumbnail URL
            val src: String = element.select(".thumb img").attr("src")
            thumbnailUrl = when {
                src.startsWith("http") -> src
                else -> "${library.url}/${src}"
            }

            // URL
            val href: String = element.attrOfFirst(".thumb a", attr = "href")
            url = "${library.url}/${href}"

            // Title
            element.select(".list-body a").not(".btn").first()?.let {
                title = it.text().trim()
            }

            element.select(".list-body .info-elib span").let {
                author = it.textAt(0)
                publisher = it.textAt(2)
                date = it.textAt(4)
                        .trim()
                        .replace(" .*".toRegex(), "")
            }

            element.select(".list-body .meta span").let {
                platform = when {
                    it.size > 7 -> it.textAt(6)
                    it.size > 9 && (! it.textAt(8).startsWith("대출")) ->
                        "${it.textAt(6)} ${it.textAt(8)}"
                    else -> ""
                }.toUpperCase(Locale.getDefault())
            }
        }
    }

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {

        val count = doc.select(".sub001 strong")
                .text()
                .toIntOnly(-1)

        val page = doc.select(".dataTables_paginate .paginate_button")
                .last()
                ?.attr("href")
                ?.toIntOnly(-1)
                ?: -1

        return Pair(count, page)
    }
}