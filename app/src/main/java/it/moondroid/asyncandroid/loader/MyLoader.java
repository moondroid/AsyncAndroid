package it.moondroid.asyncandroid.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.math.BigInteger;

/**
 * Created by marco.granatiero on 28/01/2015.
 */
public class MyLoader extends AsyncTaskLoader<BigInteger> {

    public static int MSGCODE_PROGRESS = 1;
    public static String MSGKEY_PROGRESS = "progress";

    private int primeToFind;
    private boolean canceled = false;
    private WeakReference<? extends Handler> handler;

    /**
     * The Loader abstract class requires a Context passed to its constructor, so we must
     pass a Context up the chain.
     */
    public MyLoader(Context context, int primesToFind) {
        super(context);
        this.primeToFind = primesToFind;
        startLoading();
    }

    @Override
    public BigInteger loadInBackground() {
        Log.d("MyLoader", "loadInBackground()");
        BigInteger prime = new BigInteger("2");
        for (int i=0; i<primeToFind; i++) {
            prime = prime.nextProbablePrime();

            int percentComplete = (int)((i * 100f)/primeToFind);
            publishProgress(percentComplete);

            if (canceled){
                canceled = false;
                cancelLoad();
                break;
            }

        }
        return prime;
    }

    @Override
    public void deliverResult(BigInteger data) {
        Log.d("MyLoader", "deliverResult()");
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        Log.d("MyLoader", "onStartLoading()");
        super.onStartLoading();
        //required to call loadInBackground()
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        Log.d("MyLoader", "onStopLoading()");
        cancelLoad();
    }



    @Override
    public void onCanceled(BigInteger data) {
        Log.d("MyLoader", "onCanceled()");
        super.onCanceled(data);
    }


    public void cancel() {
        canceled = true;
    }

    public void setHandler(WeakReference<? extends Handler> handler){
        this.handler = handler;
    }

    /**
     * Helper to publish int value
     * @param value
     */
    protected void publishProgress(int value){

        if(handler!=null){

            Bundle data = new Bundle();
            data.putInt(MSGKEY_PROGRESS, value);

            /* Creating a message */
            Message msg = new Message();
            msg.setData(data);
            msg.what = MSGCODE_PROGRESS;
            /* Sending the message */
            handler.get().sendMessage(msg);
        }
    }

}
