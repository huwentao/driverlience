package dl.cs.org.driverlinence.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.cardinfo.component.annotation.Layout;
import com.cardinfo.component.base.Html5Activity;

import butterknife.Bind;
import dl.cs.org.driverlinence.R;

/**
 * Created by huwentao on 16/8/1.
 */
@Layout(layoutId = R.layout.activity_helper_html)
public class WebActivity extends Html5Activity {
    @Bind(R.id.webView) WebView webView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.title) TextView actTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar(toolbar);
        String url = getIntent().getStringExtra("url");
        init(webView, url);
    }

    @Override
    protected void onNewPagerTitle(WebView webView, String s) {
        setActTitle(s);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        }
        return true;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        super.initToolbar(toolbar);
        setActTitle(getTitle());
    }

    protected void setActTitle(CharSequence title) {
        if (TextUtils.isEmpty(title)) return;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
