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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LocalMusicListFragment extends Fragment {
	
	private View rootView = null;
	private ListView musicInfoList = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EasyMusicMainActivity.isGetMusicListFlag = true;
        
        //get music info
        EasyMusicMainActivity.getMusicInfos(null, null, EasyMusicMainActivity.musicSortOrder);
        
        //initial the list adapter
        EasyMusicMainActivity.getMusicInfoListAdapter();
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.musicinfo_list_fragment, container, false);
        
    	musicInfoList = (ListView) rootView.findViewById(R.id.music_info_list);
        initListView();
        
        return rootView;
    }
    
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    public void initListView(){
        musicInfoList.setAdapter(EasyMusicMainActivity.musicInfoListAdapter);
        musicInfoList.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				EasyMusicMainActivity.MusicPlay(position);
			}
		});
    }
}
