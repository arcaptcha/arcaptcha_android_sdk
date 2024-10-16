## Android SDK for ARCaptcha

###### [Installation](#installation) | [Example](#display-a-arcaptcha-challenge) | [Configs](#config-params)

This SDK provides a wrapper for [ARCaptcha](https://www.arcaptcha.ir). You will need to configure a `site key` and a `secret key` from your arcaptcha account in order to use it.


### Installation

## Gradle
<pre>
// Register JitPack Repository inside the root build.gradle file
repositories {
    <b>maven { url 'https://jitpack.io' }</b> 
}
// Add ARCaptcha sdk dependency inside the app's build.gradle file
dependencies {
    <b>implementation 'com.github.arcaptcha:arcaptcha_android_sdk:v0.1.1'</b>
}
</pre>

## Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

//Step 2. Add the dependency
<dependency>
    <groupId>com.github.arcaptcha</groupId>
    <artifactId>arcaptcha_android_sdk</artifactId>
    <version>v0.1.1</version>
</dependency>
```



#### Display a ARCaptcha challenge

The following snippet code will ask the user to complete a challenge. 

```java

ArcaptchaDialog.ArcaptchaListener arcaptchaListener = new ArcaptchaDialog.ArcaptchaListener() {
    @Override
    public void onSuccess(String token) {
        Toast.makeText(MainActivity.this, "Puzzle Solved, Token Generated!",
                Toast.LENGTH_LONG).show();
        arcaptchaDialog.dismiss();

        Log.d("Token: [" + token + "]");
        txvLog.setText("ArcaptchaToken: \n" + token);
    }

    @Override
    public void onCancel() {
        arcaptchaDialog.dismiss();
    }
};

ArcaptchaDialog.Builder arcaptchaDialogBuilder = new ArcaptchaDialog.Builder(
    YOUR_API_SITE_KEY, DOMAIN, arcaptchaListener);
arcaptchaDialog = arcaptchaDialogBuilder.build();
arcaptchaDialog.show(getSupportFragmentManager(), "arcaptcha_dialog");
```
To set theme, bg_color or custom url you can instantiate dialog like this :

```java
ArcaptchaDialog.Builder arcaptchaDialogBuilder = new ArcaptchaDialog.Builder(
    YOUR_API_SITE_KEY, DOMAIN, arcaptchaListener);
arcaptchaDialogBuilder.setTheme("dark"); //Optional
arcaptchaDialogBuilder.setBackgroundColor("#7E7E7E"); //Optional
arcaptchaDialogBuilder.setChallengeUrl(YOUR_URL); //Optional
arcaptchaDialog = arcaptchaDialogBuilder.build();
```

You can also set timeout and listener for response code:

```java
arcaptchaDialogBuilder.setResponseCodeListener(new ArcaptchaDialog.ResponseCodeListener() {
    @Override
    public void onResponse(int statusCode) {
        appendLog("Response Code: " + statusCode);
    }
});

arcaptchaDialogBuilder.setTimeout(10000, new ArcaptchaDialog.TimeoutCallback() {
    @Override
    public void onTimeout() {
        appendLog("Timeout Happens!");
    }
});
```


##### Config params


|Name|Values/Type|Required|Default|Description|
|--|---|---|---|---|
|`siteKey`|String|**Yes**|-|This is your sitekey, this allows you to load challenges. If you need a sitekey, please visit [ARCaptcha](https://arcaptcha.ir/sign-up), and sign up to get your sitekey.|
|`domain`|String|**Yes**|-|-|
|`challenge_url`|String|**No**|`"https://widget.arcaptcha.ir/show_challenge"`|Url that contains arcaptcha challenge |
|`theme`|String|**No**|`"light"`|Will set theme of widget|
|`bg_color`|String|**No**|`""`|Note: if you set this propery to `""` `bg_color` will be `transparent`| 


#### Verify the completed challenge

After retrieving a `token`, you should pass it to your backend in order to verify the validity of the token by doing a [server side check](https://docs.arcaptcha.ir/docs/installation#verify-the-user-response-server-side) using the ARCaptcha secret linked to your sitekey.
