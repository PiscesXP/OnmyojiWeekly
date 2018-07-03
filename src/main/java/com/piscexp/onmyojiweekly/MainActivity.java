package com.piscexp.onmyojiweekly;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.security.Permission;
import java.security.Permissions;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //button
        Button queryButton = (Button) findViewById(R.id.buttonQuery);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickQuery();
            }
        });

        //self add
        checkPermission();
    }

    /**
     * 查询。
     */
    public void clickQuery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没权限查不了  ┑(￣Д ￣)┍", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        //查找 com.netease.onmyoji.xx
        final String dataPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data";
        File dataDir = new File(dataPath);
        if (dataDir.exists()) {
            ArrayList<File> onmyojiDirList = new ArrayList<>();
            final String PackageName = "com.netease.onmyoji";  //NOT include mi oppo...
            for (File subDir : dataDir.listFiles()) {
                if (subDir.isDirectory() && subDir.getName().contains(PackageName)) {
                    //is onmyoji dir
                    onmyojiDirList.add(subDir);
                }
            }
            if (onmyojiDirList.isEmpty()) {
                //not found
                Toast.makeText(this, "未找到痒痒鼠游戏", Toast.LENGTH_LONG).show();
                return;
            }
            //查找id
            ArrayList<String> IDList = new ArrayList<>();
            for (File onmyojiDir : onmyojiDirList) {
                final String SubDirAppend = "/files/netease/onmyoji";
                File onmyojiSubDir = new File(onmyojiDir.getAbsolutePath() + SubDirAppend);
                if (onmyojiSubDir.exists() && onmyojiSubDir.isDirectory()) {
                    //search dir "chat_xxxxxx"
                    for (File dir : onmyojiSubDir.listFiles()) {
                        if (dir.isDirectory() && dir.getName().contains("chat_")) {
                            String ID = dir.getName().split("_")[1];
                            IDList.add(ID);
                        }
                    }
                }
            }
            if (IDList.isEmpty()) {
                Toast.makeText(this, "未找到任何角色", Toast.LENGTH_LONG).show();
            } else if (IDList.size() > 1) {
                String promotInfo = "找到了" + IDList.size() + "个角色，查询第一个角色...\n（多角色查询开发中...QAQ）";
                Toast.makeText(this, promotInfo, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "正在打开周报...", Toast.LENGTH_LONG).show();
            }
            //open web
            String firstURL = "http://yxzs.163.com/yys/weekly/index.html?roleInfo=60__" + IDList.get(0);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(firstURL);
            intent.setData(content_url);
            startActivity(intent);
        } else {
            Toast.makeText(this, "未找到data文件夹.", Toast.LENGTH_LONG).show();

        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没权限
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("");
            builder.setMessage("需要获取读取权限");
            builder.setPositiveButton("好", null);
            builder.show();
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "没权限查不了  ┑(￣Д ￣)┍", Toast.LENGTH_LONG).show();
//            }
        }
    }

    //-----------------------------------------------
    //auto generated
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
        getMenuInflater().inflate(R.menu.main, menu);
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
