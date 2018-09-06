package com.example.blink22.topgitcontributors;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class ContributorPageFragment extends Fragment {
    private static final String CONTRIBUTOR_URI = "photo_uri";
    public static final int MAX_PROGRESS = 100;

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static ContributorPageFragment newInstance(Uri uri){
        Bundle args = new Bundle();
        args.putParcelable(CONTRIBUTOR_URI, uri);

        ContributorPageFragment fragment = new ContributorPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUri = getArguments().getParcelable(CONTRIBUTOR_URI);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contributor_page, container, false);

        mWebView = v.findViewById(R.id.fragment_contributor_page_web_view);
        mProgressBar = v.findViewById(R.id.fragment_contributor_page_progress_bar);
        mProgressBar.setMax(MAX_PROGRESS);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if(mUri.toString().startsWith("http")){
                    return false;
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, mUri);
                    startActivity(intent);
                    return true;
                }
            }
        });

        mWebView.loadUrl(mUri.toString());

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100){
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });

        return v;
    }

    public boolean canGoBack(){
        return mWebView.canGoBack();
    }

    public void goBack(){
        mWebView.goBack();
    }
}
