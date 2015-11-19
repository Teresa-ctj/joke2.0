package com.android.tmall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tmall.util.MyJsonObjectRequest;
import com.android.tmall.util.TmallUtil;
import com.android.tmall.util.VolleyUtil;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 注册 Activity
 */
public class RegisterActivity extends Activity {
    @InjectView(R.id.txtUsername)
    EditText txtUsername;
    @InjectView(R.id.txtPassword)
    EditText txtPassword;
    @InjectView(R.id.txtRePassword)
    EditText txtRePassword;
    @InjectView(R.id.txtEmail)
    EditText txtEmail;
    @InjectView(R.id.tvProtocol)
    TextView tvProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.inject(this);

        String protocol = "<font color=" + "\"" + "#AAAAAA" + "\">" + "点击上面的"
                + "\"" + "注册" + "\"" + "按钮,即表示你同意" + "</font>" + "<u>"
                + "<font color=" + "\"" + "#576B95" + "\">" + "《天猫软件许可及服务协议》"
                + "</font>" + "</u>";

        tvProtocol.setText(Html.fromHtml(protocol));
    }

    @OnClick(R.id.btn_register)
    public void registerClick() {
        String password = txtPassword.getText().toString();
        String rePassword = txtRePassword.getText().toString();

        if (!password.equals(rePassword)) {
            TmallUtil.toast(this, R.string.password_error);
            return;
        }

        // 表单数据
        Map<String, String> params = new HashMap<>();
        params.put("username", txtUsername.getText().toString());
        params.put("password", password);
        params.put("email", txtEmail.getText().toString());

        // 自定义Json对象请求类(请求方式,URL,表单参数,响应成功后的处理类,响应错误后的处理类)
        MyJsonObjectRequest request = new MyJsonObjectRequest(
                Request.Method.POST,
                VolleyUtil.getAbsoluteUrl("UserServletJson"),
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jo) {
                        try {
                            if ("success".equals(jo.getString("flag"))) {
                                // 注册成功后跳转至登录
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            } else if ("error".equals(jo.getString("flag"))) {
                                // 注册失败
                                TmallUtil.toast(getApplicationContext(), R.string.register_error);
                            } else if ("exist".equals(jo.getString("flag"))) {
                                // 用户名已存在
                                TmallUtil.toast(getApplicationContext(), R.string.register_exist);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        TmallUtil.toast(getApplicationContext(), R.string.net_error);
                    }
                }
        );
        VolleyUtil.getInstance(this).addToRequestQueue(request, "register_req");
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyUtil.getInstance(this).cancelRequests("register_req");
    }
}
