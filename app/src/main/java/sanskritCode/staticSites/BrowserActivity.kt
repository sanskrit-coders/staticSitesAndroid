package sanskritCode.staticSites

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebView


class BrowserActivity : BaseActivity() {
    val browserWebClient = BrowserWebClient()
    var currentUrl: String = "file:///android_asset/docs/index.html"
    private fun setupGlobalCookieStorage() {
        CookieManager.setAcceptFileSchemeCookies(true)
        CookieManager.getInstance().setAcceptCookie(true)
    }

    private fun setupContentContainer() {
        setContentView(R.layout.activity_browser)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun getPermissions() {
        getPermission(Manifest.permission.INTERNET, this)
        getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)
    }

    private fun setupWebView(savedInstanceState: Bundle?) {
        val webView = getWebView()
        setupWebViewCookieStorage(webView)
        setupWebViewSettings(webView)
        getPermissions()
        val testHtmlUrl = "file://${Environment.getExternalStorageDirectory()}/Download/amazon.mhtml"
        currentUrl = savedInstanceState?.get("currentUrl") as? String ?: testHtmlUrl
        webView.loadUrl(currentUrl)
    }

    private fun getWebView(): WebView {
        return findViewById(R.id.main_webview) as WebView
    }

    private fun setupWebViewCookieStorage(webView: WebView) {
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }
    }

    private fun setupWebViewSettings(webView: WebView) {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.setWebViewClient(browserWebClient)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupGlobalCookieStorage();
        setupContentContainer();
        setupWebView(savedInstanceState);
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
        // Save the user's current game state
        outState?.run {
            putString("currentUrl", currentUrl)
        }
    }

}
