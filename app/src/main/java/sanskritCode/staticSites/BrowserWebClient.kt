package sanskritCode.staticSites

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

class BrowserWebClient() : WebViewClient() {

    // According to https://stackoverflow.com/questions/36484074/is-shouldoverrideurlloading-really-deprecated-what-can-i-use-instead , this function should indeed be overridden for the code to work with all Android versions.
    @Suppress("OverridingDeprecatedMember")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        Log.i("WebView", "Attempting to load URL: $url")
        var currentUrl = url
        if (currentUrl.endsWith("/")) {
            currentUrl = currentUrl + "index.html"
        }
        view.loadUrl(currentUrl)
        return true
    }
}