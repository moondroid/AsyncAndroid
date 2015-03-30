package it.moondroid.asyncandroid.asynctask;

/**
 * Created by marco.granatiero on 28/01/2015.
 */
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.math.BigInteger;

import it.moondroid.asyncandroid.R;

public class AsyncTaskActivity extends ActionBarActivity
        implements AsyncTaskListener<Integer, BigInteger> {

    public static final String PRIMES = "primes";
    private ProgressDialog dialog;
    private TextView resultView;
    private Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_async);
        ((TextView)findViewById(R.id.title)).setText(R.string.asynctask_title);
        ((TextView)findViewById(R.id.description)).setText(R.string.asynctask_desc);

        resultView = (TextView)findViewById(R.id.result);
        goButton = (Button)findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AsyncTaskFragment primes = (AsyncTaskFragment)
                        getSupportFragmentManager().findFragmentByTag(PRIMES);

                if (primes == null) {
                    primes = new AsyncTaskFragment();
                    FragmentTransaction transaction =
                            getSupportFragmentManager().beginTransaction();
                    transaction.add(primes, PRIMES);
                    transaction.commit();
                }
            }
        });
    }

    @Override
    public void onPreExecute() {
        onProgressUpdate(0);
    }

    @Override
    public void onProgressUpdate(Integer... progress) {
        if (dialog == null) {
            prepareProgressDialog();
        }
        dialog.setProgress(progress[0]);
    }

    @Override
    public void onPostExecute(BigInteger result) {
        resultView.setText(result.toString());
        cleanUp();
    }

    @Override
    public void onCancelled(BigInteger result) {
        resultView.setText("cancelled at " + result);
        cleanUp();
    }

    private void prepareProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.calculating);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                AsyncTaskFragment primes = (AsyncTaskFragment)
                        getSupportFragmentManager().findFragmentByTag(PRIMES);
                primes.cancel();
            }
        });
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();
    }

    private void cleanUp() {
        dialog.dismiss();
        dialog = null;
        FragmentManager fm = getSupportFragmentManager();
        AsyncTaskFragment primes = (AsyncTaskFragment) fm.findFragmentByTag(PRIMES);
        fm.beginTransaction().remove(primes).commit();
    }
}
