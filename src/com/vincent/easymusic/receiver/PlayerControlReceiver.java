/**
 * PlayerControlReceiver.java
 * com.ximalaya.ting.android.opensdk.test
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ---------------------------------------
 *   		 2015-7-9 		chadwii
 *
 * Copyright (c) 2015, chadwii All Rights Reserved.
*/

package com.vincent.easymusic.receiver;

import java.io.File;

import com.vincent.easymusic.EasyMusicMainActivity;
import com.vincent.easymusic.R;
import com.vincent.easymusic.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PlayerControlReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent){
		if(EasyMusicMainActivity.musicInfo == null || EasyMusicMainActivity.musicInfo.size() == 0){
    		Toast.makeText(Utils.mContext, "There has no musics now.", Toast.LENGTH_SHORT).show();
    		return;
    	}
		String action = intent.getAction();
		String path = EasyMusicMainActivity.musicInfo.get(EasyMusicMainActivity.positionPlay).getPath();
		String title = EasyMusicMainActivity.musicInfo.get(EasyMusicMainActivity.positionPlay).getTitle();
		String artist = EasyMusicMainActivity.musicInfo.get(EasyMusicMainActivity.positionPlay).getArtist();
		int size = EasyMusicMainActivity.musicInfo.size();
		if (Utils.ACTION_CONTROL_PLAY_PAUSE.equals(action)){
			if(EasyMusicMainActivity.isMusicPlaying){
				EasyMusicMainActivity.isMusicPlaying = false;
				EasyMusicMainActivity.mediaPlayer.pause();
				EasyMusicMainActivity.musicPlayPause.setBackgroundResource(R.drawable.music_to_pause);
				File songFile = new File(path);
				if(!songFile.exists()){
					EasyMusicMainActivity.UpdateMusicInfo(EasyMusicMainActivity.positionPlay);
					EasyMusicMainActivity.updateNotification("Title", "Artist", true, true);
					return;
				}
				EasyMusicMainActivity.updateNotification(title, artist, true, true);
			}
			else{
				if(EasyMusicMainActivity.mediaPlayer != null){
					File songFile = new File(path);
					if(!songFile.exists()){
						EasyMusicMainActivity.UpdateMusicInfo(EasyMusicMainActivity.positionPlay);
						EasyMusicMainActivity.updateNotification("Title", "Artist", true, true);
						return;
					}
					EasyMusicMainActivity.isMusicPlaying = true;
					EasyMusicMainActivity.mediaPlayer.start();
					EasyMusicMainActivity.musicPlayPause.setBackgroundResource(R.drawable.music_to_start);
					EasyMusicMainActivity.updateNotification(title, artist, false, true);
				}else{
					EasyMusicMainActivity.MusicPlay(EasyMusicMainActivity.positionPlay);
				}
			}
		}else if (Utils.ACTION_CONTROL_PLAY_NEXT.equals(action)){
			EasyMusicMainActivity.MusicPlay((EasyMusicMainActivity.positionPlay+1)%size);
		}else if (Utils.ACTION_CONTROL_PLAY_PRE.equals(action)){
			EasyMusicMainActivity.MusicPlay((size+EasyMusicMainActivity.positionPlay-1)%size);
		}
	}
}

