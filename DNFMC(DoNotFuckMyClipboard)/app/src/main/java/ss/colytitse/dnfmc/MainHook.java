package ss.colytitse.dnfmc;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.List;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import ss.colytitse.dnfmc.app.AppSettings;
import ss.colytitse.dnfmc.fuck.StartFuck;

public class MainHook implements IXposedHookLoadPackage{

    private static final String TAG = "test_";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) return;
        AppSettings.AppConfigs appConfigs = new AppSettings.AppConfigs(lpparam.packageName);

        Log.d(TAG, "软件？ " + lpparam.packageName);
        Log.d(TAG, "规则？ " + appConfigs.getAppConfigText());
        Log.d(TAG, "开启？ " + appConfigs.getSetSwitch());
        Log.d(TAG, "编辑？ " + appConfigs.getEditState());

        if (!appConfigs.getSetSwitch()) return;

        try {
            handleLoadStart(lpparam, appConfigs);
            XposedBridge.log("DoNotFuckMyClipboard is run.");
        } catch (Throwable t) {
            XposedBridge.log("DNFMC_ERR: " + t);
        }

    }

    private void handleLoadStart(XC_LoadPackage.LoadPackageParam lpparam, AppSettings.AppConfigs appConfigs) {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    Context mContext = (Context) param.args[0];
                    ClassLoader classLoader = mContext.getClassLoader();
                    StartFuck startfuck = new StartFuck(lpparam.packageName, classLoader) {
                        @Override
                        public boolean isRegulation(String inText) {
                            if (!appConfigs.getEditState()) return true;
                            List<String> appConfigText = appConfigs.getAppConfigText();
                            if (appConfigText == null) return false;
                            Log.d(TAG, "输入了: " + inText);
                            for (String reg : appConfigText) {
                                if (reg.trim().length() < 1) continue;
                                Log.d(TAG, reg + " 匹配: " + inText.matches(reg));
                                if (inText.contains(reg) || inText.matches(reg)) return true;
                            }
                            return false;
                        }
                    };
                    startfuck.setFuckerWorking();
                }
            }
        );
    }
}
