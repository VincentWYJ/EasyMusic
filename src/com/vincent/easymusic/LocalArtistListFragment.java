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

package com.vincent.easymusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.app.Fragment;
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
	
	private View rootView = null;
	private ListView musicInfoList = null;
	
	private List<Map<String, Object>>artistMapList = null;
	private String artistName = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EasyMusicMainActivity.isGetMusicListFlag = false;
    	artistMapList = new ArrayList<Map<String, Object>>();
        
        //get artist info
        getArtistInfos(null, null, EasyMusicMainActivity.artistSortOrder);
        
        //initial the list adapter
        getArtistInfoListAdapter();
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.musicinfo_list_fragment, container, false);
        
    	musicInfoList = (ListView) rootView.findViewById(R.id.music_info_list);
        initArtistListView();
        
        return rootView;
    }
    
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void getArtistInfos(String selection, String[] selectionArgs, String sortOrder){
    	EasyMusicMainActivity.getMusicInfos(selection, selectionArgs, sortOrder);
    	
    	artistMapList.clear();
    	
    	Set<String>artistNameSet = new TreeSet<String>(EasyMusicMainActivity.collator);
    	for(int i=0; i<EasyMusicMainActivity.musicInfos.size(); ++i){
        	artistName = EasyMusicMainActivity.musicInfos.get(i).getArtist();
        	artistNameSet.add(artistName);
        }
    	
    	int artistCountArray[] = new int[artistNameSet.size()];
    	int index = 0;
    	for(Iterator<String>iter = artistNameSet.iterator(); iter.hasNext();){
    		String artistNameInSet = iter.next();
    		for(int i=0; i<EasyMusicMainActivity.musicInfos.size(); ++i){
            	artistName = EasyMusicMainActivity.musicInfos.get(i).getArtist();
            	if(artistNameInSet.equals(artistName)){
            		artistCountArray[index] += 1;
            	}
            }
    		
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("artist", artistNameInSet);
    		map.put("count", artistCountArray[index]+" 首 ");
    		artistMapList.add(map);
        	++index;
    	}
    }
    
    public void getArtistInfoListAdapter(){
    	EasyMusicMainActivity.musicInfoListAdapter = new SimpleAdapter(getActivity(), artistMapList, R.layout.musicinfo_layout,
                new String[]{"artist", "count"},
                new int[]{R.id.left_top, R.id.left_bottom});
    }
    
    public void initArtistListView(){
    	musicInfoList.setAdapter(EasyMusicMainActivity.musicInfoListAdapter);
        musicInfoList.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				EasyMusicMainActivity.isGetMusicListFlag = true;
				artistName = (String) artistMapList.get(position).get("artist");
				EasyMusicMainActivity.getMusicInfos(MediaStore.Audio.Media.ARTIST+"=?", new String[]{artistName}, EasyMusicMainActivity.musicSortOrder);
				EasyMusicMainActivity.getMusicInfoListAdapter();
				musicInfoList.setAdapter(EasyMusicMainActivity.musicInfoListAdapter);
		        musicInfoList.setOnItemClickListener(new OnItemClickListener() {
		        	
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method stub
						EasyMusicMainActivity.MusicPlay(position);
					}
				});
			}
		});
    }
    
}
