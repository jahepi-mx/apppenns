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

    private boolean recording;
    private MediaRecorder recorder;
    private AudioRecorderListener listener;
    private File currentAudio;
    private String suffix;

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
            } catch (IOException e) {
                recording = false;
                listener.onRecordingError();
                return false;
            }
        }
        return true;
    }

    public void stop() {
        if (recording) {
            recording = false;
            recorder.stop();
            recorder.reset();
            listener.onStopRecording(currentAudio);
        }
    }

    public void dispose() {
        recorder.release();
        recorder = null;
    }

    public boolean isRecording() {
        return recording;
    }

    public static interface AudioRecorderListener {
        public void onStartRecording();
        public void onStopRecording(File audio);
        public void onRecordingError();
    }
}
