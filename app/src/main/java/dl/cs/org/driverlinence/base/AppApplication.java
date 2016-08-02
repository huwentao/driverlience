package dl.cs.org.driverlinence.base;

import com.cardinfo.component.base.AppActManager;
import com.cardinfo.component.base.BaseApplication;
import com.cardinfo.component.cache.IMemoryCache;
import com.cardinfo.component.cache.MemoryCache;
import com.cardinfo.component.network.service.ThreadPoolTool;
import com.cardinfo.component.utils.JSON;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by huwentao on 16/8/1.
 */
public class AppApplication extends BaseApplication {
    private IMemoryCache<String, Object> caches = MemoryCache.lruCache(50);

    /**
     * @param obj
     */
    public void saveCache(Object obj) {
        caches.put(obj.getClass().getName(), obj);
    }

    /**
     * @param key
     * @param obj
     */
    public void saveCache(String key, Object obj) {
        caches.put(key, obj);
    }


    /**
     * @param key
     * @return
     */
    public Object getCache(String key) {
        return caches.get(key);
    }

    /**
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getCache(Class<T> tClass) {
        Object o = getCache(tClass.getName());
        return tClass.cast(o);
    }

    /**
     * @param key
     * @return
     */
    public Object getCacheClear(String key) {
        return caches.remove(key);
    }

    /**
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getCacheClear(Class<T> tClass) {
        Object obj = getCacheClear(tClass.getName());
        return tClass.cast(obj);
    }

    /**
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> List<T> getArraysClear(String key, Class<T> tClass) {
        Object o = getCacheClear(key);
        TypeToken<List<T>> tTypeToken = new TypeToken<List<T>>() {
        };
        return JSON.parseArray(JSON.toJSONString(o), tTypeToken);
    }

    /**
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> List<T> getArrays(String key, Class<T> tClass) {
        Object o = getCache(key);
        TypeToken<List<T>> tTypeToken = new TypeToken<List<T>>() {
        };
        return JSON.parseArray(JSON.toJSONString(o), tTypeToken);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //测试用临时数据库文件存放在SD卡

        Logger.init()// default PRETTYLOGGER or use just init()
                .methodCount(2)                 // default 2
                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2);                // default 0
        openComponentLog();
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        caches.clear();
        ThreadPoolTool.getInstance().close();
        AppActManager.getInstance().exit();
        System.exit(0);
    }
}
