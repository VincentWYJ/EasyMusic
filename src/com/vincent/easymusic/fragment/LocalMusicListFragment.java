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
import java.util.List;
import java.util.Map;

import com.vincent.easymusic.EasyMusicMainActivity;
import com.vincent.easymusic.R;
import com.vincent.easymusic.info.MusicInfo;
import com.vincent.easymusic.utils.Utils;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class LocalMusicListFragment extends Fragment {
	
	private View rootView = null;
	private ListView musicInfoListView = null;
	
	public static List<MusicInfo> musicInfo = null;
	private static List<Map<String, Object>> musicInfoList = null;
	private static SimpleAdapter musicInfoListAdapter = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        musicInfo = Utils.getMusicInfo(null, null, Utils.musicSortOrder);
        
        musicInfoList = Utils.getMusicInfoList(musicInfo);

        musicInfoListAdapter = Utils.getMusicInfoListAdapter(musicInfoList);
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
    
    public void initListView(){
        musicInfoListView.setAdapter(musicInfoListAdapter);
        musicInfoListView.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(EasyMusicMainActivity.musicInfo != null){
					if(EasyMusicMainActivity.musicInfo != null){
						EasyMusicMainActivity.musicInfo.clear();
						EasyMusicMainActivity.musicInfo = null;
					}
				}
				EasyMusicMainActivity.musicInfo = new ArrayList<MusicInfo>(musicInfo);
				EasyMusicMainActivity.MusicPlay(position);
			}
		});
    }
    
    public static void updateMusicInfoListAdapter(int position){
    	musicInfo.remove(position);
    	musicInfoList.remove(position);
		musicInfoListAdapter.notifyDataSetChanged();
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
