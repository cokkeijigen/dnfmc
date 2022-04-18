package ss.colytitse.dnfmc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.annotation.Nullable;
import android.annotation.SuppressLint;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import static ss.colytitse.dnfmc.app.AppSettings.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import ss.colytitse.dnfmc.app.ContainsActivity;
import ss.colytitse.dnfmc.app.AppInfoAdapter;
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test_";
    public List<PackageInfo> packageInfo;
    public final Context mContext = this;

    @Override @SuppressLint("SetTextI18n")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageInfo = getPackageManager().getInstalledPackages(0);
        setContentView(R.layout.main_layout);
        setActivityStatusBar(this);
        initMainActivityListView(packageInfo);
        TextView setVersionName = findViewById(R.id.version_name);
        setVersionName.setText("当前版本：" + getVersionName(mContext));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(canReShowView(mContext)){
            initMainActivityListView(packageInfo);
            setReShowView(mContext,false);
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private void initMainActivityListView(List<PackageInfo> packageInfo) {
        ListView listView = findViewById(R.id.app_items);
        listView.setAdapter(new AppInfoAdapter(mContext, packageInfo));
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            TextView app_pkgn = view.findViewById(R.id.app_pkgn);
            String text = app_pkgn.getText().toString();
            Switch onSwitch = view.findViewById(R.id.set_switch);
            if (!onSwitch.isChecked()) {
                onSwitch.setChecked(true);
                putOnSwitch(mContext, text);
            } else {
                onSwitch.setChecked(false);
                putOffSwitch(mContext, text);
            }
            Log.d(TAG , "点击了第" + i + "个项目");
        });
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            TextView app_pkgn = view.findViewById(R.id.app_pkgn);
            String text = app_pkgn.getText().toString();
            Switch onSwitch = view.findViewById(R.id.set_switch);
            if (!onSwitch.isChecked()) {
                onSwitch.setChecked(true);
                putOnSwitch(mContext, text);
            } else {
                onSwitch.setChecked(false);
                putOffSwitch(mContext, text);
            }
        });
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent();
            TextView app_pkgn = view.findViewById(R.id.app_pkgn);
            TextView app_name = view.findViewById(R.id.app_name);
            String name = app_name.getText().toString();
            String pkgn = app_pkgn.getText().toString();
            intent.putExtra("app_pkgn", pkgn);
            intent.putExtra("app_name", name);
            intent.setClass(mContext, ContainsActivity.class);
            mContext.startActivity(intent);
            return false;
        });
    }

    public void showMenu(View view) {
        Log.d("test_","点击了菜单");
        View main_menu = findViewById(R.id.main_menu);
        Toast.makeText(this,"啥也没有", Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"By iTsukezigen", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint({"WrongViewCast", "UseCompatLoadingForDrawables"})
    public void insearch(View view) {
        EditText edit_insearch = findViewById(R.id.edit_insearch);
        ImageButton btn_insearch = findViewById(R.id.btn_insearch);
        if(edit_insearch.getVisibility() == View.GONE){
            edit_insearch.setVisibility(View.VISIBLE);
            btn_insearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24, getTheme()));
            edit_insearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(view.getWindowToken(), 0);
                    String intext = textView.getText().toString();
                    if (intext.length() > 0)
                        initMainActivityListView(searchAppView(intext));
                    return true;
                }
                return false;
            });
        }else {
            edit_insearch.setVisibility(View.GONE);
            initMainActivityListView(packageInfo);
            edit_insearch.setText("");
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
            btn_insearch.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_search_24, getTheme()));
        }
    }

    private List<PackageInfo> searchAppView(String text) {
        List<PackageInfo> result = new ArrayList<>();
        for (PackageInfo info : packageInfo){
            if (info.packageName.contains(text) || info.applicationInfo.loadLabel(mContext.getPackageManager()).toString().contains(text))
                result.add(info);
        }
        return result;
    }
}
