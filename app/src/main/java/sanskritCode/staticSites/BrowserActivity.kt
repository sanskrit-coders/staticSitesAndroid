package sanskritCode.staticSites

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import sanskritCode.downloaderFlow.ArchiveInfo
import sanskritCode.downloaderFlow.BaseActivity
import sanskritCode.downloaderFlow.MainActivity
import java.io.File


class BrowserActivity : BaseActivity() {
    internal val LOGGER_TAG = javaClass.getSimpleName()
    val browserWebClient = BrowserWebClient()
    var currentUrl: String = "file:///android_asset/docs/index.html"
    private fun setupGlobalCookieStorage() {
        CookieManager.setAcceptFileSchemeCookies(true)
        CookieManager.getInstance().setAcceptCookie(true)
    }

    private fun getDestDir(): File {
        val sdcard = Environment.getExternalStorageDirectory()
        return File(sdcard.absolutePath, getString(sanskritCode.downloaderFlow.R.string.df_destination_sdcard_directory))
    }

    // Prevents the app from reverting to the start page after the
    // "back pressed-> application back in the foreground" sequence.
    override fun onBackPressed() {
        // Effectively disabling back button for now.
    }


    private fun setupContentContainer() {
        setContentView(R.layout.activity_browser)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun getPermissions() {
        getPermission(Manifest.permission.INTERNET, this)
        getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)
    }

    private fun getStartPageHtmls(): List<String> {
        val sharedArchiveInfoStore = getSharedPreferences(
                getString(R.string.df_archive_info_store), Context.MODE_PRIVATE)
        val archiveInfoMap = sharedArchiveInfoStore.all
        val destDir = getDestDir()
        val startPageHtmls = archiveInfoMap.values.map { archiveInfoStr ->
            val staticSiteInfo = StaticSiteInfo(archiveInfoStr as String)
            val startPageUrl = staticSiteInfo.getStartPageUrl(destDir)
            """<li><a href="${startPageUrl}">${startPageUrl}</a></li>"""
        }
        return startPageHtmls
    }

    private fun setupWebView(savedInstanceState: Bundle?) {
        val webView = getWebView()
        setupWebViewCookieStorage(webView)
        setupWebViewSettings(webView)
        getPermissions()
        val savedUrl = savedInstanceState?.get("currentUrl") as? String
        if (savedUrl != null) {
            currentUrl = savedUrl
            webView.loadUrl(currentUrl)
        } else {
            val startPageHtmls = getStartPageHtmls()
            if (startPageHtmls.isEmpty()) {
                startDownloaderFlow()
            } else {
                val indexListHtml = """
                    <div style="font-size: 500%">
                    Index pages of installed sites:
                    ${startPageHtmls.joinToString(separator = "\n", prefix = "\n<ul>", postfix = "</ul>")}
                    </div>
                """.trimIndent()
                Log.d(LOGGER_TAG, indexListHtml)
                val indexIndexorumFile = File(getDestDir(), "index.html")
                indexIndexorumFile.writeText(indexListHtml)

                currentUrl = indexIndexorumFile.toURI().toURL().toString()
                webView.loadUrl(currentUrl)
            }
        }
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

    fun startDownloaderFlow() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_item_update_sites -> {
                startDownloaderFlow()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
