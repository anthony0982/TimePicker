package com.example.test;

import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.widget.Toast;

import com.example.base.BaseActivity;
import com.example.timepicker.R;
import com.example.timepicker.TimePicker;
import com.example.timepicker.TimePicker.TimePickerListener;
import com.example.util.LogUtil;

import datetime.DateTime;

public class MainActivity extends BaseActivity implements TimePickerListener {

    @ViewInject(id = R.id.time_picker)
    private TimePicker mTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimePicker.setTimePickerListener(this);
    }

    @Override
    public void onPick(DateTime time) {
        LogUtil.d(getLogTag(), "onPick calendar : " + time.toString());
        Toast.makeText(getBaseContext(), "已选时间：" + time.toString(), Toast.LENGTH_SHORT).show();
    }
}
