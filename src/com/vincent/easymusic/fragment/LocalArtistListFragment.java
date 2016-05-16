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

public class LocalArtistListFragment extends Fragment {
	
	private static View rootView = null;
	private static ListView artistInfoListView = null;

	private static List<MusicInfo> musicInfo = null;
	private static List<Map<String, Object>> artistInfoList = null;
	private static SimpleAdapter artistInfoListAdapter = null;
	private static String artistName = null;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.musicinfo_list_fragment, container, false);
        
        artistInfoListView = (ListView) rootView.findViewById(R.id.music_info_list);
        initArtistInfoListView();
        
        return rootView;
    }
    
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    public static void initArtistInfoListView(){
    	Utils.isInArtistMusicList = false;
        musicInfo = GetMusicInfo.getMusicInfo(Utils.mContext, null, null, Utils.artistSortOrder);
        getArtistInfoList();
        getArtistInfoListAdapter();
        if(artistInfoListView == null){
        	artistInfoListView = (ListView) rootView.findViewById(R.id.music_info_list);
        }
    	artistInfoListView.setAdapter(artistInfoListAdapter);
    	artistInfoListView.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				/**/
				Utils.isInArtistMusicList = true;
				artistName = (String) artistInfoList.get(position).get("artist");
				musicInfo = GetMusicInfo.getMusicInfo(Utils.mContext, MediaStore.Audio.Media.ARTIST+"=?", new String[]{artistName}, Utils.musicSortOrder);
				getArtistMusicInfoList();
				getArtistMusicInfoListAdapter();
				artistInfoListView.setAdapter(artistInfoListAdapter);
				artistInfoListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if(EasyMusicMainActivity.musicInfo != null){
							EasyMusicMainActivity.musicInfo.clear();
							EasyMusicMainActivity.musicInfo = null;
						}	
						EasyMusicMainActivity.musicInfo = new ArrayList<MusicInfo>(musicInfo);
						EasyMusicMainActivity.MusicPlay(position);
						
						Utils.isPlayingInMusicList = false;
						Utils.isPlayingInAlbumMusicList = false;
						Utils.isPlayingInArtistMusicList = true;
					}
				});
				
			}
		});
    }
    
    public static void getArtistInfoList(){
    	if(artistInfoList == null){
    		artistInfoList = new ArrayList<Map<String, Object>>();
    	}
    	artistInfoList.clear();
    	
    	Set<String> artistNameSet = new TreeSet<String>(Utils.collator);
    	for(int i=0; i<musicInfo.size(); ++i){
        	artistNameSet.add(musicInfo.get(i).getArtist());
        }
    	
    	int artistCountArray[] = new int[artistNameSet.size()];
    	int index = 0;
    	for(Iterator<String>iter = artistNameSet.iterator(); iter.hasNext();){
    		String artistNameInSet = iter.next();
    		for(int i=0; i<musicInfo.size(); ++i){
            	if(artistNameInSet.equals(musicInfo.get(i).getArtist())){
            		artistCountArray[index] += 1;
            	}
            }
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("artist", artistNameInSet);
    		map.put("count", artistCountArray[index]+" 首 ");
    		artistInfoList.add(map);
        	++index;
    	}
    }
    
    public static void getArtistInfoListAdapter(){
    	artistInfoListAdapter = new SimpleAdapter(Utils.mContext, artistInfoList, R.layout.musicinfo_item_layout,
                new String[]{"artist", "count"},
                new int[]{R.id.left_top, R.id.left_bottom});
    }
    
    public static void getArtistMusicInfoList(){
    	if(artistInfoList == null){
    		artistInfoList = new ArrayList<Map<String, Object>>();
    	}
    	artistInfoList.clear();
    	
        for(int i=0; i<musicInfo.size(); ++i){
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("title", musicInfo.get(i).getTitle());
        	map.put("artist", musicInfo.get(i).getArtist());
        	float duration = (float) (musicInfo.get(i).getDuration()/60.0/1000.0);
        	int pre = (int)duration;
        	float suf = (duration-pre)*60;
        	map.put("duration",String.valueOf(pre)+":"+Utils.decimalFormat.format(suf));
        	artistInfoList.add(map);
        }
	}

    public static void getArtistMusicInfoListAdapter(){
    	artistInfoListAdapter = new SimpleAdapter(Utils.mContext, artistInfoList, R.layout.musicinfo_item_layout,
                new String[]{"title", "artist", "duration"},
                new int[]{R.id.left_top, R.id.left_bottom, R.id.right});
    }
    
    public static void updateArtistInfoListAdapter(int position){
    	String removeArtistName = musicInfo.get(position).getArtist();
    	musicInfo = GetMusicInfo.getMusicInfo(Utils.mContext, MediaStore.Audio.Media.ARTIST+"=?", new String[]{removeArtistName}, Utils.musicSortOrder);
    	if(EasyMusicMainActivity.musicInfo != null){
			EasyMusicMainActivity.musicInfo.clear();
			EasyMusicMainActivity.musicInfo = null;
		}
		EasyMusicMainActivity.musicInfo = new ArrayList<MusicInfo>(musicInfo);
    	if(Utils.isInArtistMusicList && removeArtistName.equals(artistName)){
        	getArtistMusicInfoList();
        	artistInfoListAdapter.notifyDataSetChanged();
    	}else{
    		initArtistInfoListView();
    	}
    }
    
    @Override
	public void onDestroy(){
    	super.onDestroy();
    	
    	if(musicInfo != null){
	    	musicInfo.clear();
	    	musicInfo = null;
    	}
    	if(artistInfoList != null){
	    	artistInfoList.clear();
	        artistInfoList = null;
    	}
        artistInfoListAdapter = null;
    }
}