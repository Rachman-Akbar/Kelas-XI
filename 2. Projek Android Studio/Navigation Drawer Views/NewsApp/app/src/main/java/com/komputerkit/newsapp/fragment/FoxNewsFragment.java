package com.komputerkit.newsapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.komputerkit.newsapp.databinding.FragmentFoxNewsBinding;

public class FoxNewsFragment extends Fragment {

    private FragmentFoxNewsBinding binding;

    public FoxNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFoxNewsBinding.inflate(inflater, container, false);

        binding.webViewFox.setWebViewClient(new WebViewClient());
        binding.webViewFox.getSettings().setJavaScriptEnabled(true);
        binding.webViewFox.loadUrl("https://www.foxnews.com/");

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
