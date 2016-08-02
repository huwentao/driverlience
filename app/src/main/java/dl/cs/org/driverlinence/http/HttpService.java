package dl.cs.org.driverlinence.http;

import android.net.Uri;
import android.support.design.widget.Snackbar;

import com.cardinfo.component.network.exception.NetError;
import com.cardinfo.component.network.net.IHttpRequest;
import com.cardinfo.component.network.service.impl.HttpTask;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mokey on 2016/4/20.
 */
public class HttpService {
    private final static String SERVER_ADDRESS = "http://www.hncsjj.gov.cn:8085/CsZzYy/";//测试服务器


    public HttpTask getImageCode(Snackbar snackbar) {
        return new DownloadTask(SERVER_ADDRESS + "idCode?" + Math.random(), snackbar);
    }

    public HttpTask getMessageCode(String id, String imageYzm) {
        return new SimpleHttpTask() {

            @Override
            public void doTask() throws NetError {
                Map<String, String> params = new HashMap<>();
                params.put("sfzmhm", id);
                params.put("imgRandCode", imageYzm);
                post(getHttpRequest(), "cszzyy/getSjyzm.action", params);
            }
        };
    }

    public HttpTask login(String id, String yzm, String imageYzm) {
        return new SimpleHttpTask() {

            @Override
            public void doTask() throws NetError {
                Map<String, String> params = new HashMap<>();
                params.put("sfzmhm", id);
                params.put("yzm", yzm);
                params.put("imgRandCode", imageYzm);
                post(getHttpRequest(), "cszzyy/sjyzmYz.action", params);
            }
        };
    }

    /**
     * 查询考试计划
     * @return
     */
    public HttpTask queryPlan() {
        return new SimpleHttpTask() {
            @Override
            public void doTask() throws NetError {
                post(getHttpRequest(), "cszzyy/queryPlan.action", "");
            }
        };
    }

    /**
     * 查询考试计划
     * @param planId
     * @return
     */
    public HttpTask queryPlanByIds(String planId) {
        return new SimpleHttpTask() {
            @Override
            public void doTask() throws NetError {
                Map<String, String> params = new HashMap<>();
                params.put("planIds", planId);
                post(getHttpRequest(), "cszzyy/queryPlanByIds.action", params);
            }
        };
    }

    /**
     * 预约考试
     * @param id
     * @param imageCode
     * @return
     */
    public HttpTask yuekaoRel(String id,String imageCode){
        return new SimpleHttpTask() {
            @Override
            public void doTask() throws NetError {
                Map<String,String> params = new HashMap<>();
                params.put("planId",id);
                params.put("imgRandCode",imageCode);
                post(getHttpRequest(),"cszzyy/yyks.action",params);
            }
        };
    }

    public HttpTask toQxyy(){
        return null;
    }


    /**
     * 返回帮助页面地址
     *
     * @return
     */
    public String getHelperUrl() {
        Uri uri = Uri.parse(SERVER_ADDRESS);
        return String.format("http://%s:%s/app-server/pages/help/", uri.getHost(), uri.getPort());
    }

    /**
     * 卡友商户通业务商户注册协议
     *
     * @return
     */
    public String getRegisterAgreementUrl() {
        Uri uri = Uri.parse(SERVER_ADDRESS);
        return String.format("http://%s:%s/app-server/pages/app/register_agreement/", uri.getHost(), uri.getPort());
    }

    /**
     * 卡友商户通业务商户合作协议
     *
     * @return
     */
    public String getMerchantAgreementUrl() {
        Uri uri = Uri.parse(SERVER_ADDRESS);
        return String.format("http://%s:%s/app-server/pages/app/partner_agreement/", uri.getHost(), uri.getPort());
    }

    /**
     * @param request
     * @param url
     * @throws NetError
     */
    private void get(IHttpRequest request, String url, Object... params) throws NetError {
        request.get(String.format(SERVER_ADDRESS + url, params));
        Logger.i(String.format(SERVER_ADDRESS + url, params));
    }

    /**
     * @param request
     * @param url
     * @param json
     * @throws NetError
     */
    private void post(IHttpRequest request, String url, String json) throws NetError {
        Logger.d("request JSON=>%s", json);
        request.post(SERVER_ADDRESS + url, json, false);
    }

    private void post(IHttpRequest request, String url, Map<String,String> params) throws NetError {
        Logger.d("params=>%s", params);
        request.post(SERVER_ADDRESS + url, params);
    }
    /**
     * @param request
     * @param url
     * @param json
     * @throws NetError
     */
    private void put(IHttpRequest request, String url, String json) throws NetError {
        Logger.d("request JSON=>%s", json);
        request.put(SERVER_ADDRESS + url, json, false);
    }

    /**
     * @param request
     * @param url
     * @throws NetError
     */
    private void delete(IHttpRequest request, String url, Object... params) throws NetError {
        request.delete(String.format(SERVER_ADDRESS + url, params));
    }

    private static class SingletonHolder {
        private static HttpService instance = new HttpService();
    }

    public static HttpService getInstance() {
        return SingletonHolder.instance;
    }

    private HttpService() {

    }
}
