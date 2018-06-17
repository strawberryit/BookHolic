package pe.andy.bookholic.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pe.andy.bookholic.R;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>  {

    private Context mContext;
    @Getter @Setter
    private List<LibrarySearchTask> tasks;

    static int textDefault;
    static int textMuted;

    public LibraryAdapter(Context mContext, List<LibrarySearchTask> tasks) {
        this.mContext = mContext;
        this.tasks = tasks;

        textMuted = ContextCompat.getColor(mContext, R.color.textMuted);
        textDefault = ContextCompat.getColor(mContext, android.R.color.tab_indicator_text);
    }

    @Override
    public LibraryAdapter.LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View view = inflater.inflate(R.layout.library_item, parent, false);
        return new LibraryAdapter.LibraryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(LibraryAdapter.LibraryViewHolder holder, int position) {
        LibrarySearchTask task = this.tasks.get(position);

        holder.tvLibraryName.setText(task.getLibraryName());

        holder.ivIcon.setVisibility(View.GONE);
        holder.pbSearchProgress.setVisibility(View.GONE);
        holder.ivDone.setVisibility(View.GONE);
        holder.ivFail.setVisibility(View.GONE);

        switch(task.getSearchStatus()) {
            case INITIAL:
                holder.ivIcon.setVisibility(View.VISIBLE);
                break;
            case PROGRESS:
                holder.pbSearchProgress.setVisibility(View.VISIBLE);
                break;
            case DONE:
                holder.ivDone.setVisibility(View.VISIBLE);
                break;
            case FAIL:
                holder.ivFail.setVisibility(View.VISIBLE);
        }

        if (task.getSearchStatus() == LibrarySearchTask.LibrarySearchStatus.DONE) {
            if (task.getResultCount() == 0) {
                holder.tvLibraryName.setTextColor(textMuted);

                holder.tvSearchCount.setText("(결과 없음)");
                holder.tvSearchCount.setTextColor(textMuted);
            }
            else {
                holder.tvLibraryName.setTextColor(textDefault);

                holder.tvSearchCount.setText("(" + task.getResultCount() + ")");
                holder.tvSearchCount.setTextColor(textDefault);
            }
        }
        else {
            holder.tvLibraryName.setTextColor(textDefault);

            holder.tvSearchCount.setText("");
            holder.tvSearchCount.setTextColor(textDefault);
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


        public LibraryViewHolder(View itemView) {
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
