package com.offlinepaymentmethods.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.offlinepaymentmethods.R;
import com.offlinepaymentmethods.controller.base.BaseActivity;
import com.offlinepaymentmethods.controller.fragment.ClientFragment;
import com.offlinepaymentmethods.controller.fragment.MerchantFragment;
import com.offlinepaymentmethods.interfaces.OnActivityResultListener;
import com.offlinepaymentmethods.interfaces.OnClickListener;

import butterknife.InjectView;
import me.ydcool.lib.qrmodule.activity.QrScannerActivity;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnClickListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;

    @InjectView(R.id.nav_view)
    NavigationView navigationView;

    private OnActivityResultListener activityResultListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        inflateFragment(new ClientFragment(), Boolean.FALSE);

    }

    @Override
    public void initLayout() {
        setContentView(R.layout.activity_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.merchant) {
            inflateFragment(new MerchantFragment(), Boolean.TRUE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QrScannerActivity.QR_REQUEST_CODE) {
            Log.d("QR-MODULE", resultCode == RESULT_OK
                    ? data.getExtras().getString(QrScannerActivity.QR_RESULT_STR)
                    : "Scanned Nothing!");
            if(activityResultListener != null && resultCode == RESULT_OK) {
                activityResultListener.onActivityResult(data.getExtras().getString(QrScannerActivity.QR_RESULT_STR));
            }
        }
    }

    private void inflateFragment(Fragment newFragment, Boolean addToBackStack) {
//        Bundle arguments = new Bundle();
//        newFragment.setArguments(arguments);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_content_home, newFragment);
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void openScannerActivity(OnActivityResultListener activityResultListener) {
        if (activityResultListener != null)
            this.activityResultListener = activityResultListener;
        Intent intent = new Intent(getApplicationContext(), QrScannerActivity.class);
        startActivityForResult(intent, QrScannerActivity.QR_REQUEST_CODE);
    }
}
