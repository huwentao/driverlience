package dl.cs.org.driverlinence.http;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.cardinfo.component.network.service.CancelListener;
import com.cardinfo.component.network.service.Progress;
import com.cardinfo.component.network.service.RetryListener;
import com.cardinfo.component.network.service.TaskResult;

/**
 * Created by huwentao on 16-4-25.
 */
public class LoadingDialog extends AlertDialog implements Progress {
    private CancelListener cancelListener;


    public LoadingDialog(Context context) {
        super(context);
        setTitle(null);
        setMessage("加载中...");
        setCancelable(true);
        setCancelListener(() -> {
            if (cancelListener != null) {
                cancelListener.cancel();
            }
        });
    }


    @Override
    public void onProgress(int progress, boolean done, boolean isUp) {

    }

    @Override
    public void showProgress() {
        show();
    }

    @Override
    public void hideProgress() {
        dismiss();
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
        this.cancelListener = cancelListener;
    }
}
