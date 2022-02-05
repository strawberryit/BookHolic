package pe.andy.bookholic.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import pe.andy.bookholic.databinding.BookItemBinding
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.util.BookColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class BookAdapter : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    var books = mutableListOf<Ebook>()

    lateinit var bookColor: BookColor

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {

        val binding = BookItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)

        bookColor = BookColor(parent.context)

        return BookViewHolder(binding)
    }

    override fun getItemCount(): Int
            = this.books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = this.books[position]
        holder.bind(book)
    }

    fun add(list: List<Ebook>) {
        books.addAll(list)
        notifyDataSetChanged()
    }

    fun set(list: List<Ebook>) {
        books.clear()
        books.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        books.clear()
        notifyDataSetChanged()
    }

    inner class BookViewHolder(val binding: BookItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Ebook) {

            with(binding) {
                title.text = book.title
                author.text = book.author
                publisher.text = book.publisher
                date.text = book.date
                platform.text = book.platform
                platform.setBackgroundColor(bookColor.getPlatformBGColor(book.platform))
                libraryName.text = book.libraryName

                rentCount.apply {
                    // Visibility
                    visibility = when {
                        book.countTotal < 0 -> View.INVISIBLE
                        else -> View.VISIBLE
                    }

                    // Color
                    setBackgroundColor(bookColor.getRentStatusColor(book))

                    // Text
                    @SuppressLint("SetTextI18n")
                    text = "${book.countRent} / ${book.countTotal}"
                }

                header.apply {
                    val backgroundColor = when {
                        book.isAvailable -> bookColor.bgGreen
                        book.countTotal < 0 -> bookColor.bgLightBlue
                        else -> bookColor.bgRed
                    }
                    setBackgroundColor(backgroundColor)
                }

                imageRecent.visibility = when {
                    isRecentlyPublished(book.date) -> View.VISIBLE
                    else -> View.GONE
                }

                // Bind onclick url
                bookView.setOnClickListener {
                    if (book.url.isNotEmpty()) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(book.url))
                        bookView.context.startActivity(browserIntent)
                    }
                }

                thumbnail.load(book.thumbnailUrl)
            }
        }

        /**
         * 2년 내에 출판된 책인지 확인
         */
        internal fun isRecentlyPublished(date: String): Boolean {
            try {
                val dateTimeFormatter = when {
                    date.matches("""^\d\d\d\d-\d\d-\d\d""".toRegex()) -> DateTimeFormatter.ISO_DATE
                    date.matches("""^\d\d\d\d\d\d\d\d""".toRegex()) -> DateTimeFormatter.ofPattern("yyyyMMdd")
                    else -> return false
                }

                return ChronoUnit.DAYS.between(
                    LocalDate.parse(date, dateTimeFormatter),
                    LocalDate.now()) < (365 * 2)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
    }
}
