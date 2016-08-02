package dl.cs.org.driverlinence.http;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.cardinfo.component.network.service.CancelListener;
import com.cardinfo.component.network.service.Progress;
import com.cardinfo.component.network.service.RetryListener;
import com.cardinfo.component.network.service.TaskResult;


/**
 * Created by huwentao on 16-4-22.
 */
public class ViewProgress extends ProgressBar implements Progress {
    private boolean isLoading = true;

    public ViewProgress(Context context) {
        super(context);
    }

    public ViewProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onProgress(int progress, boolean done, boolean isUp) {
        setProgress(progress);
    }

    @Override
    public void showProgress() {
        setVisibility(VISIBLE);
        setProgress(0);
    }

    @Override
    public void hideProgress() {
        setProgress(0);
        if (isLoading) {
            setVisibility(GONE);
        }
    }

    @Override
    public void onError(String TAG, TaskResult error) {

    }

    @Override
    public void noData() {

    }

    @Override
    public void setRetryListener(RetryListener retryListener) {

    }

    @Override
    public void setCancelListener(CancelListener cancelListener) {

    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
