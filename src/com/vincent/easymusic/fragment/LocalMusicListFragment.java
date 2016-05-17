/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.vincent.easymusic.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vincent.easymusic.EasyMusicMainActivity;
import com.vincent.easymusic.R;
import com.vincent.easymusic.info.GetMusicInfo;
import com.vincent.easymusic.info.MusicInfo;
import com.vincent.easymusic.utils.Utils;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class LocalMusicListFragment extends Fragment {
	
	private View rootView = null;
	private static ListView musicInfoListView = null;
	
	private static List<MusicInfo> musicInfo = null;
	private static List<Map<String, Object>> musicInfoList = null;
	private static SimpleAdapter musicInfoListAdapter = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.musicinfo_list_fragment, container, false);
        
        musicInfoListView = (ListView) rootView.findViewById(R.id.music_info_list);
        initListView();
        
        return rootView;
    }
    
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    public static void initListView(){
    	musicInfo = GetMusicInfo.getMusicInfo(Utils.mContext, null, null, Utils.musicSortOrder);
      	if(EasyMusicMainActivity.musicInfo == null){
      		EasyMusicMainActivity.musicInfo = new ArrayList<MusicInfo>(musicInfo);
      	}
        getMusicInfoList();
        getMusicInfoListAdapter();
        musicInfoListView.setAdapter(musicInfoListAdapter);
        musicInfoListView.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(EasyMusicMainActivity.musicInfo != null){
					EasyMusicMainActivity.musicInfo.clear();
					EasyMusicMainActivity.musicInfo = null;
				}
				EasyMusicMainActivity.musicInfo = new ArrayList<MusicInfo>(musicInfo);
				EasyMusicMainActivity.MusicPlay(position);
				
				Utils.isPlayingInMusicList = true;
				Utils.isPlayingInAlbumMusicList = false;
				Utils.isPlayingInArtistMusicList = false;
			}
		});
        musicInfoListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				File file = new File(musicInfo.get(position).getPath());
				if(file.exists()){
					file.delete();
					Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			    	scanIntent.setData(Uri.fromFile(file));
			    	Utils.mContext.sendBroadcast(scanIntent);
				}
				((Activity) Utils.mContext).runOnUiThread(new Runnable(){
					
					@Override
					public void run(){
		            	try{
							Thread.sleep(1000);
							if(Utils.isPlayingInMusicList){
								EasyMusicMainActivity.UpdateMusicInfo(position);
							}else{
								musicInfo = GetMusicInfo.getMusicInfo(Utils.mContext, null, null, Utils.musicSortOrder);
						        getMusicInfoList();
								musicInfoListAdapter.notifyDataSetChanged();
							}
						} catch (InterruptedException e){
							e.printStackTrace();
						}
					}
				});
				
				return true;
			}
		});
    }
    
    public static void getMusicInfoList(){
    	if(musicInfoList == null){
    		musicInfoList = new ArrayList<Map<String, Object>>();
    	}
    	musicInfoList.clear();
        for(int i=0; i<musicInfo.size(); ++i){
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("title", musicInfo.get(i).getTitle());
        	map.put("artist", musicInfo.get(i).getArtist());
        	float duration = (float) (musicInfo.get(i).getDuration()/60.0/1000.0);
        	int pre = (int)duration;
        	float suf = (duration-pre)*60;
        	map.put("duration",String.valueOf(pre)+":"+Utils.decimalFormat.format(suf));
        	musicInfoList.add(map);
        }
	}

    public static void getMusicInfoListAdapter(){
    	musicInfoListAdapter = new SimpleAdapter(Utils.mContext, musicInfoList, R.layout.musicinfo_item_layout,
                new String[]{"title", "artist", "duration"},
                new int[]{R.id.left_top, R.id.left_bottom, R.id.right});
    }
    
    public static void updateMusicInfoListAdapter(int position){
    	musicInfo = GetMusicInfo.getMusicInfo(Utils.mContext, null, null, Utils.musicSortOrder);
        getMusicInfoList();
		musicInfoListAdapter.notifyDataSetChanged();
		if(EasyMusicMainActivity.musicInfo != null){
			EasyMusicMainActivity.musicInfo.clear();
			EasyMusicMainActivity.musicInfo = null;
		}
		EasyMusicMainActivity.musicInfo = new ArrayList<MusicInfo>(musicInfo);
    }
    
    @Override
	public void onDestroy(){
    	super.onDestroy();
    	
    	if(musicInfo != null){
	    	musicInfo.clear();
	    	musicInfo = null;
    	}
    	if(musicInfoList != null){
	    	musicInfoList.clear();
	        musicInfoList = null;
    	}
        musicInfoListAdapter = null;
    }
}
