package it.moondroid.asyncandroid.asynctask;

/**
 * Created by marco.granatiero on 28/01/2015.
 */
public interface AsyncTaskListener<Progress, Result> {
    void onPreExecute();
    void onProgressUpdate(Progress... progress);
    void onPostExecute(Result result);
    void onCancelled(Result result);
}
