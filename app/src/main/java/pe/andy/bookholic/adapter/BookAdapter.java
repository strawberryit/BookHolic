package pe.andy.bookholic.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pe.andy.bookholic.databinding.BookItemBinding;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.util.BookColor;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context mContext;
    @Getter @Setter
    private List<Ebook> books;

    private BookColor bookColor;

    public BookAdapter(Context mContext, List<Ebook> books) {
        this.mContext = mContext;
        this.books = books;

        bookColor = new BookColor(mContext);
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
        binding.platform.setBackgroundColor(bookColor.getPlatformBGColor(book.getPlatform()));
        binding.libraryName.setText(book.getLibraryName());

        // Visibility
        int visibility = (book.getCountTotal() < 0) ? View.INVISIBLE : View.VISIBLE;
        binding.rentCount.setVisibility(visibility);

        // Color
        int color = bookColor.getRentStatusColor(book);
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
