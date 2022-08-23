package com.arcaptcha.andsdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import java.io.Serializable;

public class ArcaptchaDialog extends DialogFragment {
    Activity activity;
    View rootView;
    ImageView ivClose;
    WebView webMain;
    ProgressBar progressBar;
    ArcaptchaListener arcaptchaListener;
    public String siteKey;
    public String domain;
    public String theme;
    public String bgColor;

    public static final String ARCAPTCHA_URL = "https://widget.arcaptcha.ir/show_challenge";

    public static final String ARCAPTCHA_LISTENER_TAG = "arcaptcha_listener";
    public static final String ARCAPTCHA_SITE_KEY_TAG = "site_key";
    public static final String ARCAPTCHA_DOMAIN_TAG = "domain";
    public static final String ARCAPTCHA_THEME_TAG = "theme";
    public static final String ARCAPTCHA_BG_COLOR_TAG = "bg_color";

    public ArcaptchaDialog(){}

    public static ArcaptchaDialog getInstance(String siteKey, String domain, ArcaptchaListener arcaptchaListener){
        final Bundle args = new Bundle();
        args.putSerializable(ARCAPTCHA_LISTENER_TAG, arcaptchaListener);
        args.putString(ARCAPTCHA_SITE_KEY_TAG, siteKey);
        args.putString(ARCAPTCHA_DOMAIN_TAG, domain);
        final ArcaptchaDialog arcaptchaDialog = new ArcaptchaDialog();
        arcaptchaDialog.setArguments(args);
        return arcaptchaDialog;
    }

    public static ArcaptchaDialog getInstance(String siteKey, String domain,  String theme,  String bg_color, ArcaptchaListener arcaptchaListener){
        final Bundle args = new Bundle();
        args.putSerializable(ARCAPTCHA_LISTENER_TAG, arcaptchaListener);
        args.putString(ARCAPTCHA_SITE_KEY_TAG, siteKey);
        args.putString(ARCAPTCHA_DOMAIN_TAG, domain);
        args.putString(ARCAPTCHA_THEME_TAG, theme);
        args.putString(ARCAPTCHA_BG_COLOR_TAG, bg_color);
        final ArcaptchaDialog arcaptchaDialog = new ArcaptchaDialog();
        arcaptchaDialog.setArguments(args);
        return arcaptchaDialog;
    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        arcaptchaListener = (ArcaptchaListener) bundle.getSerializable(ARCAPTCHA_LISTENER_TAG);
        siteKey = bundle.getString(ARCAPTCHA_SITE_KEY_TAG);
        domain = bundle.getString(ARCAPTCHA_DOMAIN_TAG);
        theme = bundle.getString(ARCAPTCHA_THEME_TAG,"");
        bgColor = bundle.getString(ARCAPTCHA_BG_COLOR_TAG,"");

        activity = getActivity();
        rootView = inflater.inflate(R.layout.arcaptcha_fragment, container, false);
        webMain = rootView.findViewById(R.id.webMain);
        progressBar = rootView.findViewById(R.id.progressBar);
        ivClose = rootView.findViewById(R.id.ivClose);
        setupWebView();

        ivClose.setOnClickListener(v -> {
         dismiss();
        });
        return rootView;
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void setupWebView(){
        ArcaptchaJSInterface javascriptInterface = new ArcaptchaJSInterface(activity, arcaptchaListener);

        final WebSettings settings = webMain.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        webMain.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webMain.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webMain.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webMain.addJavascriptInterface(javascriptInterface, "AndroidInterface");
        webMain.loadUrl(getCaptchaUrl());


        webMain.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public String getCaptchaUrl(){
        StringBuilder urlBuilder = new StringBuilder(ARCAPTCHA_URL);
        urlBuilder.append("?site_key=");
        urlBuilder.append(siteKey);
        urlBuilder.append("&domain=");
        urlBuilder.append(domain);
        if (!theme.equals("")) {
            urlBuilder.append("&theme=");
            urlBuilder.append(theme);
        }
        if (!bgColor.equals("")) {
            urlBuilder.append("&bg_color=");
            urlBuilder.append(bgColor);
        }
        return urlBuilder.toString();
    }

    public static abstract class ArcaptchaListener implements Parcelable, Serializable {
        public abstract void onSuccess(String token);
        public abstract void onCancel();

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    }
}
