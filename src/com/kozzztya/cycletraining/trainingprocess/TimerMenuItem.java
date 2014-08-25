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

    private CountDownTimer timer;
    private boolean isTimerStarted;
    private final long SECOND = 1000;

    private Context context;
    private ImageView imageViewTimer;
    private TextView textViewTimer;

    public TimerMenuItem(Context context, Menu menu) {
        this.context = context;
        isTimerStarted = false;

        MenuItem menuItem = menu.findItem(R.id.action_timer);
        View actionView = MenuItemCompat.getActionView(menuItem);
        actionView.setOnClickListener(this);

        imageViewTimer = (ImageView) actionView.findViewById(R.id.imageViewTimer);
        textViewTimer = (TextView) actionView.findViewById(R.id.textViewTimer);
    }

    public void configure(int startTime, final boolean isVibrate) {
        textViewTimer.setText(String.valueOf(startTime));

        timer = new CountDownTimer(startTime * SECOND, SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText(String.valueOf(millisUntilFinished / SECOND));
            }

            @Override
            public void onFinish() {
                timerStop();
                if (isVibrate) {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(SECOND);
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (!isTimerStarted) {
            timerStart();
        } else {
            timerStop();
        }
    }

    public void timerStart() {
        imageViewTimer.setVisibility(View.GONE);
        textViewTimer.setVisibility(View.VISIBLE);
        isTimerStarted = true;
        timer.start();
    }

    public void timerStop() {
        imageViewTimer.setVisibility(View.VISIBLE);
        textViewTimer.setVisibility(View.GONE);
        isTimerStarted = false;
        timer.cancel();
    }
}
