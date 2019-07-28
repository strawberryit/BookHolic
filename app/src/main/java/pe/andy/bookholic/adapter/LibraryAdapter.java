package pe.andy.bookholic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;
import pe.andy.bookholic.R;
import pe.andy.bookholic.databinding.LibraryItemBinding;
import pe.andy.bookholic.searcher.LibrarySearchTask;

import static android.view.View.GONE;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>  {

    private Context mContext;
    @Getter @Setter
    private List<LibrarySearchTask> tasks;

    private static int textDefault;
    private static int textMuted;

    public LibraryAdapter(Context mContext, List<LibrarySearchTask> tasks) {
        this.mContext = mContext;
        this.tasks = tasks;

        textMuted = ContextCompat.getColor(mContext, R.color.textMuted);
        textDefault = ContextCompat.getColor(mContext, android.R.color.tab_indicator_text);
    }

    @NonNull
    @Override
    public LibraryAdapter.LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LibraryItemBinding binding = LibraryItemBinding.inflate(
                LayoutInflater.from(mContext), parent, false);

        return new LibraryViewHolder(binding.getRoot(), binding);
    }


    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.LibraryViewHolder holder, int position) {
        LibrarySearchTask task = this.tasks.get(position);

        holder.binding.libraryName.setText(task.getLibraryName());
        holder.binding.libraryIcon.setVisibility(GONE);
        holder.binding.searchProgress.setVisibility(GONE);
        holder.binding.searchDone.setVisibility(GONE);
        holder.binding.searchFail.setVisibility(GONE);

        View icon = holder.binding.libraryIcon;
        switch(task.getSearchStatus()) {
            case PROGRESS:
                icon = holder.binding.searchProgress;
                break;
            case DONE:
                if (task.getResultCount() > 0) {
                    icon = holder.binding.searchDone;
                }
                break;
            case FAIL:
                icon = holder.binding.searchFail;
        }
        icon.setVisibility(View.VISIBLE);

        // UI를 초기화
        holder.binding.libraryName.setTextColor(textDefault);
        holder.binding.searchCount.setVisibility(GONE);

        if (task.isSearchDone()) {
            if (task.getResultCount() == 0) {
                holder.binding.libraryName.setTextColor(textMuted);
            }
            else {
                holder.binding.searchCount.setText(String.format(Locale.KOREA, "%d", task.getResultCount()));
                holder.binding.searchCount.setTextColor(Color.WHITE);
                holder.binding.searchCount.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (this.tasks == null) ? 0 : this.tasks.size();
    }

    class LibraryViewHolder extends RecyclerView.ViewHolder {
        LibraryItemBinding binding;

        public LibraryViewHolder(@NonNull View itemView, LibraryItemBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

}
