package smarter.uearn.money.callrecord;

import java.io.File;
import java.io.IOException;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.util.Log;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;

public class MediaRecordWrapper {
    private String outputFile;
    public int final_audio_source = -1;

    private MediaRecorder mediaRecorder;
    private Context context;
    private String TAG = "MediaRecordWrapper";

    public MediaRecordWrapper(Context mcontext) {
        context = mcontext;
    }

    public boolean startRecording(String filename, int audio_source) {
        this.outputFile = filename;

        if (audio_source == MediaRecorder.AudioSource.MIC) {
            return do_media_MIC_record();

        } else {
            return do_media_record();
        }
    }


    public boolean stopRecording() {
        try {
            if(mediaRecorder != null)
                 mediaRecorder.stop();
        } catch (RuntimeException re) {
            re.printStackTrace();
            if (outputFile != null) {
                File f = new File(outputFile);
                if (f.exists()) {
                    f.delete();
                }
            }
            return false;
        }

        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        return true;
    }

    private boolean do_media_record() {

        String audioSource = "";
        String audioEncoder = "";
        String outputFormat = "";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        audioSource = String.valueOf(MediaRecorder.AudioSource.VOICE_CALL);
        try {
            if (ApplicationSettings.containsPref(AppConstants.TRANSCRIPT_AUDIO_FORMATE)) {
                outputFormat = ApplicationSettings.getPref(AppConstants.TRANSCRIPT_AUDIO_FORMATE, "");
                if ((outputFormat != null) && !(outputFormat.isEmpty())) {
                    if (outputFormat.equalsIgnoreCase("mpeg4")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    } else if (outputFormat.equalsIgnoreCase("amrwb")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
                    } else if (outputFormat.equalsIgnoreCase("amrnb")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    } else if (outputFormat.equalsIgnoreCase("aacadts")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                    } else if (outputFormat.equalsIgnoreCase("default")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    } else if (outputFormat.equalsIgnoreCase("threegpp")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    } else if (outputFormat.equalsIgnoreCase("webm")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.WEBM);
                    } else {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        outputFormat = "mpeg4";
                    }
                } else {
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    outputFormat = "mpeg4";
                }
            } else {
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                outputFormat = "mpeg4";
            }
        } catch (Exception e) {
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " Media Record Set Output Format do_media_record()" + e;
            ServiceApplicationUsage.callErrorLog(message);
            e.printStackTrace();
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        }

