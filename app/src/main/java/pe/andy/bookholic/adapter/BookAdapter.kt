package pe.andy.bookholic.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pe.andy.bookholic.databinding.BookItemBinding
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.util.BookColor

class BookAdapter(
        private var books: List<Ebook> = emptyList()
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

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

    private fun loadThumbnail(view: ImageView, url: String) {
        try {
            Glide.with(view.context)
                    .load(url)
                    .into(view)
        } catch (ex: Exception) {
            Log.d("BookHolic", "Thumbnail url loading failure: ${url}")
        }
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

                // Bind onclick url
                bookView.setOnClickListener {
                    if (book.url.isNotEmpty()) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(book.url))
                        bookView.context.startActivity(browserIntent)
                    }
                }

                loadThumbnail(thumbnail, book.thumbnailUrl)
            }
        }
    }
}
