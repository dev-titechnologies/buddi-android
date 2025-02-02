package buddiapp.com.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import buddiapp.com.R;

public class WebActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ActionBar actionBar = getSupportActionBar();
//1 photo salar slip , online state, pan,aadhar,
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        webView = (WebView) findViewById(R.id.web);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        if (getIntent().getStringExtra("url") != null) {

            webView.loadUrl(getIntent().getStringExtra("url"));

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
