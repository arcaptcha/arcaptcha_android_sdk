package com.arcaptcha.andsdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArcaptchaDialog extends DialogFragment {
    Activity activity;
    View rootView;
    ImageView ivClose;
    WebView webMain;
    ProgressBar progressBar;
    ArcaptchaListener arcaptchaListener;
    ResponseCodeListener responseCodeListener = null;
    TimeoutCallback timeoutCallback = null;
    public long timeoutDuration = 0;
    public String siteKey;
    public String domain;

    public String challengeUrl = "";
    public String theme = "";
    public String bgColor = "";

    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());

    public static final String ARCAPTCHA_LISTENER_TAG = "arcaptcha_listener";
    public static final String TIMEOUT_CALLBACK_TAG = "timeout_callback";
    public static final String TIMEOUT_DURATION_TAG = "timeout_duration";
    public static final String RESPONSE_CODE_LISTENER_TAG = "response_code_listener";
    public static final String ARCAPTCHA_CHALLENGE_KEY_TAG = "challenge_key";
    public static final String ARCAPTCHA_SITE_KEY_TAG = "site_key";
    public static final String ARCAPTCHA_DOMAIN_TAG = "domain";
    public static final String ARCAPTCHA_THEME_TAG = "theme";
    public static final String ARCAPTCHA_BG_COLOR_TAG = "bg_color";

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
        if(bundle.containsKey(TIMEOUT_CALLBACK_TAG)){
            timeoutCallback = (TimeoutCallback) bundle.getSerializable(TIMEOUT_CALLBACK_TAG);
        }
        if(bundle.containsKey(RESPONSE_CODE_LISTENER_TAG)){
            responseCodeListener = (ResponseCodeListener) bundle.getSerializable(RESPONSE_CODE_LISTENER_TAG);
        }
        timeoutDuration = bundle.getLong(TIMEOUT_DURATION_TAG);

        challengeUrl = bundle.getString(ARCAPTCHA_CHALLENGE_KEY_TAG);
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
    private void setupWebView(){
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
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                String mainUrl = getCaptchaUrl();
                if(mainUrl.equals(url)) {
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setRequestMethod("GET");
                        int responseCode = connection.getResponseCode();
                        if (responseCodeListener != null) {
                            responseCodeListener.onResponse(responseCode);
                        }
                    } catch (IOException e) {
                    }
                }

                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                cancelTimer();

                if(timeoutDuration > 0) {
                    timeoutHandler.postDelayed(() -> {
                        if(timeoutCallback != null){
                            webMain.stopLoading();
                            timeoutCallback.onTimeout();
                        }
                    }, timeoutDuration);
                }
            }

            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                cancelTimer();
                Log.d("ArcaptchaPageFinished", "Success: " + url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                cancelTimer();
                Log.d("ArcaptchaReceiveError", error.toString());
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                cancelTimer();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        cancelTimer();
    }

    public void cancelTimer(){
        timeoutHandler.removeCallbacksAndMessages(null);
    }

    private String getCaptchaUrl(){
        StringBuilder urlBuilder = new StringBuilder(challengeUrl);
        urlBuilder.append("?site_key=");
        urlBuilder.append(siteKey);
        urlBuilder.append("&domain=");
        urlBuilder.append(domain);
        if (!theme.isEmpty()) {
            urlBuilder.append("&theme=");
            urlBuilder.append(theme);
        }
        if (!bgColor.isEmpty()) {
            urlBuilder.append("&bg_color=");
            urlBuilder.append(bgColor);
        }
        return urlBuilder.toString();
    }

    public static abstract class ResponseCodeListener extends ArcSerializable {
        public abstract void onResponse(int statusCode);
    }

    public static abstract class TimeoutCallback extends ArcSerializable {
        public abstract void onTimeout();
    }

    public static abstract class ArcaptchaListener extends ArcSerializable {
        public abstract void onSuccess(String token);
        public abstract void onCancel();
    }

    public static abstract class ArcSerializable implements Parcelable, Serializable {
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {}
    }

    public static class Builder {
        private String siteKey;
        private String domain;
        private ArcaptchaListener arcaptchaListener;
        private ResponseCodeListener responseCodeListener = null;
        private TimeoutCallback timeoutCallback = null;
        private long timeoutDuration = 0;
        private String challengeUrl = "https://widget.arcaptcha.ir/show_challenge";
        private String theme = "";
        private String bgColor = "";

        public Builder(String siteKey, String domain, ArcaptchaListener arcaptchaListener){
            this.siteKey = siteKey;
            this.domain = domain;
            this.arcaptchaListener = arcaptchaListener;
        }

        public Builder setChallengeUrl(String challengeUrl) {
            this.challengeUrl = challengeUrl;
            return this;
        }

        public Builder setTheme(String theme) {
            this.theme = theme;
            return this;
        }

        public Builder setBackgroundColor(String bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Builder setTimeout(long timeoutDuration, TimeoutCallback timeoutCallback) {
            this.timeoutDuration = timeoutDuration;
            this.timeoutCallback = timeoutCallback;
            return this;
        }

        public Builder setResponseCodeListener(ResponseCodeListener responseCodeListener) {
            this.responseCodeListener = responseCodeListener;
            return this;
        }

        public ArcaptchaDialog build(){
            final Bundle args = new Bundle();
            args.putSerializable(ARCAPTCHA_LISTENER_TAG, arcaptchaListener);
            if(timeoutCallback != null) args.putSerializable(TIMEOUT_CALLBACK_TAG, timeoutCallback);
            if(responseCodeListener != null)
                args.putSerializable(RESPONSE_CODE_LISTENER_TAG, responseCodeListener);
            args.putLong(TIMEOUT_DURATION_TAG, timeoutDuration);
            args.putString(ARCAPTCHA_SITE_KEY_TAG, siteKey);
            args.putString(ARCAPTCHA_DOMAIN_TAG, domain);
            args.putString(ARCAPTCHA_CHALLENGE_KEY_TAG, challengeUrl);
            args.putString(ARCAPTCHA_THEME_TAG, theme);
            args.putString(ARCAPTCHA_BG_COLOR_TAG, bgColor);
            final ArcaptchaDialog arcaptchaDialog = new ArcaptchaDialog();
            arcaptchaDialog.setArguments(args);
            return arcaptchaDialog;
        }
    }
}
