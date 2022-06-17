package pe.andy.bookholic.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import pe.andy.bookholic.databinding.BookItemBinding
import pe.andy.bookholic.model.FavoriteBook
import pe.andy.bookholic.util.BookColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class FavoriteAdapter(
    val onItemLongClick: (FavoriteBook) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    lateinit var bookColor: BookColor

    private val diffUtil = object : DiffUtil.ItemCallback<FavoriteBook>() {
        override fun areItemsTheSame(oldItem: FavoriteBook, newItem: FavoriteBook): Boolean {
            return oldItem.seq == newItem.seq
        }

        override fun areContentsTheSame(oldItem: FavoriteBook, newItem: FavoriteBook): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class FavoriteViewHolder(val binding: BookItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: FavoriteBook) {
            with(binding) {
                title.text = favorite.title
                author.text = favorite.author
                publisher.text = favorite.publisher
                date.text = favorite.date
                platform.text = favorite.platform
                platform.setBackgroundColor(bookColor.getPlatformBGColor(favorite.platform))
                libraryName.text = favorite.libraryName

                imageRecent.visibility = when {
                    isRecentlyPublished(favorite.date) -> View.VISIBLE
                    else -> View.GONE
                }

                // Bind onclick url
                bookView.setOnClickListener {
                    if (favorite.url.isNotEmpty()) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(favorite.url))
                        bookView.context.startActivity(browserIntent)
                    }
                }
                bookView.setOnLongClickListener {
                    onItemLongClick.invoke(favorite)
                    true
                }

                thumbnail.load(favorite.thumbnailUrl)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = BookItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        bookColor = BookColor(parent.context)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = differ.currentList[position]
        holder.bind(favorite)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}