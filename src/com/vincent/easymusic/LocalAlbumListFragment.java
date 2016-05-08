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

public class LocalAlbumListFragment extends Fragment {
	
	private View rootView = null;
	private ListView musicInfoList = null;

	private List<Map<String, Object>>albumMapList = null;
	private String albumName = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EasyMusicMainActivity.isGetMusicListFlag = false;
    	albumMapList = new ArrayList<Map<String, Object>>();
        
        //get album info
        getAlbumInfos(null, null, EasyMusicMainActivity.albumSortOrder);
        
        //initial the list adapter
        getAlbumInfoListAdapter();
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.musicinfo_list_fragment, container, false);
        
    	musicInfoList = (ListView) rootView.findViewById(R.id.music_info_list);
        initAlbumListView();
        
        return rootView;
    }
    
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void getAlbumInfos(String selection, String[] selectionArgs, String sortOrder){
    	EasyMusicMainActivity.getMusicInfos(selection, selectionArgs, sortOrder);
    	
    	albumMapList.clear();
    	
    	Set<String>albumNameSet = new TreeSet<String>(EasyMusicMainActivity.collator);
    	for(int i=0; i<EasyMusicMainActivity.musicInfos.size(); ++i){
    		albumName = EasyMusicMainActivity.musicInfos.get(i).getAlbum();
    		albumNameSet.add(albumName);
        }
    	
    	int albumCountArray[] = new int[albumNameSet.size()];
    	int index = 0;
    	for(Iterator<String>iter = albumNameSet.iterator(); iter.hasNext();){
    		String albumNameInSet = iter.next();
    		String albumArtist = null;
    		for(int i=0; i<EasyMusicMainActivity.musicInfos.size(); ++i){
            	albumName = EasyMusicMainActivity.musicInfos.get(i).getAlbum();
            	if(albumNameInSet.equals(albumName)){
            		albumCountArray[index] += 1;
            		albumArtist = EasyMusicMainActivity.musicInfos.get(i).getArtist();
            	}
            }
        	
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("album", albumNameInSet);
    		map.put("count", albumCountArray[index]+" 首 - "+albumArtist);
        	albumMapList.add(map);
        	++index;
    	}
    }
    
    public void getAlbumInfoListAdapter(){
    	EasyMusicMainActivity.musicInfoListAdapter = new SimpleAdapter(getActivity(), albumMapList, R.layout.musicinfo_layout,
                new String[]{"album", "count"},
                new int[]{R.id.left_top, R.id.left_bottom});
    }
    
    public void initAlbumListView(){
        musicInfoList.setAdapter(EasyMusicMainActivity.musicInfoListAdapter);
        musicInfoList.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				EasyMusicMainActivity.isGetMusicListFlag = true;
				albumName = (String) albumMapList.get(position).get("album");
				EasyMusicMainActivity.getMusicInfos(MediaStore.Audio.Media.ALBUM+"=?", new String[]{albumName}, EasyMusicMainActivity.musicSortOrder);
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
