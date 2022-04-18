package ss.colytitse.dnfmc.fuck;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public abstract class StartFuck {

    public abstract boolean isRegulation(String inText);
    private final ClassLoader classLoader;
    private final String packageName;

    public StartFuck(String packageName, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.packageName = packageName;
    }

    public void setFuckerWorking(){

        XC_MethodHook newPlainText, setText, setPrimaryClip;

        newPlainText = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                String inText = (param.args[0]).toString().trim();
                if (inText.length() < 1 || !isRegulation(inText)) return;
                param.args[0] = "";
                param.setResult(null);
                XposedBridge.log(packageName + ": " + inText);
            }
        };

        setText = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                String inText = (param.args[0]).toString().trim();
                if (inText.length() < 1 || !isRegulation(inText)) return;
                // Log.d("test_fuck", "粘贴板读取内容测试: " + inText);
                param.args[0] = "";
                param.setResult(null);
                XposedBridge.log(packageName + ": " + inText);
            }
        };

        setPrimaryClip = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                ClipData clipData = (ClipData) param.args[0];
                String inText = clipData.getItemAt(0).getText().toString().trim();
                if (inText.length() < 1 || !isRegulation(inText)) return;
                param.args[0] = ClipData.newPlainText("","");
            }
        };

        XposedHelpers.findAndHookMethod(
                XposedHelpers.findClass("android.content.ClipData", classLoader),
                "newPlainText", CharSequence.class, CharSequence.class, newPlainText );
        XposedHelpers.findAndHookMethod(
                XposedHelpers.findClass("android.content.ClipboardManager", classLoader),
                "setText", CharSequence.class, setText);
        XposedHelpers.findAndHookMethod(ClipboardManager.class, "setPrimaryClip",
                ClipData.class, setPrimaryClip);
    }

}
