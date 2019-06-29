package com.litesnap.open;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.Calendar;


/**
 * Created by ZTMIDGO on 2018/2/11.
 */

public abstract class SinglePopupWindowFragment extends DialogFragment {
    public static final float DEFAULT_GRAVITY = 0.65f;
    public static final float MATCH_GRAVITY = -1;
    public static final int DEFAULT_STYLE = R.style.MyDialog;
    public static final int STYLE_NO_DIM = R.style.MyDialogNoDim;

    public abstract View getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    private boolean mIsOriginTime = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsOriginTime = Calendar.getInstance().get(Calendar.HOUR ) % 2 != 0;
        if (false){
            setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);
        }else {
            //setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return onKeyListener();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getContentView(inflater, container, savedInstanceState);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        // 这里吧原来的commit()方法换成了commitAllowingStateLoss()
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        //当软键盘遮挡EditText时，在父节点设置android:fitsSystemWindows="true"
        super.onResume();
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (mIsOriginTime){
            //window.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_dialog_material_background));
        }else {
            //window.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_white_material_background));
            //window.setWindowAnimations(R.style.Animation);
        }
        int[] widthAndHeight = AndroidSystem.getSystemWidthAndHeight(getActivity());
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


        window.setAttributes(lp);
    }


    public float getGravity(){
        return DEFAULT_GRAVITY;
    }

    public int getStyle(){
        return DEFAULT_STYLE;
    }

    public boolean onKeyListener(){
        return false;
    }

}