        try {
            if (ApplicationSettings.containsPref(AppConstants.TRANSCRIPT_AUDIO_ENCODE_FORMATE)) {
                outputFormat = ApplicationSettings.getPref(AppConstants.TRANSCRIPT_AUDIO_ENCODE_FORMATE, "");
                if ((outputFormat != null) && !(outputFormat.isEmpty())) {
                    if (outputFormat.equalsIgnoreCase("aac")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    } else if (outputFormat.equalsIgnoreCase("default")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    } else if (outputFormat.equalsIgnoreCase("amrnb")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    } else if (outputFormat.equalsIgnoreCase("amrwb")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                    } else if (outputFormat.equalsIgnoreCase("heaac")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
                    } else if (outputFormat.equalsIgnoreCase("aaceld")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);
                    } else {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        audioEncoder = String.valueOf(MediaRecorder.AudioEncoder.AAC);
                    }
                } else {
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    audioEncoder = String.valueOf(MediaRecorder.AudioEncoder.AAC);
                }
            } else {
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                audioEncoder = String.valueOf(MediaRecorder.AudioEncoder.AAC);
            }
        } catch (Exception e) {
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " Media Record Set Audio Encoder do_media_record()" + e;
            ServiceApplicationUsage.callErrorLog(message);
            e.printStackTrace();
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioEncoder = String.valueOf(MediaRecorder.AudioEncoder.AAC);
        }

        mediaRecorder.setOutputFile(outputFile);
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " MediaRecord Prepare Illegal state exception do_media_record() mediaRecorder.prepare()" + e;
            ServiceApplicationUsage.callErrorLog(message);
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " MediaRecord Prepare IOException do_media_record() mediaRecorder.prepare()" + e;
            ServiceApplicationUsage.callErrorLog(message);
            e.printStackTrace();
            return false;
        }

        try {
            mediaRecorder.start();
            final_audio_source = MediaRecorder.AudioSource.VOICE_CALL;
            return true;
        } catch (RuntimeException re) {
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " Media Record Failed -  Fallback to do Mic (Audio) Record do_media_record() mediaRecorder.start()" + re;
            ServiceApplicationUsage.callErrorLog(message);
            re.printStackTrace();

            return do_media_MIC_record();
        } catch (Exception e) {
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " Media Record Failed Exception do_media_record() mediaRecorder.start()" + e;
            ServiceApplicationUsage.callErrorLog(message);
            e.printStackTrace();
        }
        return false;
    }

    private boolean do_media_MIC_record() {

        String audioSource = "";
        String audioEncoder = "";
        String outputFormat = "";

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioSource = String.valueOf(MediaRecorder.AudioSource.MIC);
        try {
            if (ApplicationSettings.containsPref(AppConstants.TRANSCRIPT_AUDIO_FORMATE)) {
                outputFormat = ApplicationSettings.getPref(AppConstants.TRANSCRIPT_AUDIO_FORMATE, "");
                if ((outputFormat != null) && !(outputFormat.isEmpty())) {
                    if (outputFormat.equalsIgnoreCase("mpeg4")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    } else if (outputFormat.equalsIgnoreCase("amrwb")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
                    } else if (outputFormat.equalsIgnoreCase("amrnb")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    } else if (outputFormat.equalsIgnoreCase("aacadts")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                    } else if (outputFormat.equalsIgnoreCase("default")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    } else if (outputFormat.equalsIgnoreCase("threegpp")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    } else if (outputFormat.equalsIgnoreCase("webm")) {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.WEBM);
                    } else {
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        outputFormat = "mpeg4";
                    }
                } else {
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    outputFormat = "mpeg4";
                }
            } else {
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                outputFormat = "mpeg4";
            }
        } catch (Exception e) {
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " Media Record Set Output Format do_media_MIC_record()" + e;
            ServiceApplicationUsage.callErrorLog(message);
            e.printStackTrace();
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        }
        try {
            if (ApplicationSettings.containsPref(AppConstants.TRANSCRIPT_AUDIO_ENCODE_FORMATE)) {
                outputFormat = ApplicationSettings.getPref(AppConstants.TRANSCRIPT_AUDIO_ENCODE_FORMATE, "");
                if ((outputFormat != null) && !(outputFormat.isEmpty())) {
                    if (outputFormat.equalsIgnoreCase("aac")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    } else if (outputFormat.equalsIgnoreCase("default")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    } else if (outputFormat.equalsIgnoreCase("amrnb")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    } else if (outputFormat.equalsIgnoreCase("amrwb")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                    } else if (outputFormat.equalsIgnoreCase("heaac")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
                    } else if (outputFormat.equalsIgnoreCase("aaceld")) {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);
                    } else {
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        audioEncoder = String.valueOf(MediaRecorder.AudioEncoder.AAC);
                    }
                } else {
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    audioEncoder = String.valueOf(MediaRecorder.AudioEncoder.AAC);
                }
            } else {
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                audioEncoder = String.valueOf(MediaRecorder.AudioEncoder.AAC);
            }
        } catch (Exception e) {
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " Media Record Set Audio Encoder do_media_MIC_record()" + e;
            ServiceApplicationUsage.callErrorLog(message);
            e.printStackTrace();
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioEncoder = String.valueOf(MediaRecorder.AudioEncoder.AAC);
        }
        mediaRecorder.setOutputFile(outputFile);
		try {
    		mediaRecorder.prepare();
		} catch (IllegalStateException e) {
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " MediaRecord Prepare Illegal state exception do_media_MIC_record() mediaRecorder.prepare()" + e;
            ServiceApplicationUsage.callErrorLog(message);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " MediaRecord Prepare IOException do_media_MIC_record() mediaRecorder.prepare()" + e;
            ServiceApplicationUsage.callErrorLog(message);
			e.printStackTrace();
			return false;
		}
    	
    	try {
	      	mediaRecorder.start(); 
	      	final_audio_source = MediaRecorder.AudioSource.MIC;
	      	return true;
    	} catch(RuntimeException re){
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " Media Record Failed -  Fallback to do Mic (Audio) Record do_media_MIC_record() mediaRecorder.start()" + re;
            ServiceApplicationUsage.callErrorLog(message);
			re.printStackTrace();
		    return false;
    	}catch(Exception e){
            String message = "Audio Source: "+ audioSource + " Audio Encoder: " + audioEncoder + " Output Format: " + outputFormat + " Output File: "+ outputFile + " Media Record Failed Exception do_media_MIC_record() mediaRecorder.start()" + e;
            ServiceApplicationUsage.callErrorLog(message);
    		e.printStackTrace();
    	}
		return false;
	}
}
