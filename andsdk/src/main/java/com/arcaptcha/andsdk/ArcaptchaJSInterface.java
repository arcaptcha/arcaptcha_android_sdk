package com.arcaptcha.andsdk;

import android.content.Context;
import android.util.Log;

public class ArcaptchaJSInterface {
    ArcaptchaDialog.ArcaptchaListener arcaptchaListener;
    Context context;

    ArcaptchaJSInterface(Context c, ArcaptchaDialog.ArcaptchaListener arcaptchaListener) {
        this.context = c;
        this.arcaptchaListener = arcaptchaListener;
    }

    @android.webkit.JavascriptInterface
    public void passToken(String token){
        if(arcaptchaListener != null) arcaptchaListener.onSuccess(token);
    }

    @android.webkit.JavascriptInterface
    public void dismiss(){
        if(arcaptchaListener != null) arcaptchaListener.onCancel();
    }
}
