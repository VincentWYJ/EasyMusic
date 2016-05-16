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

import com.vincent.easymusic.EasyMusicMainActivity;
import com.vincent.easymusic.R;
import com.vincent.easymusic.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PlayerControlReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent){
		String action = intent.getAction();
		if (Utils.ACTION_CONTROL_PLAY_PAUSE.equals(action)){
			if(EasyMusicMainActivity.mediaPlayer.isPlaying()){
				EasyMusicMainActivity.mediaPlayer.pause();
				EasyMusicMainActivity.musicPlayPause.setBackgroundResource(R.drawable.music_to_pause);
				EasyMusicMainActivity.updateNotification(EasyMusicMainActivity.musicInfo.get(EasyMusicMainActivity.positionPlay).getTitle(), 
						EasyMusicMainActivity.musicInfo.get(EasyMusicMainActivity.positionPlay).getArtist(), true, true);
			}
			else{
				EasyMusicMainActivity.mediaPlayer.start();
				EasyMusicMainActivity.musicPlayPause.setBackgroundResource(R.drawable.music_to_start);
				EasyMusicMainActivity.updateNotification(EasyMusicMainActivity.musicInfo.get(EasyMusicMainActivity.positionPlay).getTitle(), 
						EasyMusicMainActivity.musicInfo.get(EasyMusicMainActivity.positionPlay).getArtist(), false, true);
			}
		}else if (Utils.ACTION_CONTROL_PLAY_NEXT.equals(action)){
			EasyMusicMainActivity.MusicPlay((EasyMusicMainActivity.positionPlay+1)%EasyMusicMainActivity.musicInfo.size());
		}else if (Utils.ACTION_CONTROL_PLAY_PRE.equals(action)){
			EasyMusicMainActivity.MusicPlay((EasyMusicMainActivity.musicInfo.size()+EasyMusicMainActivity.positionPlay-1)%EasyMusicMainActivity.musicInfo.size());
		}
	}

}

