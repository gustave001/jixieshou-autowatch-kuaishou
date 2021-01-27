package com.cmlanche.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cmlanche.core.utils.StringUtil;
import com.cmlanche.jixieshou.R;
import com.cmlanche.model.AppInfo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;

/**
 * 新建或编辑任务界面
 */
public class NewOrEditTaskActivity extends BaseActivity {

    private TextView taskTypeName;
    private TextInputEditText periodEdit;
    private TextInputEditText sleepTimeEdit;
    private AppInfo appInfo;
    private AppInfo editedAppInfo;
    private boolean isEdit;
    private MaterialButton deleteBtn;
    private MaterialButton sureBtn;
    private TextView title;

    @Override
    protected void initData(Bundle savedInstanceState) {
        String info = getIntent().getStringExtra("appInfo");
        if (StringUtil.isEmpty(info)) {
            this.isEdit = false;
        } else {
            this.isEdit = true;
            this.editedAppInfo = JSON.parseObject(info, AppInfo.class);
            this.appInfo = editedAppInfo;
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_new_task);
        findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.typeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(NewOrEditTaskActivity.this, TaskTypeListActivity.class), 100);
            }
        });

        taskTypeName = findViewById(R.id.name);
        periodEdit = findViewById(R.id.periodEdit);
        sleepTimeEdit = findViewById(R.id.sleepTimeEdit);
        sureBtn = findViewById(R.id.sureBtn);
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查数据合法性
                if (appInfo == null) {
                    Toast.makeText(getApplicationContext(), "请选择一个任务", Toast.LENGTH_LONG).show();
                    return;
                }
                String period = periodEdit.getText().toString();
                String sleepTime = sleepTimeEdit.getText().toString();
                if (StringUtil.isEmpty(period)) {
                    Toast.makeText(getApplicationContext(), "请输入执行时长", Toast.LENGTH_LONG).show();
                    return;
                }
                if (StringUtil.isEmpty(sleepTime)) {
                    Toast.makeText(getApplicationContext(), "请输入滑动间隔", Toast.LENGTH_LONG).show();
                    return;
                }
                appInfo.setPeriod(Integer.parseInt(period));
                appInfo.setSleepTime(Integer.parseInt(sleepTime));

                Intent data = new Intent();
                data.putExtra("appInfo", JSON.toJSONString(appInfo));
                if (isEdit) {
                    data.putExtra("editedAppInfo", JSON.toJSONString(editedAppInfo));
                    setResult(2, data);
                } else {
                    setResult(1, data);
                }
                finish();
            }
        });

        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("appInfo", JSON.toJSONString(appInfo));
                setResult(1, data);
                finish();
            }
        });
        deleteBtn.setVisibility(isEdit ? View.VISIBLE : View.GONE);

        title = findViewById(R.id.title);
        if (isEdit) {
            initAppInfo(appInfo);
            title.setText("编辑任务");
            sureBtn.setText("更新");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 1) {
            // 免费任务
            String info = data.getStringExtra("appInfo");
            appInfo = JSON.parseObject(info, AppInfo.class);
            this.initAppInfo(appInfo);
        }
    }

    private void initAppInfo(AppInfo appInfo) {
        this.taskTypeName.setText(appInfo.getName());
        findViewById(R.id.sp).setVisibility(View.VISIBLE);
        findViewById(R.id.periodLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.sleepTimeLayout).setVisibility(View.VISIBLE);
        this.periodEdit.setText(String.valueOf(appInfo.getPeriod()));
        this.periodEdit.setSelection(this.periodEdit.getText().length());
        this.sleepTimeEdit.setText(String.valueOf(appInfo.getSleepTime()));
        this.sleepTimeEdit.setSelection(this.sleepTimeEdit.getText().length());
    }
}
