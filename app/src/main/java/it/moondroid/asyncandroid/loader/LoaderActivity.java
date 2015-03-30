package it.moondroid.asyncandroid.loader;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.math.BigInteger;

import it.moondroid.asyncandroid.R;
import it.moondroid.asyncandroid.asynctask.AsyncTaskFragment;

public class LoaderActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<BigInteger> {

    public static final int LOADER_ID = "primes_loader".hashCode();

    private MyLoader loader;
    private static ProgressDialog dialog;
    private TextView resultView;
    private Button goButton;

    private final LoaderHandler handler = new LoaderHandler();

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
                if (dialog == null) {
                    prepareProgressDialog();
                }
                getLoaderManager().restartLoader(LOADER_ID, null, LoaderActivity.this);
            }
        });

    }


    @Override
    public Loader<BigInteger> onCreateLoader(int loaderId, Bundle args) {
        loader = new MyLoader(getApplicationContext(), 2000);
        loader.setHandler(new WeakReference<>(handler));
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<BigInteger> loader, BigInteger result) {
        resultView.setText(result.toString());
        cleanUp();
    }

    @Override
    public void onLoaderReset(Loader<BigInteger> loader) {
        cleanUp();
    }


    private void prepareProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.calculating);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //TODO cancel loader
               //getLoaderManager().destroyLoader(LOADER_ID);
                loader.cancel();
            }
        });
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();
    }

    private void cleanUp() {
        if (dialog != null){
            dialog.dismiss();
        }
        dialog = null;
    }

    private static final class LoaderHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MyLoader.MSGCODE_PROGRESS) {

                int progress = msg.getData().getInt(MyLoader.MSGKEY_PROGRESS);
//                if (dialog == null) {
//                    prepareProgressDialog();
//                }
                if (dialog != null) {
                    dialog.setProgress(progress);
                }

            }
        }
    }
}
