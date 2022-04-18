package ss.colytitse.dnfmc.app;

import static ss.colytitse.dnfmc.app.AppSettings.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ss.colytitse.dnfmc.R;

public class AppInfoAdapter extends BaseAdapter {

    private final List<PackageInfo> packageInfo;
    private final Context context;
    public AppInfoAdapter(Context context, List<PackageInfo> packageInfo) {
        this.context = context;
        List<PackageInfo> onSwitch, offSwitch, allSwitchStatus;
        allSwitchStatus = new ArrayList<>();
        onSwitch = new ArrayList<>();
        offSwitch = new ArrayList<>();
        for (PackageInfo info: packageInfo){
            if(getSetSwitch(context, info.packageName))
                onSwitch.add(info);
            else offSwitch.add(info);
        }
        allSwitchStatus.addAll(onSwitch);
        allSwitchStatus.addAll(offSwitch);
        this.packageInfo = allSwitchStatus;
    }

    @Override
    public int getCount() {
        return packageInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override @SuppressLint({"ViewHolder", "SetTextI18n","UseSwitchCompatOrMaterialCode"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        TextView app_name = view.findViewById(R.id.app_name);
        TextView app_pkgn = view.findViewById(R.id.app_pkgn);
        TextView app_version = view.findViewById(R.id.app_version);
        ImageView app_icon = view.findViewById(R.id.app_icon);
        Switch onSwitch = view.findViewById(R.id.set_switch);
        AppInfo appInfo = new AppInfo(packageInfo.get(i), context);
        app_version.setText(appInfo.getVersionName() + " (" + appInfo.getVersionCode() + ")");
        onSwitch.setChecked(getSetSwitch(context, appInfo.getPackageName()));
        app_icon.setImageDrawable(appInfo.getAppIcon());
        app_pkgn.setText(appInfo.getPackageName());
        app_name.setText(appInfo.getAppName());
        onSwitch.setClickable(false);
        return view;
    }

    public static class AppInfo {

        private final PackageInfo packageInfo;
        private final Context context;

        public AppInfo(PackageInfo packageInfo, Context context) {
            this.packageInfo = packageInfo;
            this.context = context;
        }

        public String getPackageName(){
            return packageInfo.packageName;
        }

        public String getAppName(){
            return packageInfo.applicationInfo
                    .loadLabel(context.getPackageManager()).toString();
        }

        public String getVersionName(){
            return packageInfo.versionName;
        }

        public int getVersionCode(){
            return packageInfo.versionCode;
        }

        public Drawable getAppIcon(){
            return packageInfo.applicationInfo.loadIcon(context.getPackageManager());
        }
    }
}
