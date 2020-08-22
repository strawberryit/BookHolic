package pe.andy.bookholic.adapter

import android.content.Context
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
        private val mContext: Context,
        private var books: List<Ebook> = emptyList()
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val bookColor: BookColor = BookColor(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {

        val binding = BookItemBinding.inflate(
                LayoutInflater.from(mContext), parent, false)

        return BookViewHolder(binding.root, binding)
    }

    override fun getItemCount(): Int
            = this.books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = this.books[position]
        holder.bind(book)
    }

    private fun loadThumbnail(view: ImageView, url: String) {
        try {
            Glide.with(this.mContext)
                    .load(url)
                    .into(view)
        } catch (ex: Exception) {
            Log.d("BookHolic", "Thumbnail url loading failure: ${url}")
        }
    }

    inner class BookViewHolder(itemView: View, val binding: BookItemBinding) : RecyclerView.ViewHolder(itemView) {

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
                    text = "${book.countRent} / ${book.countTotal}"
                }

                // Bind onclick url
                bookView.setOnClickListener {
                    if (book.url.isNotEmpty()) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(book.url))
                        mContext.startActivity(browserIntent)
                    }
                }

                loadThumbnail(thumbnail, book.thumbnailUrl)
            }
        }
    }
}
