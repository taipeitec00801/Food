package com.example.food.Other;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.food.R;
import com.example.food.Settings.UserInformationActivity;

public class TakePhotoPopWin {
    private PopupWindow popWindow;
    private Context context;

    public void takePhotoPopWin(Context mContext,View v) {
        this.context = mContext;

        View view = LayoutInflater.from(context).inflate(R.layout.take_photo_pop, null, false);

        popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // 设置弹出窗体可点击
        popWindow.setFocusable(true);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        popWindow.setAnimationStyle(R.style.take_photo_anim);

        // 设置按钮监听
        //照相
        Button btn_take_photo =  view.findViewById(R.id.btn_take_photo);
        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("測試", "照相");
            }
        });
        //相簿
        Button btn_pick_photo =  view.findViewById(R.id.btn_pick_photo);
        btn_pick_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("測試", "相簿");

            }
        });
        // 取消按钮
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 销毁弹出框
                popWindow.dismiss();
            }
        });

        // 设置外部可点击
        popWindow.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v, -50, 0);

        // 设置弹出窗体的背景
        // 实例化一个ColorDrawable颜色为半透明
        popWindow.setBackgroundDrawable(new ColorDrawable(0xb0ffffff));
    }
}
