package sanskritCode.staticSites

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.*

class BrowserWebClient() : WebViewClient() {
    val historyStack = Stack<String>()
    val futureUrlsStack = Stack<String>()

    // According to https://stackoverflow.com/questions/36484074/is-shouldoverrideurlloading-really-deprecated-what-can-i-use-instead , this function should indeed be overridden for the code to work with all Android versions.
    @Suppress("OverridingDeprecatedMember")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        Log.i("WebView", "Attempting to load URL: $url")
        var currentUrl = url
        if (currentUrl.endsWith("/")) {
            currentUrl = currentUrl + "index.html"
        }
        view.loadUrl(currentUrl)
        historyStack.push(currentUrl)
        return true
    }

    fun goBack(view: WebView) {
        futureUrlsStack.push(historyStack.pop())
        view.loadUrl(historyStack.peek())
    }
}