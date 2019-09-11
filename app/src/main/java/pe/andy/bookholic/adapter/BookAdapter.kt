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

    override fun getItemCount(): Int {
        return this.books.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = this.books[position]

        val binding = holder.binding

        with(book) {
            binding.title.text = title
            binding.author.text = author
            binding.publisher.text = publisher
            binding.date.text = date
            binding.platform.text = platform
            binding.platform.setBackgroundColor(bookColor.getPlatformBGColor(platform))
            binding.libraryName.text = libraryName
        }

        // Visibility
        val visibility = if (book.countTotal < 0) View.INVISIBLE else View.VISIBLE
        binding.rentCount.visibility = visibility

        // Color
        val color = bookColor.getRentStatusColor(book)
        binding.rentCount.setBackgroundColor(color)

        // Text
        val count = book.countRent.toString() + " / " + book.countTotal
        binding.rentCount.text = count

        // Bind onclick url
        binding.bookView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(book.url))
            mContext.startActivity(browserIntent)
        }

        loadThumbnail(binding.thumbnail, book.thumbnailUrl)
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

    inner class BookViewHolder(itemView: View, val binding: BookItemBinding) : RecyclerView.ViewHolder(itemView)
}
