package com.oliver.serial;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.oliver.sdk.SerialManager;
import com.oliver.sdk.constant.Constants;
import com.oliver.sdk.listener.cmd.IOnReceiveCommandListener;
import com.oliver.sdk.listener.cmd.IOnSendCommandListener;
import com.oliver.sdk.model.SerialConfig;
import com.oliver.sdk.util.LogUtils;
import com.oliver.serial.util.RuleUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private TextView mTvUartPath;
    private TextView mTvUartName;
    private TextView mTvSHowCommand;
    private EditText mEtEditCommand;
    private ImageView mIvClearEditCommand;
    private Button mBtnSendEditCommand;
    private Button mBtnSendDefaultCommand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvUartName = findViewById(R.id.uart_name);
        mTvUartPath = findViewById(R.id.uart_path);
        mTvSHowCommand = findViewById(R.id.tv_show_command);
        mEtEditCommand = findViewById(R.id.et_edit_command);
        mIvClearEditCommand = findViewById(R.id.iv_clear_command);
        mBtnSendEditCommand = findViewById(R.id.btn_send_edit_command);
        mBtnSendDefaultCommand = findViewById(R.id.btn_send_default_command);
        // 默认串口3
        updateUartViews(Constants.SERIAL_PORT_PATH.get(2));

        LogUtils.d(TAG,"Build.MODEL: " + Build.MODEL);

        initToolBar();
        initListeners();
    }

    private void updateUartViews(String dev) {
        mTvUartPath.setText(dev);
        mTvUartName.setText(Constants.SERIAL_PORT_MAP.get(dev));
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        //设置Toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle("串口调试工具");
    }

    private void initListeners() {
        SerialManager.getInstance().addListener(new IOnSendCommandListener() {
            @Override
            public void onSendCommand(String dev, String command) {
                LogUtils.d(TAG, "onSendCommand: " + RuleUtils.formatCommand(dev, command, true));
            }
        });
        SerialManager.getInstance().addListener(new IOnReceiveCommandListener() {
            @Override
            public void onReceiveCommand(String dev, String command) {
                LogUtils.d(TAG, "onReceiveCommand: " + RuleUtils.formatCommand(dev, command, false));
            }
        });

        RxView.clicks(mIvClearEditCommand)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mEtEditCommand.setText("");
                    }
                });
        RxView.clicks(mBtnSendDefaultCommand)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .map(new Function<Object, String>() {
                    @Override
                    public String apply(Object unit) throws Exception {
                        return mTvUartPath.getText().toString();
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String uart) throws Exception {
                        SerialManager.getInstance().sendCommand(TextUtils.isEmpty(uart) ? Constants.SERIAL_PORT_PATH.get(3) : uart);
                    }
                });

        RxView.clicks(mBtnSendEditCommand)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .map(new Function<Object, String[]>() {
                    @Override
                    public String[] apply(Object unit) throws Exception {
                        String command = mEtEditCommand.getText().toString();
                        String dev = mTvUartPath.getText().toString();
                        return new String[]{dev, command};
                    }
                })
                .subscribe(new Consumer<String[]>() {
                    @Override
                    public void accept(String[] uart) throws Exception {
                        String dev = TextUtils.isEmpty(uart[0]) ? Constants.SERIAL_PORT_PATH.get(3) : uart[0];
                        SerialManager.getInstance().sendCommand(dev, uart[1]);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //写一个menu的资源文件.然后创建就行了.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String uart = null;
        switch (item.getItemId()) {
            case R.id.serial_port_1:
                LogUtils.d(TAG, "click serial_port_1");
                uart = Constants.SERIAL_PORT_PATH.get(0);
                break;
            case R.id.serial_port_2:
                LogUtils.d(TAG, "click serial_port_2");
                uart = Constants.SERIAL_PORT_PATH.get(1);
                break;
            case R.id.serial_port_3:
                LogUtils.d(TAG, "click serial_port_3");
                uart = Constants.SERIAL_PORT_PATH.get(2);
                break;
            case R.id.serial_port_4:
                LogUtils.d(TAG, "click serial_port_4");
                uart = Constants.SERIAL_PORT_PATH.get(3);
                break;
        }
        updateUartViews(uart);
        return true;

    }
}
