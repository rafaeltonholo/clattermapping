package br.com.tonholosolutions.clattermapping.service;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created on 13/11/2015.
 *
 * @author rafaeltonholo
 */
public final class SoundListener {
    private static final String TAG = SoundListener.class.getSimpleName();
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
    private static final int ENCODING_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static SoundListener sInstance;

    private AudioRecord mAudioRecord;
    private boolean mIsRecording;
    private int mSampleRateInHz = 44100;
    private int mBufferSize;
    private RecordAudioTask mRecordAudioTask;

    private SoundListener() {
        initAudioRecord();
        mRecordAudioTask = new RecordAudioTask();
    }

    /**
     * Inicia o Audio Record
     */
    private void initAudioRecord() {
        mBufferSize = AudioRecord.getMinBufferSize(mSampleRateInHz, CHANNEL, ENCODING_FORMAT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mSampleRateInHz, CHANNEL,
                ENCODING_FORMAT, mBufferSize);
    }

    public static SoundListener getInstance() {
        synchronized (SoundListener.class) {
            if (sInstance == null) sInstance = new SoundListener();

            return sInstance;
        }
    }

    public void startRecording() {
        mRecordAudioTask = new RecordAudioTask();
        mRecordAudioTask.execute();
    }

    public void stopRecording() {
        mIsRecording = false;
        mRecordAudioTask = null;
    }

    // Gets the RMS
    public double calculateRMS(short[] sndChunk) {

        // Init some vars
        double rms = 0;
        double sum = 0;

        // Sum the values in the buffer
        for (short chunk : sndChunk) {
            sum += Math.pow(chunk, 2);
        }

        // Get the mean and take the square root to get rms
        rms = Math.sqrt(sum / sndChunk.length);

        return rms;
    }

    // Gets dB from RMS
    public double calculateDb(double rms) {

        // Init some vars
        double db = 0;
        //double ref = 32767.0; // reference value used for dB calculation
        double ref = 2 * 0.1;

        // dB calculation
        db = 20 * Math.log10(rms / ref);

        return db;
    }

    private class RecordAudioTask extends AsyncTask<Void, Double, Double> {

        @Override
        protected Double doInBackground(Void... params) {
            double sumDb = 0;
            double captureIterations = 0;
            if (!mIsRecording) {
                mIsRecording = true;
                mAudioRecord.startRecording();
                short[] soundChunk = new short[mBufferSize];
                double rms = 0;
                double db = 0;
                // While record switch is on
                while(mIsRecording) {

                    // Read a chunk of audio data
                    mAudioRecord.read(soundChunk, 0, mBufferSize);

                    // Calculate the RMS of the audio chunk
                    rms = calculateRMS(soundChunk);

                    // Calculate dB
                    db = calculateDb(rms);

                    // Update the UI with the rms value
                    publishProgress(db);

                    sumDb += db;
                    captureIterations++;
                }
            }

            return sumDb / captureIterations;
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "dB: " + String.valueOf(values[0]));
        }
    }
}
