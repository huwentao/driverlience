package dl.cs.org.driverlinence.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.cardinfo.component.base.BaseActivity;
import com.cardinfo.component.network.service.TaskResult;
import com.cardinfo.component.network.service.ThreadPoolTool;

import dl.cs.org.driverlinence.http.SimpleHttpTask;

/**
 * Created by huwentao on 16/8/1.
 */
public class AppActivity extends BaseActivity {
    private Context mContext;
    private AppApplication mApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mApp = (AppApplication) getApplication();
    }

    protected void showSnackBar(View view,String content) {
        if (view != null) {
            Snackbar.make(view,content,Snackbar.LENGTH_SHORT).show();
        }
    }

    public Context getAppContext() {
        return mContext;
    }

    /**
     * 错误响应统一处理
     *
     * @param taskResult 响应结果
     */
    public void handleFailed(View view ,TaskResult taskResult) {
        SimpleHttpTask.handleFailed(this, view, taskResult);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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



    public AppApplication getApp() {
        return mApp;
    }

    public void setApp(AppApplication mApp) {
        this.mApp = mApp;
    }

    @Override
    protected void onDestroy() {
        ThreadPoolTool.getInstance().cancelAll();
        super.onDestroy();
    }
}
