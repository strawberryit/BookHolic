package pe.andy.bookholic.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pe.andy.bookholic.R;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.util.Str;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context mContext;
    @Getter @Setter
    private List<Ebook> books;

    private int bgOrange, bgGreen, bgRed, bgMaroon, bgLightBlue, bgPurple, bgGray;

    public BookAdapter(Context mContext, List<Ebook> books) {
        this.mContext = mContext;
        this.books = books;

        bgRed = ContextCompat.getColor(mContext, R.color.bgRed);
        bgMaroon = ContextCompat.getColor(mContext, R.color.bgMaroon);
        bgOrange = ContextCompat.getColor(mContext, R.color.bgOrange);
        bgGreen = ContextCompat.getColor(mContext, R.color.bgGreen);
        bgLightBlue = ContextCompat.getColor(mContext, R.color.bgLightBlue);
        bgPurple = ContextCompat.getColor(mContext, R.color.bgPurple);
        bgGray = ContextCompat.getColor(mContext, R.color.bgGray);

    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View view = inflater.inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Ebook book = this.books.get(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvPublisher.setText(book.getPublisher());
        holder.tvDate.setText(book.getDate());
        holder.tvPlatform.setText(book.getPlatform());
        holder.tvPlatform.setBackgroundColor(getPlatformBGColor(holder.tvPlatform.getText()));
        holder.tvLibraryName.setText(book.getLibraryName());

        // Visibility
        int visibility = (book.getCountTotal() < 0) ? View.INVISIBLE : View.VISIBLE;
        holder.tvRentCount.setVisibility(visibility);

        // Color
        int color = (book.getCountRent() < book.getCountTotal()) ? this.bgGreen : this.bgRed;
        holder.tvRentCount.setBackgroundColor(color);

        // Text
        holder.tvRentCount.setText(book.getCountRent() + " / " + book.getCountTotal());

        // Bind onclick url
        holder.cardView.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.getUrl()));
            mContext.startActivity(browserIntent);
        });

        Glide.with(this.mContext)
                .load(book.getThumbnailUrl())
                .into(holder.ivThumbnail);
    }

    private int getPlatformBGColor(CharSequence platform) {
        String name = Str.def(platform.toString());

        if (name.contains("교보"))
            return bgGreen;
        else if (name.contains("북큐브"))
            return bgOrange;
        else if (name.contains("예스24") || name.contains("YES24"))
            return bgLightBlue;
        else if (name.contains("메키아") || name.contains("MEKIA"))
            return bgPurple;
        else if (name.contains("ECO MOA"))
            return bgMaroon;
        else
            return bgGray;
    }

    @Override
    public int getItemCount() {
        return (this.books == null) ? 0 : this.books.size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView tvTitle, tvAuthor, tvPublisher, tvDate, tvPlatform, tvRentCount, tvLibraryName;
        ImageView ivThumbnail;

        public BookViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.book_view);
            tvTitle = itemView.findViewById(R.id.title);
            tvAuthor = itemView.findViewById(R.id.author);
            tvPublisher = itemView.findViewById(R.id.publisher);
            tvDate = itemView.findViewById(R.id.date);
            tvPlatform = itemView.findViewById(R.id.platform);
            tvLibraryName = itemView.findViewById(R.id.libraryName);
            tvRentCount = itemView.findViewById(R.id.rentCount);

            ivThumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }

}
