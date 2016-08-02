package dl.cs.org.driverlinence.http;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import com.cardinfo.component.network.service.TaskResult;
import com.cardinfo.component.network.service.TaskStatus;
import com.cardinfo.component.network.service.impl.HttpTask;
import com.cardinfo.component.utils.JSON;
import com.cardinfo.component.utils.NetworkUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import dl.cs.org.driverlinence.base.AppApplication;
import okhttp3.Response;

/**
 * Created by mokey on 2016/4/20.
 * 智能POS 请求头新增参数：  APP_TYPE=SMART_POS
 * 商户APP请求头新增参数：   APP_TYPE=MERCHANT_APP
 * 商户门户请求头新增参数：   APP_TYPE=MERCHANT_PORTAL
 */
public abstract class SimpleHttpTask extends HttpTask {

    public SimpleHttpTask() {

    }

    @Override
    public boolean before() {
//        getHttpRequest().addHeader("APP_TYPE", "SMART_POS");
        if (!NetworkUtils.isConnected(AppApplication.getAppContext())) {
            haveNoNet();
            return false;
        }
        return true;
    }

    @Override
    public void after() {
        TaskResult taskResult = getResult();
        Response response = taskResult.getResponse();
        TaskStatus status = taskResult.getStatus();
        Logger.d("请求状态：status(%s)", taskResult.getStatus());
        if (status.isSuccess()) {
            try {
                String bodyStr = response.body().string();
                if (isEmpty(bodyStr)) {
                    setDataEmpty();
                } else {
                    String[] results = bodyStr.replace("\"", "").split("@");
                    if ("0".equals(results[0])) {
                        taskResult.setStatus(TaskStatus.success);
                    } else {
                        taskResult.setStatus(TaskStatus.Failure);
                        taskResult.setError(results[1]);
                    }
                    taskResult.setResult(bodyStr);
                }
            } catch (IOException e) {
                failure();
            } catch (JSONException e) {
                Logger.d(e.getMessage(), e);
            }
        } else if (status.isFailure() || status.isServerError()) {
            if (response != null) {
                try {
                    String message = response.body().string();
                    taskResult.setResult(message);
                    if (!TextUtils.isEmpty(message) && JSON.isJsonString(message)) {
                        JSONObject object = new JSONObject(message);
                        message = object.getString("msg");
                        taskResult.setError(message);
                    }
                } catch (Exception e) {
                    Logger.e(e, e.getMessage());
                    failure();
                }
            }
        }
        String result = (String) taskResult.getResult();
        if (JSON.isJsonString(result)) {
            Logger.json((String) taskResult.getResult());
        } else {
            Logger.d("请求状态：status(%s)\t请求结果:(%s)\nError:%s", taskResult.getStatus(), taskResult.getResult(), taskResult.getNetError());
        }
    }

    /**
     * 判断响应结果是否为空数据
     *
     * @param body
     * @return
     * @throws JSONException
     */
    private boolean isEmpty(String body) throws JSONException {
        if (TextUtils.isEmpty(body)) {
            return true;
        }
        return body.length() == 2
                || "null".equals(body)
                || "NULL".equals(body)
                || "Null".equals(body)
                || "[]".equals(body);
    }

    /**
     * 错误消息处理
     *
     * @param mRootView
     * @param taskResult
     */
    public static void handleFailed(Context context, View mRootView, TaskResult taskResult) {
        TaskStatus status = taskResult.getStatus();
        switch (status) {
            case Failure:
                String result = (String) taskResult.getResult();
                if (!TextUtils.isEmpty(result) && JSON.isJsonString(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        taskResult.setError(object.getString("msg"));
                    } catch (JSONException e) {
                        Logger.e(e, e.getMessage());
                    }
                    showSnackBar(mRootView, taskResult.getError());
                } else {
                    Logger.e(result);
                    showSnackBar(mRootView, "网络加载失败，请稍后重试");
                }
                break;
            case Unauthorized:
                result = (String) taskResult.getResult();
                if (!TextUtils.isEmpty(result) && JSON.isJsonString(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        taskResult.setError(object.getString("msg"));
                    } catch (JSONException e) {
                        Logger.e(e, e.getMessage());
                    }
                } else {
                    Logger.e(result);
                    taskResult.setError("登录失效，请重新登录！");
                }
                break;
            case Forbidden:
                result = (String) taskResult.getResult();
                if (!TextUtils.isEmpty(result) && JSON.isJsonString(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        taskResult.setError(object.getString("msg"));
                    } catch (JSONException e) {
                        Logger.e(e, e.getMessage());
                    }
                } else {
                    Logger.e(result);
                    taskResult.setError("网络加载失败，请稍后重试");
                }
                showSnackBar(mRootView, taskResult.getError());
                break;
            case ServerError:
                showSnackBar(mRootView, taskResult.getError());
                break;
            case TimeOut:
                showSnackBar(mRootView, "网络连接超时，请稍后重试");
                break;
            case DataEmpty:
//                showSnackBar(mRootView, taskResult.getError());
                break;
            case noNet:
                showSnackBar(mRootView, "当前没有可用的网络");
                break;
        }
    }

    private static void showSnackBar(View mRootView, String content) {
        if (mRootView != null) {
            Snackbar.make(mRootView, content, Snackbar.LENGTH_SHORT).show();
        }
    }
}
