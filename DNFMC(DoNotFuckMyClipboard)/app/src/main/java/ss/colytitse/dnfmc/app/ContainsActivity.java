package ss.colytitse.dnfmc.app;

import static ss.colytitse.dnfmc.app.AppSettings.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ss.colytitse.dnfmc.R;
@SuppressLint("UseSwitchCompatOrMaterialCode")
public class ContainsActivity extends AppCompatActivity {

    private static final String TAG = "test_";
    private final Context mContext = this;
    private EditText containerText;
    private EditText containerMin;
    private EditText containerMax;
    private LinearLayout edit_view;
    private String packageName;
    private Button cleanButton;
    private Switch controlSwitch;
    private Switch onSwitch;
    private String appName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_contains);
        setActivityStatusBar(this);
        edit_view = findViewById(R.id.content_conf_edit);
        containerText = findViewById(R.id.container_text);
        cleanButton = findViewById(R.id.con_clean_btn);
        onSwitch = findViewById(R.id.con_onswitch);
        controlSwitch = findViewById(R.id.ctrl_sw);
        Bundle extras = getIntent().getExtras();
        packageName = extras.getString("app_pkgn");
        appName = extras.getString("app_name");
        if(checkControlSwitch()) initActivity();
        initActionBar();
    }

    private boolean checkControlSwitch() {
        controlSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked){
                edit_view.setVisibility(View.GONE);
                setEditState(mContext, packageName, false);
            }else {
                edit_view.setVisibility(View.VISIBLE);
                setEditState(mContext, packageName, true);
                initActivity();
            }
        });
        if (getEditState(mContext, packageName)) return true;
        edit_view.setVisibility(View.GONE);
        controlSwitch.setChecked(true);
        return false;
    }

    private void initActivity() {
        String conf = getOnContainsConf(mContext, packageName);
        if (!conf.equals("")) containerText.setText(conf);
        cleanButton.setOnClickListener(view -> {
            putOnContains(mContext, packageName, "",0, 0);
            containerText.setText("");
        });
    }

    private void initActionBar() {
        Drawable app_icon = getAppIcon(packageName);
        TextView name = findViewById(R.id.con_app_name);
        TextView pkgn = findViewById(R.id.con_app_pkgn);
        ImageView icon = findViewById(R.id.con_app_icon);
        if (app_icon != null) icon.setImageDrawable(app_icon);
        if (!packageName.equals("")) pkgn.setText(packageName);
        if (!appName.equals("")) name.setText(appName);
        onSwitch.setChecked(getSetSwitch(mContext, packageName));
    }

    @Nullable
    private Drawable getAppIcon(String pkgName) {
        try {
            PackageManager pm = this.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
            return info.loadIcon(pm);
        } catch (Throwable t) {
            Log.d(TAG, "获取失败: " + t);
            return null;
        }
    }

    private void saveData(){
        if(onSwitch.isChecked() != getSetSwitch(mContext, packageName))
            setReShowView(mContext, true);
        String conf = containerText.getText().toString();
        putOnContains(mContext, packageName, conf, -1, -1);
        if(onSwitch.isChecked()) putOnSwitch(mContext, packageName);
        else putOffSwitch(mContext, packageName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }
}
