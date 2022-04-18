package ss.colytitse.dnfmc.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import de.robv.android.xposed.XSharedPreferences;
import ss.colytitse.dnfmc.BuildConfig;
import ss.colytitse.dnfmc.R;

@SuppressLint({"SetWorldReadable", "WorldReadableFiles","ApplySharedPref"})
@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class AppSettings {

    private static final String SaveDataFile = "config";

    public static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(SaveDataFile, Context.MODE_PRIVATE);
    }


    public static void putOnSwitch(Context context, String packageName){
        getPrefs(context).edit().putBoolean(packageName + "_setSwitch", true).commit();
        setReadable(context);
    }

    public static void putOnContains(Context context, String packageName, String conf, int min, int max){
        SharedPreferences.Editor edit = getPrefs(context).edit();
        edit.putString(packageName + "_conf", conf).commit();
        if (min != -1 && max != -1) edit
                .putInt(packageName + "_min", min)
                .putInt(packageName + "_max", max)
                .commit();
        setReadable(context);
    }

    public static void putOffSwitch(Context context, String packageName){
        getPrefs(context).edit().remove(packageName + "_setSwitch").commit();
        setReadable(context);
    }

    public static boolean getSetSwitch(Context context, String packageName) {
        return getPrefs(context).getBoolean(packageName + "_setSwitch", false);
    }

    public static void setReShowView(Context context, boolean is){
        getPrefs(context).edit().putBoolean("reShowView", is).commit();
    }

    public static boolean canReShowView(Context context){
        return getPrefs(context).getBoolean("reShowView",false);
    }

    public static String getOnContainsConf(Context context, String packageName){
        return getPrefs(context).getString(packageName + "_conf", "");
    }

    public static int getOnContainsMin(Context context, String packageName){
        return getPrefs(context).getInt(packageName + "_min", 0);
    }

    public static int getOnContainsMax(Context context, String packageName){
        return getPrefs(context).getInt(packageName + "_max", 0);
    }

    public static void setEditState(Context context, String packageName, boolean state){
        getPrefs(context).edit().putBoolean(packageName + "_editstate", state).commit();
        setReadable(context);
    }

    public static boolean getEditState(Context context, String packageName){
        return getPrefs(context).getBoolean(packageName + "_editstate", true);
    }

    public static void setReadable(Context context) {
        File dataDir = new File(context.getApplicationInfo().dataDir);
        File prefsDir = new File(dataDir, "shared_prefs");
        File prefsFile = new File(prefsDir, SaveDataFile + ".xml");
        if (prefsFile.exists()) {
            for (File file : new File[]{dataDir, prefsDir, prefsFile}) {
                file.setReadable(true, false);
                file.setExecutable(true, false);
            }
        }
    }

    public static void setStatusBarColor(Activity activity, int color){
        activity.getWindow().setStatusBarColor(
                ContextCompat.getColor(activity.getApplicationContext(), color));
    }

    public static void setActivityStatusBar(Activity activity) {
        setStatusBarColor(activity, R.color.toolbar);
        if(activity.getApplicationContext().getResources().getConfiguration().uiMode == 0x11)
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class AppConfigs{

        private String packageName;
        private final XSharedPreferences file;

        public AppConfigs(){
            this.file = new XSharedPreferences(BuildConfig.APPLICATION_ID, SaveDataFile);
        }

        public AppConfigs(String packageName) {
            this();
            this.packageName = packageName;
        }

        public XSharedPreferences getObject(){
            return file;
        }

        public boolean getSetSwitch() {
            return file.getBoolean(packageName + "_setSwitch", false);
        }

        public boolean getEditState(){
            return file.getBoolean(packageName + "_editstate", true);
        }

        public  List<String> getAppConfigText(){
            String data = file.getString(packageName + "_conf", "");
            return !data.equals("") ? Arrays.asList(data.split("\n")) : null;
        }

    }

}
