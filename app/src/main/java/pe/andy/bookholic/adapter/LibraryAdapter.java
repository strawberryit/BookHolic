package pe.andy.bookholic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;
import pe.andy.bookholic.R;
import pe.andy.bookholic.searcher.LibrarySearchTask;

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
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View view = inflater.inflate(R.layout.library_item, parent, false);
        return new LibraryAdapter.LibraryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.LibraryViewHolder holder, int position) {
        LibrarySearchTask task = this.tasks.get(position);

        holder.tvLibraryName.setText(task.getLibraryName());

        holder.ivIcon.setVisibility(View.GONE);
        holder.pbSearchProgress.setVisibility(View.GONE);
        holder.ivDone.setVisibility(View.GONE);
        holder.ivFail.setVisibility(View.GONE);

        View icon = holder.ivIcon;
        switch(task.getSearchStatus()) {
            case PROGRESS:
                icon = holder.pbSearchProgress;
                break;
            case DONE:
                if (task.getResultCount() > 0) {
                    icon = holder.ivDone;
                }
                break;
            case FAIL:
                icon = holder.ivFail;
        }
        icon.setVisibility(View.VISIBLE);

        // UI를 초기화
        holder.tvLibraryName.setTextColor(textDefault);
        holder.tvSearchCount.setVisibility(View.GONE);

        if (task.isSearchDone()) {
            if (task.getResultCount() == 0) {
                holder.tvLibraryName.setTextColor(textMuted);
            }
            else {
                holder.tvSearchCount.setText(String.format(Locale.KOREA, "%d", task.getResultCount()));
                holder.tvSearchCount.setTextColor(Color.WHITE);
                holder.tvSearchCount.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (this.tasks == null) ? 0 : this.tasks.size();
    }

    class LibraryViewHolder extends RecyclerView.ViewHolder {

        TextView tvLibraryName, tvSearchCount;
        ProgressBar pbSearchProgress;
        ImageView ivIcon, ivDone, ivFail;


        LibraryViewHolder(View itemView) {
            super(itemView);

            pbSearchProgress = itemView.findViewById(R.id.search_progress);
            ivIcon = itemView.findViewById(R.id.library_icon);
            ivDone = itemView.findViewById(R.id.search_done);
            ivFail = itemView.findViewById(R.id.search_fail);

            tvLibraryName = itemView.findViewById(R.id.library_name);
            tvSearchCount = itemView.findViewById(R.id.search_count);
        }
    }

}
