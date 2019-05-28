package com.xk.limitapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author xuekai1
 * @date 2019-05-28
 */
public class LimitUtils {

    private static String host = "https://qim10ic7.api.lncld.net/1.1/classes/";
    private static String id = "Qim10Ic7wlTmTeM5Y26bsau3-gzGzoHsz";
    private static String key = "QUGwR69MfPTa82hHEkM1984I";

    public static void limit(final String packageName, final Activity context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isLimit;
                isLimit = getFromSp(context);
                if (!isLimit) {
                    //sp中返回了不限制，可以直接return了。
                    return;
                }
                isLimit = getStateFromNet(packageName, context);
                if (!isLimit) {
                    //通过网络返回了不限制，可以直接return了。
                    return;
                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWaring(context);
                    }
                });
            }
        }).start();

    }

    private static void showWaring(final Activity context) {
        AlertDialog show = new AlertDialog
                .Builder(context)
                .setTitle("提示").
                        setMessage("试用版暂不可使用，请使用正版")
                .show();
        show.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                context.finish();
            }
        });
        show.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                context.finish();
            }
        });
    }

    /**
     * 0.禁用
     * 1.可用
     * 2.给sp中保存可用标记
     *
     * @return isLimit
     */
    private static boolean getStateFromNet(String packageName, Activity context) {
        int state;

        HttpURLConnection conn = null;
        try {
            // 利用string url构建URL对象
            URL mURL = new URL(host + "LimitingApp?where={\"PackageName\":\"" + packageName + "\"}");

            conn = (HttpURLConnection) mURL.openConnection();

            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("X-LC-Id", id);
            conn.setRequestProperty("X-LC-Key", key);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String reponse = sb.toString();

                JSONObject jsonObject = new JSONObject(reponse);
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 0) {
                    JSONObject o = (JSONObject) results.get(0);
                    Object limiteType = o.get("limitType");
                    state = (int) limiteType;
                } else {
                    state = 0;
                }
            } else {
                state = 0;
            }

        } catch (Exception e) {
            state = 0;
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        if (state == 0) {
            return true;
        } else if (state == 1) {
            return false;
        } else if (state == 2) {
            SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            config.edit().putBoolean("isLimit", false).apply();
            return false;
        } else {
            return true;
        }
    }

    private static boolean getFromSp(Activity context) {
        SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return config.getBoolean("isLimit", true);
    }

    //根据字节数组构建UTF-8字符串
    private String getStringByBytes(byte[] bytes) {
        String str = "";
        try {
            str = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
}
