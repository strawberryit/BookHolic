package pe.andy.bookholic.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.andy.bookholic.R
import pe.andy.bookholic.databinding.LibraryItemBinding
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask.LibrarySearchStatus.*

class LibraryAdapter(
        private val mContext: Context,
        private val libraries: List<LibrarySearchTask>
) : RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    private val textMuted = ContextCompat.getColor(mContext, R.color.textMuted)
    private val textDefault = ContextCompat.getColor(mContext, android.R.color.tab_indicator_text)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val binding = LibraryItemBinding.inflate(
                LayoutInflater.from(mContext), parent, false)

        return LibraryViewHolder(binding.root, binding)
    }

    override fun getItemCount(): Int {
        return libraries.size
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val library = libraries[position]
        holder.bind(library)
    }

    inner class LibraryViewHolder(itemView: View, val binding: LibraryItemBinding)
        : RecyclerView.ViewHolder(itemView) {

        fun bind(library: LibrarySearchTask) {

            with(binding) {
                libraryName.text = library.libraryName
                libraryIcon.visibility = GONE
                searchProgress.visibility = GONE
                searchDone.visibility = GONE
                searchFail.visibility = GONE

                when (library.searchStatus) {
                    PROGRESS -> searchProgress
                    DONE ->
                        if (library.resultCount > 0) {
                            searchDone
                        } else {
                            libraryIcon
                        }
                    FAIL -> searchFail
                    else -> libraryIcon
                }.run {
                    visibility = VISIBLE
                }

                // UI를 초기화
                libraryName.setTextColor(textDefault)
                searchCount.visibility = GONE

                // 검색이 끝나면 검색결과에 따른 처리
                if (library.searchStatus == DONE) {
                    when(library.resultCount) {
                        0 -> libraryName.setTextColor(textMuted)
                        else -> {
                            with(searchCount) {
                                text = library.resultCount.toString()
                                setTextColor(Color.WHITE)
                                visibility = VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }
}