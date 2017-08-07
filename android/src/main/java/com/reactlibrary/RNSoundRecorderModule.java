
package com.reactlibrary;

import android.media.MediaRecorder;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RNSoundRecorderModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private MediaRecorder mRecorder = null;
  private String mOutput = null;

  private static final String OPTION_KEY_SOURCE = "source";
  private static final String OPTION_KEY_FORMAT = "format";
  private static final String OPTION_KEY_CHANNELS = "channels";
  private static final String OPTION_KEY_ENCODING_BIT_RATE = "encodingBitRate";
  private static final String OPTION_KEY_ENCODER = "encoder";
  private static final String OPTION_KEY_SAMPLE_RATE= "sampleRate";

  public RNSoundRecorderModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNSoundRecorder";
  }

  @Override
  public Map<String, Object> getConstants() {
    Map<String, Object> constants = new HashMap<>();
    constants.put("PATH_DOCUMENT", reactContext.getFilesDir().getAbsolutePath());
    constants.put("PATH_CACHE", reactContext.getCacheDir().getAbsolutePath());
    // constants.put("PATH_LIBRARY", "");

    constants.put("SOURCE_CAMCORDER", MediaRecorder.AudioSource.CAMCORDER);
    constants.put("SOURCE_MIC", MediaRecorder.AudioSource.MIC);
    constants.put("SOURCE_REMOTE_SUBMIX", MediaRecorder.AudioSource.REMOTE_SUBMIX);
    constants.put("SOURCE_VOICE_CALL", MediaRecorder.AudioSource.VOICE_CALL);
    constants.put("SOURCE_VOICE_COMMUNICATION", MediaRecorder.AudioSource.VOICE_COMMUNICATION);
    constants.put("SOURCE_VOICE_DOWNLINK", MediaRecorder.AudioSource.VOICE_DOWNLINK);
    constants.put("SOURCE_VOICE_RECOGNITION", MediaRecorder.AudioSource.VOICE_RECOGNITION);
    constants.put("SOURCE_VOICE_UPLINK", MediaRecorder.AudioSource.VOICE_UPLINK);

    constants.put("FORMAT_AAC_ADTS", MediaRecorder.OutputFormat.AAC_ADTS);
    constants.put("FORMAT_AMR_NB", MediaRecorder.OutputFormat.AMR_NB);
    constants.put("FORMAT_AMR_WB", MediaRecorder.OutputFormat.AMR_WB);
    constants.put("FORMAT_MPEG_4", MediaRecorder.OutputFormat.MPEG_4);
    constants.put("FORMAT_THREE_GPP", MediaRecorder.OutputFormat.THREE_GPP);
    constants.put("FORMAT_WEBM", MediaRecorder.OutputFormat.WEBM);

    constants.put("ENCODER_AAC", MediaRecorder.AudioEncoder.AAC);
    constants.put("ENCODER_AAC_ELD", MediaRecorder.AudioEncoder.AAC_ELD);
    constants.put("ENCODER_AMR_NB", MediaRecorder.AudioEncoder.AMR_NB);
    constants.put("ENCODER_AMR_WB", MediaRecorder.AudioEncoder.AMR_WB);
    constants.put("ENCODER_HE_AAC", MediaRecorder.AudioEncoder.HE_AAC);
    constants.put("ENCODER_VORBIS", MediaRecorder.AudioEncoder.VORBIS);

    return constants;
  }

  @ReactMethod
  public void start(String path, ReadableMap options, Promise promise) {
    if(mRecorder != null) {
      promise.reject("already_recording", "Already Recording");
      return;
    }

    // parse options
    int source = MediaRecorder.AudioSource.DEFAULT;
    if(options.hasKey(OPTION_KEY_SOURCE))
      source = options.getInt(OPTION_KEY_SOURCE);

    int format = MediaRecorder.OutputFormat.DEFAULT;
    if(options.hasKey(OPTION_KEY_FORMAT))
      format = options.getInt(OPTION_KEY_FORMAT);

    int channels = 1;
    if(options.hasKey(OPTION_KEY_CHANNELS))
      channels = options.getInt(OPTION_KEY_CHANNELS);

    int encodingBitRate = 64000;
    if(options.hasKey(OPTION_KEY_ENCODING_BIT_RATE))
      encodingBitRate = options.getInt(OPTION_KEY_ENCODING_BIT_RATE);

    int encoder = MediaRecorder.AudioEncoder.DEFAULT;
    if(options.hasKey(OPTION_KEY_ENCODER))
      encoder = options.getInt(OPTION_KEY_ENCODER);

    int sampleRate = 16000;
    if(options.hasKey(OPTION_KEY_SAMPLE_RATE))
      sampleRate = options.getInt(OPTION_KEY_SAMPLE_RATE);

    mOutput = path;
    mRecorder = new MediaRecorder();
    mRecorder.setAudioSource(source);
    mRecorder.setOutputFormat(format);
    mRecorder.setAudioChannels(channels);
    mRecorder.setOutputFile(path);
    mRecorder.setAudioEncodingBitRate(encodingBitRate);
    mRecorder.setAudioEncoder(encoder);
    mRecorder.setAudioSamplingRate(sampleRate);

    try {
      mRecorder.prepare();
      mRecorder.start();
      promise.resolve(null);
    } catch (IOException e) {
      promise.reject("recording_failed", "Cannot record audio at path: " + path);
    }
  }

  @ReactMethod
  public void stop(Promise promise) {
    if(mRecorder == null) {
      promise.reject("not_recording", "Not Recording");
      return;
    }

    mRecorder.stop();
    mRecorder.release();
    mRecorder = null;

    String output = mOutput;
    mOutput = null;

    promise.resolve(output);
  }



}