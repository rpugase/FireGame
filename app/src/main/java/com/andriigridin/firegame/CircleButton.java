package com.andriigridin.firegame;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Random;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MASK;
import static android.view.MotionEvent.ACTION_MOVE;

public class CircleButton extends FloatingActionButton implements View.OnTouchListener {

    private int buttonSize;
    private int dX;
    private int dY;
    private OnMoveEventListener onMoveEventListener;

    public CircleButton(Context context) {
        super(context);
    }

    public void setOnMoveEventListener(OnMoveEventListener onMoveEventListener) {
        this.onMoveEventListener = onMoveEventListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        buttonSize = (int) getContext().getResources().getDimension(R.dimen.buttonSize);

        final Random random = new Random();
        final int color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        setBackgroundTintList(ColorStateList.valueOf(color));

        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = buttonSize;
        layoutParams.height = buttonSize;
        setLayoutParams(layoutParams);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getRawX();
        final int y = (int) event.getRawY();

        switch (event.getAction() & ACTION_MASK) {
            case ACTION_DOWN:
                final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
                dX = x - layoutParams.leftMargin;
                dY = y - layoutParams.topMargin;
                break;
            case ACTION_MOVE:
                onMoveEventListener.onMoveEvent(x - dX, y - dY);
                moveButton(x - dX, y - dY);
                break;
        }

        return true;
    }

    public void moveButton(int x, int y) {
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.leftMargin = x;
        layoutParams.topMargin = y;
        layoutParams.bottomMargin = buttonSize;
        layoutParams.rightMargin = buttonSize;
        setLayoutParams(layoutParams);
    }

    public void identifyButton() {
        setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher_round));
        setOnTouchListener(this);
    }

    interface OnMoveEventListener {
        void onMoveEvent(int x, int y);
    }
}
