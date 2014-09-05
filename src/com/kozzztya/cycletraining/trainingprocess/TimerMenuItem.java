package com.kozzztya.cycletraining.trainingprocess;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kozzztya.cycletraining.R;

/**
 * Custom MenuItem which shows countdown time by clicking on the icon
 */

public class TimerMenuItem implements View.OnClickListener {

    private final long SECOND = 1000;

    private CountDownTimer mTimer;
    private boolean mIsTimerStarted;

    private Context mContext;
    private ImageView mImageViewTimer;
    private TextView mTextViewTimer;

    public TimerMenuItem(Context context, Menu menu) {
        mContext = context;
        mIsTimerStarted = false;

        MenuItem menuItem = menu.findItem(R.id.action_timer);
        View actionView = MenuItemCompat.getActionView(menuItem);
        actionView.setOnClickListener(this);

        mImageViewTimer = (ImageView) actionView.findViewById(R.id.imageViewTimer);
        mTextViewTimer = (TextView) actionView.findViewById(R.id.textViewTimer);
    }

    public void configure(int startTime, final boolean isVibrate) {
        mTextViewTimer.setText(String.valueOf(startTime));

        mTimer = new CountDownTimer(startTime * SECOND, SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTextViewTimer.setText(String.valueOf(millisUntilFinished / SECOND));
            }

            @Override
            public void onFinish() {
                timerStop();
                if (isVibrate) {
                    Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(SECOND);
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (!mIsTimerStarted) {
            timerStart();
        } else {
            timerStop();
        }
    }

    public void timerStart() {
        mImageViewTimer.setVisibility(View.GONE);
        mTextViewTimer.setVisibility(View.VISIBLE);
        mIsTimerStarted = true;
        mTimer.start();
    }

    public void timerStop() {
        mImageViewTimer.setVisibility(View.VISIBLE);
        mTextViewTimer.setVisibility(View.GONE);
        mIsTimerStarted = false;
        mTimer.cancel();
    }
}
