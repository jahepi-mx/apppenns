package pennsylvania.jahepi.com.apppenns.components;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by javier.hernandez on 11/04/2016.
 */
public class AudioRecorder {

    private static final String AUDIO_EXTENSION = ".3gp";

    private static final int RECORDING_TIME = 70;

    private boolean recording;
    private MediaRecorder recorder;
    private AudioRecorderListener listener;
    private File currentAudio;
    private String suffix;
    private Thread thread;
    private int seconds;

    public AudioRecorder(String suffix, AudioRecorderListener listener) {
        recorder = new MediaRecorder();
        this.listener = listener;
        this.suffix = suffix;
    }

    public boolean start() {
        if (!recording) {
            recording = true;
            try {
                String name = new SimpleDateFormat("yyMMdd_HHmmss_" + suffix).format(new Date());
                currentAudio = new File(Environment.getExternalStorageDirectory(), name + AUDIO_EXTENSION);
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(currentAudio.getAbsolutePath());
                recorder.prepare();
                recorder.start();
                listener.onStartRecording();

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (seconds < RECORDING_TIME) {
                            try {
                                Thread.sleep(1000);
                                seconds++;
                                String time = getTimeFormat(seconds);
                                if (isRecording()) {
                                    listener.onRecordingTime(time);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        stop();
                    }
                });
                thread.start();

            } catch (IOException e) {
                recording = false;
                listener.onRecordingError();
                return false;
            } catch (Exception e) {
                recording = false;
                listener.onRecordingError();
                return false;
            }
        }
        return true;
    }

    public void stop() {
        if (recording) {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
                thread = null;
            }
            recording = false;
            recorder.stop();
            recorder.reset();
            seconds = 0;
            listener.onStopRecording(currentAudio);
        }
    }

    public void dispose() {
        recorder.release();
        recorder = null;
    }

    private String getTimeFormat(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        String minutesStr = "";
        String secsStr = "";
        minutesStr = "" + minutes;
        if (minutes < 10) {
            minutesStr = "0" + minutes;
        }
        secsStr = "" + secs;
        if (secs < 10) {
            secsStr = "0" + secs;
        }
        return minutesStr + ":" + secsStr;
    }

    public boolean isRecording() {
        return recording;
    }

    public static interface AudioRecorderListener {
        public void onStartRecording();
        public void onStopRecording(File audio);
        public void onRecordingError();
        public void onRecordingTime(String time);
    }
}
