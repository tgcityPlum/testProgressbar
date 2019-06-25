package com.eagersoft.youzy.testprogressbar;

import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //根控件
    private ConstraintLayout root;
    //进度控件
    private ProgressBar pbRatio;
    //最小金额控件
    private TextView tvMoneyLeft;
    //最大金额控件
    private FrameLayout fl_money;
    private TextView tvMoneyRight;
    //当前金额控件
    private TextView tvMoneyCurrent;
    //砍价最小的金额
    private int minMoney = 20;
    //砍价最大的金额
    private int maxMoney = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.root);
        pbRatio = findViewById(R.id.pb_ratio);
        tvMoneyLeft = findViewById(R.id.tv_money_left);
        tvMoneyRight = findViewById(R.id.tv_money_right);
        fl_money = findViewById(R.id.fl_money);
        //设置极值金额的数据
        setTextMoney(tvMoneyLeft, minMoney);
        setTextMoney(tvMoneyRight, maxMoney);
        //设置进度数据
        final int radio = setProgressBar(50);

        if (pbRatio.getViewTreeObserver() != null) {
            //当进度条绘制完成后的监听
            pbRatio.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    if (pbRatio != null) {
                        //绘制当前金额的位置
                        pbRatio.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        Log.e("left:" + pbRatio.getLeft() + ";right:" + pbRatio.getRight(), ";top:" + pbRatio.getTop() + ";bottom:" + pbRatio.getBottom());
                        //设置当前金额的控件
                        tvMoneyCurrent = new TextView(MainActivity.this);
                        tvMoneyCurrent.setText("已砍价¥50");
                        tvMoneyCurrent.setTextColor(Color.WHITE);
                        tvMoneyCurrent.setTextSize(14f);
                        tvMoneyCurrent.setGravity(Gravity.CENTER);
                        tvMoneyCurrent.setPadding(20, 10, 20, 10);
                        tvMoneyCurrent.setBackground(getResources().getDrawable(R.drawable.bg_e9302d));
                        //设置起点坐标  ps 116是由tvMoneyCurrent.getViewTreeObserver().addOnGlobalLayoutListener中获取的值（tvMoneyCurrent.getRight() - ）
                        int left = (pbRatio.getRight() - pbRatio.getLeft()) / 100 * radio + pbRatio.getLeft() - 116;
                        //设置view的LayoutParams
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(left, 0, 0, 0);
                        tvMoneyCurrent.setLayoutParams(layoutParams);
                        //添加控件
                        fl_money.addView(tvMoneyCurrent);

                        tvMoneyCurrent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                tvMoneyCurrent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                //绘制当前金额的位置
                                Log.e("left:" + tvMoneyCurrent.getLeft() + ";right:" + tvMoneyCurrent.getRight(), ";top:" + tvMoneyCurrent.getTop() + ";bottom:" + tvMoneyCurrent.getBottom());

                            }
                        });
                    }
                }
            });
        }

    }

    /**
     * 设置控件的金额
     */
    private void setTextMoney(TextView textView, int money) {
        SpannableString spannableString = new SpannableString(getString(R.string.money, money));
        spannableString.setSpan(new AbsoluteSizeSpan(14, true), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
    }

    /**
     * 设置进度数据
     */
    private int setProgressBar(int currentMoney) {
        int radio;
        if (currentMoney <= minMoney) {
            radio = 0;
        } else if (currentMoney >= maxMoney) {
            radio = 100;
        } else {
            radio = (int) (((float) (currentMoney - minMoney) / (maxMoney - minMoney)) * 100);
        }
        //设置动画
        ValueAnimator animator = ValueAnimator.ofInt(0, radio).setDuration(200);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pbRatio.setProgress((int) animation.getAnimatedValue());
            }
        });
        animator.start();

        return radio;
    }

}
