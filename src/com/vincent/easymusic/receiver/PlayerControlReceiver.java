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
import com.vincent.easymusic.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PlayerControlReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent){
		if(!Utils.isMusicInfoEmpty()){
			String action = intent.getAction();
			int size = EasyMusicMainActivity.musicInfo.size();
			if (Utils.ACTION_CONTROL_PLAY_PAUSE.equals(action)){
				EasyMusicMainActivity.actionPauseOrPlay();
			}else if (Utils.ACTION_CONTROL_PLAY_NEXT.equals(action)){
				EasyMusicMainActivity.MusicPlay((EasyMusicMainActivity.positionPlay+1)%size);
			}else if (Utils.ACTION_CONTROL_PLAY_PRE.equals(action)){
				EasyMusicMainActivity.MusicPlay((size+EasyMusicMainActivity.positionPlay-1)%size);
			}
		}
	}
}

