package dl.cs.org.driverlinence.ui;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cardinfo.component.annotation.Layout;
import com.cardinfo.component.network.service.NetTools;
import com.cardinfo.component.network.service.TaskResult;
import com.cardinfo.component.network.service.impl.HttpTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import dl.cs.org.driverlinence.R;
import dl.cs.org.driverlinence.base.AppActivity;
import dl.cs.org.driverlinence.http.HttpService;
import dl.cs.org.driverlinence.http.LoadingDialog;
import dl.cs.org.driverlinence.http.ViewProgress;

@Layout(layoutId = R.layout.activity_main)
public class MainActivity extends AppActivity implements SmsContent.OnChangeListener {
    @Bind(R.id.image) ImageView image;
    @Bind(R.id.ID) TextInputEditText ID;
    @Bind(R.id.imageCode) TextInputEditText imageCode;
    @Bind(R.id.messageCode) TextInputEditText messageCode;
    @Bind(R.id.progress) ViewProgress progress;
    @Bind(R.id.smsProgress) ViewProgress smsProgress;

    private List<String> imageUrls = new ArrayList<>();
    private AppAdapter<String> listAdapter;
    private Snackbar snackbar;
    private HttpTask downloadTask;

    private static final String IDNUM_TAG = "idnum_tag";
    private static final String CACHE_TAG = "cache_tag";
    private SharedPreferences sharedPref;
    private SmsContent msContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{
                    "android.permission.MOUNT_UNMOUNT_FILESYSTEMS",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.READ_SMS",
                    "android.permission.READ_SMS",
                    "android.permission.WRITE_SMS",
                    "android.permission.RECEIVE_SMS"
            }, 100);
        }

        snackbar = Snackbar.make(image, "", Snackbar.LENGTH_SHORT);
        downloadTask = HttpService.getInstance().getImageCode(snackbar);

        sharedPref = getSharedPreferences(CACHE_TAG, MODE_PRIVATE);
        ID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(IDNUM_TAG, ID.getText().toString());
                    editor.apply();
                }
            }
        });

        String idNum = sharedPref.getString(IDNUM_TAG, "");
        ID.setText(idNum);

        msContent = new SmsContent(this, new Handler(), this);
        //注册短信变化监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, msContent);

        getImageCode();

        imageCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==4){
                    getMessageCode();
                }
            }
        });
    }

    @OnClick(R.id.image)
    public void refreshImage() {
        getImageCode();
    }

    @OnClick(R.id.getImageCode)
    public void getImageCode() {
        smsProgress.hideProgress();
        NetTools.excute(downloadTask, progress, new NetTools.CallBack() {
            @Override
            public void onComplete(TaskResult taskResult) {
                if (taskResult.isSuccess()) {
                    File file = (File) taskResult.getResult();
                    Glide.with(MainActivity.this)
                            .load(file)
                            .override(600, 300)
                            .placeholder(R.drawable.loading)
                            .crossFade()
                            .into(image);
                } else {
                    snackbar.setText("图形验证码获取失败,点击重新获取")
                            .setAction("重新获取", (v) -> {
                                getImageCode();
                            }).show();
                }
            }
        });
    }

    @OnClick(R.id.getMessageCode)
    public void getMessageCode() {
        if (checkMessageCode()) {
            HttpTask httpTask = HttpService.getInstance().getMessageCode(
                    ID.getText().toString().trim(),
                    imageCode.getText().toString().trim()
            );
            smsProgress.showProgress();
            NetTools.excute(httpTask, new NetTools.CallBack() {
                @Override
                public void onComplete(TaskResult taskResult) {
                    smsProgress.hideProgress();
                    if (taskResult.isSuccess()) {
                        snackbar.setText("短信验证码获取成功").show();
                    } else {
                        snackbar.setText(taskResult.getError()).show();
                    }
//                    getImageCode();
                }
            });
        }
    }

    @OnClick(R.id.login)
    public void login() {
        if (checkLogin()) {
            HttpTask httpTask = HttpService.getInstance().login(ID.getText().toString(),
                    messageCode.getText().toString(), imageCode.getText().toString());
            NetTools.excute(httpTask, new LoadingDialog(this), new NetTools.CallBack() {
                @Override
                public void onComplete(TaskResult taskResult) {
                    if (taskResult.isSuccess()) {
                        snackbar.setText(taskResult.getStatus() + "\t" + taskResult.getResult()).show();
                        forward(QueryOrderActivity.class);
                    } else {
                        snackbar.setText(taskResult.getError()).show();
                    }
                }
            });
        }
    }

    private boolean checkLogin() {
        String idNum = ID.getText().toString();
        String imageCodeNum = imageCode.getText().toString();
        String messageCodeNum = messageCode.getText().toString();
        if (TextUtils.isEmpty(idNum)) {
            snackbar.setText("输入身份证号").show();
            return false;
        }
        if (TextUtils.isEmpty(imageCodeNum)) {
            snackbar.setText("输入图形验证码").show();
            return false;
        }
        if (TextUtils.isEmpty(messageCodeNum)) {
            snackbar.setText("输入短信验证码");
            return false;
        }
        return true;
    }

    private boolean checkMessageCode() {
        String idNum = ID.getText().toString();
        String imageCodeNum = imageCode.getText().toString();
        if (TextUtils.isEmpty(idNum)) {
            snackbar.setText("输入身份证号").show();
            return false;
        }
        if (TextUtils.isEmpty(imageCodeNum)) {
            snackbar.setText("输入图形验证码").show();
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.getContentResolver().unregisterContentObserver(msContent);
    }

    @Override
    public void onChange(String smsText) {
        if (!TextUtils.isEmpty(smsText))
            messageCode.setText(smsText);
//        smsProgress.hideProgress();
    }
}
