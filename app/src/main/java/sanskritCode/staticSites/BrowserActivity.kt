package sanskritCode.staticSites

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebView
import sanskritCode.downloaderFlow.BaseActivity
import sanskritCode.downloaderFlow.MainActivity


class BrowserActivity : BaseActivity() {
    internal val LOGGER_TAG = javaClass.getSimpleName()
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
        // Requires API level 19
//        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebViewSettings(webView: WebView) {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setAllowFileAccessFromFileURLs(true)
        webSettings.setAllowUniversalAccessFromFileURLs(true)
        webSettings.setLoadWithOverviewMode(true)
        webSettings.setUseWideViewPort(true)
        webSettings.setBuiltInZoomControls(true)
        webSettings.setDisplayZoomControls(false)
        webSettings.setSupportZoom(true)
        webSettings.setDefaultTextEncodingName("utf-8")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        webView.setWebViewClient(browserWebClient)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupGlobalCookieStorage()
        setupContentContainer()
        setupWebView(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
        // Save the user's current game state
        outState.run {
            putString("currentUrl", currentUrl)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_activity_browser, menu)
        Log.i(LOGGER_TAG, "creating options menu")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_item_update_sites -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
