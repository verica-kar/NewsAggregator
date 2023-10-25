package com.vericakaranakova.karanakovanewsaggregator;

import androidx.recyclerview.widget.RecyclerView;

import com.vericakaranakova.karanakovanewsaggregator.databinding.ViewPagerLayoutBinding;

public class MainViewHolder extends RecyclerView.ViewHolder {

    ViewPagerLayoutBinding binding;

    public MainViewHolder(ViewPagerLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
