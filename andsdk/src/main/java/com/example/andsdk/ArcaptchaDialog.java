package com.example.andsdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;

public class ArcaptchaDialog extends DialogFragment {
    Activity activity;
    View rootView;
    WebView webMain;
    ArcaptchaListener arcaptchaListener;
    public String siteKey;
    public String domain;

    public static final String ARCAPTCHA_URL = "https://widget.arcaptcha.ir/show_challenge";

    public static final String ARCAPTCHA_LISTENER_TAG = "arcaptcha_listener";
    public static final String ARCAPTCHA_SITE_KEY_TAG = "site_key";
    public static final String ARCAPTCHA_DOMAIN_TAG = "domain";

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

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
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

        activity = getActivity();
        rootView = inflater.inflate(R.layout.arcaptcha_fragment, container, false);
        webMain = rootView.findViewById(R.id.webMain);
        setupWebView();

        return rootView;
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void setupWebView(){
        ArcaptchaJSInterface javascriptInterface = new ArcaptchaJSInterface(activity, arcaptchaListener);

        final WebSettings settings = webMain.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        webMain.setBackgroundColor(Color.TRANSPARENT);
        webMain.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webMain.addJavascriptInterface(javascriptInterface, "AndroidInterface");
        webMain.loadUrl(getCaptchaUrl());
    }

    public String getCaptchaUrl(){
        StringBuilder urlBuilder = new StringBuilder(ARCAPTCHA_URL);
        urlBuilder.append("?site_key=");
        urlBuilder.append(siteKey);
        urlBuilder.append("&domain=");
        urlBuilder.append(domain);
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
