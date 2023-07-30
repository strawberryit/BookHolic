package pe.andy.bookholic.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.andy.bookholic.R
import pe.andy.bookholic.databinding.LibraryItemBinding
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask.LibrarySearchStatus.DONE
import pe.andy.bookholic.searcher.LibrarySearchTask.LibrarySearchStatus.FAIL
import pe.andy.bookholic.searcher.LibrarySearchTask.LibrarySearchStatus.PROGRESS

class LibraryAdapter(var libraries: MutableList<LibrarySearchTask>) : RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    var textMuted: Int = 0
    var textDefault: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val binding = LibraryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)

        textMuted = ContextCompat.getColor(parent.context, R.color.textMuted)
        textDefault = ContextCompat.getColor(parent.context, android.R.color.tab_indicator_text)

        return LibraryViewHolder(binding)
    }

    override fun getItemCount(): Int = libraries.size

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val library = libraries[position]
        holder.bind(library)
    }

    fun set(list: List<LibrarySearchTask>) {
        libraries.clear()
        libraries.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        libraries.clear()
        notifyDataSetChanged()
    }

    fun refresh() {
        notifyDataSetChanged()
    }

    inner class LibraryViewHolder(val binding: LibraryItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(library: LibrarySearchTask) {

            with(binding) {
                libraryName.text = library.library.name
                libraryIcon.visibility = GONE
                searchProgress.visibility = GONE
                searchDone.visibility = GONE
                searchFail.visibility = GONE

                when (library.searchStatus) {
                    PROGRESS -> searchProgress
                    DONE -> when {
                        library.resultCount > 0 -> searchDone
                        else -> libraryIcon
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