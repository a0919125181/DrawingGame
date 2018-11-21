package com.example.user.drawinggame.Room.Audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.example.user.drawinggame.Room.RoomFragment;

public class AudioTrackSet {
    AudioTrack track;

    public AudioTrackSet(RoomFragment rf){
        //每秒樣本數(取樣率)
        int frequence = 44100;

        // 定義取樣通道
        int channelInConfig = 2;

        // 定義音訊編碼（16位）
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

        // 緩衝大小(樣本率、聲道數、編碼)
        int bufferSize = AudioTrack.getMinBufferSize(frequence, channelInConfig, audioEncoding);

        // AudioTrack 物件
        track = new AudioTrack(AudioManager.STREAM_MUSIC, frequence, channelInConfig, audioEncoding, bufferSize, AudioTrack.MODE_STREAM);

        rf.setTrack(track);

        track.play();
    }


}
