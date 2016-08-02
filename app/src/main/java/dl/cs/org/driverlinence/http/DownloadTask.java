package dl.cs.org.driverlinence.http;

import android.os.Environment;
import android.support.design.widget.Snackbar;

import com.cardinfo.component.network.exception.NetError;
import com.cardinfo.component.network.net.HttpMethod;
import com.cardinfo.component.network.service.TaskResult;
import com.cardinfo.component.network.service.impl.HttpTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by huwentao on 16/8/2.
 */
public class DownloadTask extends HttpTask {
    private File diretory;
    private String downloadUrl;
    private Snackbar snackbar;

    public DownloadTask(String downloadUrl, Snackbar snackbar) {
        this.downloadUrl = downloadUrl;
        this.snackbar = snackbar;
    }

    @Override
    public boolean before() {
        File file = Environment.getExternalStorageDirectory();
        diretory = new File(file, "driverLience");
        if (!diretory.exists()) {
            boolean flag = diretory.mkdirs();
            if (!flag)
                snackbar.setText("文件夹创建失败").show();
            return true;
        } else {
            File[] files = diretory.listFiles();
            for (File f : files) {
                f.deleteOnExit();
            }
            return true;
        }
    }

    @Override
    public void doTask() throws NetError {
        getHttpRequest().download(HttpMethod.GET, downloadUrl);
    }


    @Override
    public void after() {
        TaskResult taskResult = getResult();
        if (taskResult.isSuccess()) {
            Response response = taskResult.getResponse();
            Headers headers = response.headers();
            Set<String> names = headers.names();
//            for (String name : names) {
//                Logger.d("===>%s <> %s", name, headers.get(name));
//            }
            try {
                BufferedSource source = response.body().source();
                if (!diretory.exists())
                    diretory.mkdir();
                File file = new File(diretory, String.valueOf((int) (Math.random() * 100000000)) + ".jpg");
                if (!file.exists())
                    file.createNewFile();
                taskResult.setResult(file);
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(source);
                sink.flush();
                sink.close();
                source.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
