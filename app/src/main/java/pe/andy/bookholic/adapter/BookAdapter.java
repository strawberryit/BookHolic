package pe.andy.bookholic.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pe.andy.bookholic.R;
import pe.andy.bookholic.databinding.BookItemBinding;
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
    @NonNull
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        BookItemBinding binding = BookItemBinding.inflate(
                LayoutInflater.from(mContext), parent, false);

        return new BookViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Ebook book = this.books.get(position);

        BookItemBinding binding = holder.binding;

        binding.title.setText(book.getTitle());
        binding.author.setText(book.getAuthor());
        binding.publisher.setText(book.getPublisher());
        binding.date.setText(book.getDate());
        binding.platform.setText(book.getPlatform());
        binding.platform.setBackgroundColor(getPlatformBGColor(binding.platform.getText()));
        binding.libraryName.setText(book.getLibraryName());

        // Visibility
        int visibility = (book.getCountTotal() < 0) ? View.INVISIBLE : View.VISIBLE;
        binding.rentCount.setVisibility(visibility);

        // Color
        int color = (book.getCountRent() < book.getCountTotal()) ? this.bgGreen : this.bgRed;
        binding.rentCount.setBackgroundColor(color);

        // Text
        String count = book.getCountRent() + " / " + book.getCountTotal();
        binding.rentCount.setText(count);

        // Bind onclick url
        binding.bookView.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.getUrl()));
            mContext.startActivity(browserIntent);
        });

        try {
            Glide.with(this.mContext)
                    .load(book.getThumbnailUrl())
                    .into(binding.thumbnail);
        }
        catch (Exception ex) {
            Log.d("Bookholic", "Thumbnail url loading failure: " + book.getThumbnailUrl());
        }

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

        BookItemBinding binding;

        BookViewHolder(@NonNull View itemView, BookItemBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

}
