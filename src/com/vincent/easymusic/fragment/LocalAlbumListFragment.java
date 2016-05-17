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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.vincent.easymusic.EasyMusicMainActivity;
import com.vincent.easymusic.R;
import com.vincent.easymusic.info.GetMusicInfo;
import com.vincent.easymusic.info.MusicInfo;
import com.vincent.easymusic.utils.Utils;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class LocalAlbumListFragment extends Fragment {
	
	private static View rootView = null;
	private static ListView albumInfoListView = null;

	private static List<MusicInfo> musicInfo = null;
	private static List<Map<String, Object>> albumInfoList = null;
	private static SimpleAdapter albumInfoListAdapter = null;
	private static String albumName = null;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.musicinfo_list_fragment, container, false);
        
        albumInfoListView = (ListView) rootView.findViewById(R.id.music_info_list);
        initAlbumInfoListView();
        
        return rootView;
    }
    
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    public static void initAlbumInfoListView(){
    	Utils.isInAlbumMusicList = false;
        musicInfo = GetMusicInfo.getMusicInfo(Utils.mContext, null, null, Utils.albumSortOrder);
        getAlbumInfoList();
        getAlbumInfoListAdapter();
        if(albumInfoListView == null){
        	albumInfoListView = (ListView) rootView.findViewById(R.id.music_info_list);
        }
    	albumInfoListView.setAdapter(albumInfoListAdapter);
    	albumInfoListView.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				/**/
				Utils.isInAlbumMusicList = true;
				albumName = (String) albumInfoList.get(position).get("album");
				musicInfo = GetMusicInfo.getMusicInfo(Utils.mContext, MediaStore.Audio.Media.ALBUM+"=?", new String[]{albumName}, Utils.musicSortOrder);
				getAlbumMusicInfoList();
				getAlbumMusicInfoListAdapter();
				albumInfoListView.setAdapter(albumInfoListAdapter);
				albumInfoListView.setOnItemClickListener(new OnItemClickListener() {
		        	
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if(EasyMusicMainActivity.musicInfo != null){
							EasyMusicMainActivity.musicInfo.clear();
							EasyMusicMainActivity.musicInfo = null;
						}
						EasyMusicMainActivity.musicInfo = new ArrayList<MusicInfo>(musicInfo);
						EasyMusicMainActivity.MusicPlay(position);
						
						Utils.isPlayingInMusicList = false;
						Utils.isPlayingInAlbumMusicList = true;
						Utils.isPlayingInArtistMusicList = false;
					}
				});
			}
		});
    }
    
	public static void getAlbumInfoList(){
		if(albumInfoList == null){
			albumInfoList = new ArrayList<Map<String, Object>>();
    	}
		albumInfoList.clear();
		
    	Set<String> albumNameSet = new TreeSet<String>(Utils.collator);
    	for(int i=0; i<musicInfo.size(); ++i){
    		albumNameSet.add(musicInfo.get(i).getAlbum());
    	}

    	int albumCountArray[] = new int[albumNameSet.size()];
    	int index = 0;
    	for(Iterator<String>iter = albumNameSet.iterator(); iter.hasNext();){
    		String albumNameInSet = iter.next();
    		String albumArtist = null;
    		for(int i=0; i<musicInfo.size(); ++i){
    			if(albumNameInSet.equals(musicInfo.get(i).getAlbum())){
    				albumCountArray[index] += 1;
    				albumArtist = musicInfo.get(i).getArtist();
    			}
    		}
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("album", albumNameInSet);
    		map.put("count", albumCountArray[index]+" 首 - "+albumArtist);
    		albumInfoList.add(map);
    		++index;
    	}
    }

    public static void getAlbumInfoListAdapter(){
    	albumInfoListAdapter = new SimpleAdapter(Utils.mContext, albumInfoList, R.layout.musicinfo_item_layout,
    			new String[]{"album", "count"},
    			new int[]{R.id.left_top, R.id.left_bottom});
    }
    
    public static void getAlbumMusicInfoList(){
    	if(albumInfoList == null){
    		albumInfoList = new ArrayList<Map<String, Object>>();
    	}
    	albumInfoList.clear();
    	
        for(int i=0; i<musicInfo.size(); ++i){
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("title", musicInfo.get(i).getTitle());
        	map.put("artist", musicInfo.get(i).getArtist());
        	float duration = (float) (musicInfo.get(i).getDuration()/60.0/1000.0);
        	int pre = (int)duration;
        	float suf = (duration-pre)*60;
        	map.put("duration",String.valueOf(pre)+":"+Utils.decimalFormat.format(suf));
        	albumInfoList.add(map);
        }
	}

    public static void getAlbumMusicInfoListAdapter(){
    	albumInfoListAdapter = new SimpleAdapter(Utils.mContext, albumInfoList, R.layout.musicinfo_item_layout,
                new String[]{"title", "artist", "duration"},
                new int[]{R.id.left_top, R.id.left_bottom, R.id.right});
    }
    
    public static void updateAlbumInfoListAdapter(int position){
    	String removeAlbumName = musicInfo.get(position).getAlbum();
    	musicInfo = GetMusicInfo.getMusicInfo(Utils.mContext, MediaStore.Audio.Media.ALBUM+"=?", new String[]{removeAlbumName}, Utils.musicSortOrder);
    	if(EasyMusicMainActivity.musicInfo != null){
			EasyMusicMainActivity.musicInfo.clear();
			EasyMusicMainActivity.musicInfo = null;
		}
		EasyMusicMainActivity.musicInfo = new ArrayList<MusicInfo>(musicInfo);
    	if(Utils.isInAlbumMusicList && removeAlbumName.equals(albumName)){
        	getAlbumMusicInfoList();
        	albumInfoListAdapter.notifyDataSetChanged();
    	}else{
    		initAlbumInfoListView();
    	}
    }
    
    @Override
	public void onDestroy(){
    	super.onDestroy();
    	
    	if(musicInfo != null){
	    	musicInfo.clear();
	    	musicInfo = null;
    	}
    	if(albumInfoList != null){
	    	albumInfoList.clear();
	        albumInfoList = null;
    	}
        albumInfoListAdapter = null;
    }
}