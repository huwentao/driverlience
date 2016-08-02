package dl.cs.org.driverlinence.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.cardinfo.component.network.service.impl.HttpTask;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huwentao on 16/8/2.
 */
/*
   * 监听短信数据库
   */
class SmsContent extends ContentObserver {
    private Cursor cursor = null;
    private Activity context;
    private OnChangeListener onChangeListener;
    private InternalHandler mHandler;

    public SmsContent(Activity context, Handler handler, OnChangeListener onChangeListener) {
        super(handler);
        this.context = context;
        this.onChangeListener = onChangeListener;
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onChange(boolean selfChange) {
        // TODO Auto-generated method stub
        super.onChange(selfChange);
        // 读取收件箱中指定号码的短信
        cursor = context.managedQuery(Uri.parse("content://sms/inbox"),
                new String[]{"_id", "address", "read", "body"},
                " address=? and read=?",
                new String[]{"监听的号码", "0"}, "_id desc");
        Logger.d("===> \t cursor.count=%s", cursor != null ? cursor.getCount() : null);
        // 按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("read", "1"); // 修改短信为已读模式
            cursor.moveToNext();
            Logger.d("cursor==>%s", cursor.toString());
            int smsbodyColumn = cursor.getColumnIndex("body");
            Logger.d("smsBodyColumn==>%s", smsbodyColumn);
            String smsBody = cursor.getString(smsbodyColumn);
            Logger.d("smsBody==>%s",smsBody);
            getHandler(this).obtainMessage(100, getDynamicPassword(smsBody)).sendToTarget();

        }
        // 在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃
        if (Build.VERSION.SDK_INT < 14) {
            cursor.close();
        }
    }

    /**
     * 得到Handler
     *
     * @param netService Handler
     * @return
     */
    private Handler getHandler(SmsContent netService) {
        synchronized (HttpTask.class) {
            if (mHandler == null) {
                mHandler = new InternalHandler(netService);
            }
            return mHandler;
        }
    }

    /**
     * 网络请求完成回调
     */
    private static class InternalHandler extends Handler {
        WeakReference<SmsContent> service = null;
//        NetService service = null;

        public InternalHandler(SmsContent netService) {
            super(Looper.getMainLooper());
            service = new WeakReference<>(netService);
//            service = netService;
        }

        @Override
        public void handleMessage(Message msg) {
            SmsContent smsContent = service.get();
//            NetService netService = service;
            switch (msg.what) {
                case 100:
                    if (smsContent.onChangeListener != null) {
                        smsContent.onChangeListener.onChange(msg.obj.toString());
                    }
                    break;
            }
        }
    }

    public interface OnChangeListener {
        void onChange(String smsText);
    }


    /**
     * 从字符串中截取连续6位数字组合 ([0-9]{" + 6 + "})截取六位数字 进行前后断言不能出现数字 用于从短信中获取动态密码
     *
     * @param str 短信内容
     * @return 截取得到的6位动态密码
     */
    public static String getDynamicPassword(String str) {
        // 6是验证码的位数一般为六位
        Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
                + 4 + "})(?![0-9])");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            System.out.print(m.group());
            dynamicPassword = m.group();
        }

        return dynamicPassword;
    }
}
