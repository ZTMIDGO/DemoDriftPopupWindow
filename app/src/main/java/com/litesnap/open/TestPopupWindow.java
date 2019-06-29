package com.litesnap.open;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.litesnap.open.open.popup.view.MyCoordinatorLayout;

public class TestPopupWindow extends SinglePopupWindowFragment {
    public static final String TAG = "TestPopupWindow";
    private MyCoordinatorLayout mCoord;
    public static TestPopupWindow newInstance() {

        Bundle args = new Bundle();

        TestPopupWindow fragment = new TestPopupWindow();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        mCoord = view.findViewById(R.id.parent);

        mCoord.setLayoutListener(new MyCoordinatorLayout.OnLayoutChangeListener() {
            @Override
            public void onClose() {
                dismiss();
            }
        });
        return view;
    }


}
