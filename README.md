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
    <b>implementation 'com.github.arcaptcha:arcaptcha_android_sdk:0.0.8'</b>
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
    <version>v0.0.8</version>
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

arcaptchaDialog = ArcaptchaDialog.getInstance(YOUR_API_SITE_KEY,
        DOMAIN, arcaptchaListener);
arcaptchaDialog.show(getSupportFragmentManager(), "arcaptcha_dialog_tag");
```
To set theme and bg_color parameter you can instantiate dialog like this :

```java
arcaptchaDialog = ArcaptchaDialog.getInstance(
        YOUR_API_SITE_KEY,
        DOMAIN,
        "dark",//theme
        "gray",//bg_color(If you want transparent just set it to "")
        ArcaptchaListener arcaptchaListener)
```


##### Config params


|Name|Values/Type|Required|Default|Description|
|---|---|---|---|---|
|`siteKey`|String|**Yes**|-|This is your sitekey, this allows you to load challenges. If you need a sitekey, please visit [ARCaptcha](https://arcaptcha.ir/sign-up), and sign up to get your sitekey.|
|`domain`|String|**Yes**|-|-|
|`theme`|String|**No**|`"light"`|Will set theme of widget|
|`bg_color`|String|**No**|`""`|Note: if you set this propery to `""` `bg_color` will be `transparent`| 


#### Verify the completed challenge

After retrieving a `token`, you should pass it to your backend in order to verify the validity of the token by doing a [server side check](https://docs.arcaptcha.ir/docs/installation#verify-the-user-response-server-side) using the ARCaptcha secret linked to your sitekey.
