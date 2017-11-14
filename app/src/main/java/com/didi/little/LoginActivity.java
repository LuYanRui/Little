package com.didi.little;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity {

    // 请求读取联系人权限返回值
    private static final int REQUEST_READ_CONTACTS = 0;
    // 异步请求
    private UserLoginTask mAuthTask = null;
    // 用户名组件
    private AutoCompleteTextView mEmailView;
    // 密码组件
    private EditText mPasswordView;
    // 加载进度
    private View mProgressView;
    // 登陆view
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 判断是否自动登陆
        if(isAutoLogin()){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }else{
            // 设置登陆组件
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
            populateAutoComplete();

            mPasswordView = (EditText) findViewById(R.id.password);

            // 输入动作监听
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            // 输入动作监听
            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }

    }
    // 判断是否自动登陆
    private boolean isAutoLogin(){
        SharedPreferences sharep=getPreferences(Activity.MODE_PRIVATE);

        // 不做自动登陆操作
        if(getIntent().getBooleanExtra("exit",false)){
            SharedPreferences.Editor edit=sharep.edit();
            edit.putString("time","notime");
            edit.apply();
            return false;
        }else {
            //用户点击退出
            if(sharep.getString("time","notime").equals("notime")){
                SharedPreferences.Editor edit=sharep.edit();
                edit.putString("time",Utils.getCurtime());
                edit.apply();
                return false;
            }else{
                // 超过自动登陆时间
                if(Utils.isTimeOut(sharep.getString("time","notime"))){
                    return false;
                }else{
                    return true;
                }
            }
        }

    }
    // 是否获取到权限
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
    }
    // 通过不同的版本判断是否应该动态获取权限，android6.0以上动态获取
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        // 提示用户需要开启权限
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS,WRITE_EXTERNAL_STORAGE}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS,WRITE_EXTERNAL_STORAGE}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    // 请求权限的返回结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    // 验证登陆
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // 判断用户名密码是否为空
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(email)){
            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.error_field_required));
            }

            if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
            }
        }else {
            // 用户名密码正确执行登陆
            if(email.equals("1234") && password.equals("1234")){
                showProgress(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            }else if(!email.equals("1234")){
                mEmailView.setError(getString(R.string.error_invalid_email));
            }else if(!password.equals("1234")){
                mPasswordView.setError(getString(R.string.error_incorrect_password));
            }else{
                mEmailView.setError(getString(R.string.error_invalid_email));
                mPasswordView.setError(getString(R.string.error_incorrect_password));
            }
        }
    }

    // 显示登陆进度
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // 判断当前sdk版本下ViewPropertyAnimator是否可用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // 当前版本下不可用时不显示动画
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // 异步执行登陆操作，不可在主线程执行耗时操作，否则容易引起anr
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // 模拟网络延迟
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }

        // 主线程
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

