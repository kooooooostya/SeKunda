package com.example.sekunda;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.sekunda.Data.Business;

public class TimerService extends IntentService {


    public static final String NAME = "TimerService";

    private static final String EXTRA_SECONDS = "EXTRA_SECONDS";
    private static final String EXTRA_IS_GOING = "EXTRA_IS_GOING";

    private int mSeconds;
    private boolean isTimerGoing;
    private SecFragment mSecFragment;
    private Business mBusiness;
    private boolean isAppBeenClosed = false;

    private Binder mBinder = new TimerBinder();

    //TODO доделать
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TimerService(String name, SecFragment fragment) {
        super(name);
        mSecFragment = fragment;
    }


    public static void startAction(Context context, int seconds) {
        Intent intent = new Intent(context, TimerService.class);
        intent.putExtra(EXTRA_SECONDS, seconds);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        if ((flags & START_FLAG_RETRY) == 0) {
            // Если это повторный запуск, выполнить какие-то действия.
            if(intent != null) {
                mSeconds = intent.getIntExtra(EXTRA_SECONDS,0);
                isTimerGoing = intent.getBooleanExtra(EXTRA_IS_GOING, false);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//        if(intent != null) {
//            mSeconds = intent.getIntExtra(EXTRA_SECONDS,0);
//            isTimerGoing = intent.getBooleanExtra(EXTRA_IS_GOING, false);
//        }
//    }

    public void setTimerGoing(boolean timerGoing) {
        isTimerGoing = timerGoing;
    }

    private void runTimer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
               // mBusiness = mSecFragment.getCurrentBusiness();
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isTimerGoing){
                            //mTextViewSec.setText(mCurrentBusiness.getTime());
                            if(mSecFragment.isDetached()) mSecFragment.printInfo(mBusiness);
                            mBusiness.addOneSecond();

                        handler.postDelayed(this, 1000);
                        }
                    }
                });

            }
        });
    }

    public class TimerBinder extends Binder{
        TimerService getService(){
            return TimerService.this;
        }
    }


}
