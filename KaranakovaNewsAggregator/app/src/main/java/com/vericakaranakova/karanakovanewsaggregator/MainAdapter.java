package com.vericakaranakova.karanakovanewsaggregator;

import android.content.Intent;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vericakaranakova.karanakovanewsaggregator.databinding.ViewPagerLayoutBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {

    private static final String TAG = "MainAdapter";
    private final MainActivity mainActivity;
    private ArrayList<Articles> articles = new ArrayList<>();

    public MainAdapter(MainActivity mainActivity, ArrayList<Articles> articles) {
        this.mainActivity = mainActivity;
        this.articles = articles;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(ViewPagerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);
        Articles a = articles.get(position);

        if (!a.getTitle().equals("null")) {
            holder.binding.articleTitle.setVisibility(View.VISIBLE);
            holder.binding.articleTitle.setText(a.getTitle());
            holder.binding.articleTitle.setOnClickListener(v -> clickWeb(a.getUrl()));
        } else {
            holder.binding.articleTitle.setVisibility(View.GONE);
        }

        if (!a.getPublishedAt().equals("null")) {
            try {
                holder.binding.articleDate.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date date = sdf.parse(a.getPublishedAt());
                holder.binding.articleDate.setText(new SimpleDateFormat("MMM d, yyyy hh:mm", Locale.US).format(date));
            } catch (ParseException e) {
                holder.binding.articleDate.setVisibility(View.GONE);
            }
        } else {
            holder.binding.articleDate.setVisibility(View.GONE);
        }

        if (!a.getAuthor().equals("null")) {
            holder.binding.articleAuthor.setVisibility(View.VISIBLE);
            holder.binding.articleAuthor.setText(a.getAuthor());
        } else {
            holder.binding.articleAuthor.setVisibility(View.GONE);
        }

        if (a.getImageUrl().equals("null")) {
            holder.binding.articleImage.setVisibility(View.VISIBLE);
            holder.binding.articleImage.setImageResource(R.drawable.noimage);
            holder.binding.articleImage.setOnClickListener(v -> clickWeb(a.getUrl()));
        } else {
            Picasso.get().load(a.getImageUrl()).error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(holder.binding.articleImage);
            holder.binding.articleImage.setOnClickListener(v -> clickWeb(a.getUrl()));
        }

        if (!a.getDescription().equals("null")) {
            holder.binding.articleText.setVisibility(View.VISIBLE);
            holder.binding.articleText.setText(a.getDescription());
            holder.binding.articleText.setOnClickListener(v -> clickWeb(a.getUrl()));
            holder.binding.articleText.setMovementMethod(new ScrollingMovementMethod());
        } else {
            holder.binding.articleText.setVisibility(View.GONE);
        }

        holder.binding.articleCount.setText(String.format(
                Locale.getDefault(),"%d of %d", (position+1), articles.size()));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void clickWeb(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        try {
            mainActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
