package com.komputerkit.newsapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.komputerkit.newsapp.databinding.FragmentSkyNewsBinding;

public class SkyNewsFragment extends Fragment {

    private FragmentSkyNewsBinding binding;

    public SkyNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSkyNewsBinding.inflate(inflater, container, false);

        binding.webViewSky.setWebViewClient(new WebViewClient());
        binding.webViewSky.getSettings().setJavaScriptEnabled(true);
        binding.webViewSky.loadUrl("https://news.sky.com/");

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
