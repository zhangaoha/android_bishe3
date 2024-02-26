package com.example.login.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.activity.AboutAppActivity;
import com.example.login.LoginActivity;
import com.example.login.UpdataPwdActivity;
import com.example.login.R;
import com.example.login.Utils.UserInfo;
import com.example.login.db.UserDbHelper;

import es.dmoral.toasty.Toasty;

public class MineFragment extends Fragment {


    private EditText tv_username, tv_nickname;
    private View root;
    private RelativeLayout modifyPasswordLayout, about_app, cancelAccountLayout;  // 声明成员变量
    private TextView logOut;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_mine, container, false);
        }

        // 初始化控件
        tv_username = root.findViewById(R.id.tv_username1);
        tv_nickname = root.findViewById(R.id.tv_nickname1);
        modifyPasswordLayout = root.findViewById(R.id.modifyPassword);  // 初始化成员变量
        logOut = root.findViewById(R.id.logOut);
        cancelAccountLayout = root.findViewById(R.id.cancel_account);
        about_app = root.findViewById(R.id.about_app);
        // 在这里可以进行其他初始化操作
        //设置注销账号cancel_account的点击事件
        cancelAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 弹出确认注销的对话框
                showCancelConfirmationDialog();

            }

        });
        //跳转到about_app界面
        about_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutAppActivity.class);
                startActivity(intent);
            }
        });


        //修改密码，启动修改密码界面activity
        root.findViewById(R.id.modifyPassword).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UpdataPwdActivity.class);
            startForResultLauncher.launch(intent);
        });




        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出确认对话框
                showLogoutConfirmationDialog();
            }
        });
        return root;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("确认退出登录");
        builder.setMessage("确定要退出登录吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 处理退出登录逻辑
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

                // 清理操作（如果有的话）

                // 关闭当前活动
//                if (getActivity() != null) {
//                    getActivity().finish();
//                }

                // 关闭整个应用程序（如果需要的话）
                // finishAffinity();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("注销账号")
                .setMessage("确定要注销账号吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 确定注销账号
                        deleteCurrentUserAccount();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 取消注销账号，关闭对话框
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private ActivityResultLauncher<Intent> startForResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // 处理从 UpdataPwdActivity 返回的结果
                    if (data != null && data.getIntExtra("resultCode", 0) == 1000) {
                        // 密码修改成功
                        // 处理密码修改成功后的操作，例如重新登录
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);

                        getActivity().finish();
                    }
                }
            }
    );
    private void deleteCurrentUserAccount() {
        // 获取当前登录用户的用户名
        String currentUsername = UserInfo.sUserInfo.getUsername();

        // 删除用户账号
        int deleteResult = UserDbHelper.getInstance(getActivity()).delete(currentUsername);

        if (deleteResult > 0) {
            // 删除成功
            // 清除用户信息
            UserInfo.sUserInfo = null;
            UserInfo.setUserInfo(null);

            // 退出登录
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
//            getActivity().finish();
        } else {
            // 删除失败，处理异常情况
            // 可以弹出提示或者记录日志等
            Toasty.error(getActivity(), "账号删除失败", Toast.LENGTH_SHORT).show();
        }
    }
}
