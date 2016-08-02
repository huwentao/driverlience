package dl.cs.org.driverlinence.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.cardinfo.component.annotation.Layout;
import com.cardinfo.component.network.service.NetTools;
import com.cardinfo.component.network.service.TaskResult;
import com.cardinfo.component.network.service.impl.HttpTask;

import butterknife.Bind;
import butterknife.OnClick;
import dl.cs.org.driverlinence.R;
import dl.cs.org.driverlinence.base.AppActivity;
import dl.cs.org.driverlinence.http.HttpService;
import dl.cs.org.driverlinence.http.LoadingDialog;

@Layout(layoutId = R.layout.activity_query_order)
public class QueryOrderActivity extends AppActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.listView) ListView listView;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar(toolbar);

        snackbar = Snackbar.make(toolbar, "", Snackbar.LENGTH_SHORT);

        queryPlan();
    }

    @OnClick(R.id.query)
    public void queryPlan() {
        HttpTask httpTask = HttpService.getInstance().queryPlan();
        NetTools.excute(httpTask, new LoadingDialog(this), new NetTools.CallBack() {
            @Override
            public void onComplete(TaskResult taskResult) {
                if (taskResult.isSuccess()) {

                } else {
                    snackbar.setText(taskResult.getError()).show();
                }
            }
        });
    }


}
