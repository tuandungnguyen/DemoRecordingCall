package com.imtuandung.demorecordingcall;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnHideApp;
    TextView btnUninstallApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHideApp = (Button) findViewById(R.id.btnHideApp);
        btnUninstallApp = (TextView) findViewById(R.id.tvUninstall);

        btnHideApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideApp();
            }
        });

        btnUninstallApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uninstallApp();
            }
        });
    }

    /**
     * Hide app function, remove app icon from launcher, remove from recent apps
     */
    private void hideApp() {
        try {
            ComponentName componentToDisable = new ComponentName(
                    getApplicationContext().getPackageName(), MainActivity.class.getName());
            PackageManager p = getApplicationContext().getPackageManager();
            p.setComponentEnabledSetting(componentToDisable,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

        } catch (Exception e) {
            Log.e("Show app fail", e.getMessage());
        }
    }

    /**
     * Penalty remove app programmatically
     */
    private void uninstallApp() {
        try {
            Uri packageURI = Uri.parse("package:" + getPackageName());
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                    packageURI);
            startActivity(uninstallIntent);
        } catch (Exception e) {
            Log.e("Uninstall app error ", e.getMessage());
        }
    }
}
