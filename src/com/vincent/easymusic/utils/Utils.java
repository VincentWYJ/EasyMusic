package com.vincent.easymusic.utils;

import java.text.CollationKey;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.vincent.easymusic.R;
import com.vincent.easymusic.info.GetMusicInfo;
import com.vincent.easymusic.info.MusicInfo;

import android.content.Context;
import android.provider.MediaStore;
import android.widget.SimpleAdapter;

public class Utils{
	
	public static Context mContext = null;
	
	public static String musicSortOrder = MediaStore.Audio.Media.TITLE_KEY+" ASC";
	public static String albumSortOrder = MediaStore.Audio.Media.ALBUM_KEY+" ASC";
	public static String artistSortOrder = MediaStore.Audio.Media.ARTIST_KEY+" ASC";
	
	public static DecimalFormat decimalFormat = new DecimalFormat("00");
	public static Collator collator = SetElementComparator.getInstance(Locale.CHINA);
    
	public static List<Map<String, Object>> getAlbumInfoList(List<MusicInfo> musicInfoArg){
    	List<MusicInfo> musicInfo = musicInfoArg;
    	Set<String> albumNameSet = new TreeSet<String>(collator);
    	    	
    	for(int i=0; i<musicInfo.size(); ++i){
    		albumNameSet.add(musicInfo.get(i).getAlbum());
    	}
    	
    	List<Map<String, Object>> albumInfoList = new ArrayList<Map<String, Object>>();
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
    	
    	return albumInfoList;
    }

    public static SimpleAdapter getAlbumInfoListAdapter(List<Map<String, Object>> albumInfoListArg){
    	List<Map<String, Object>> albumInfoList = albumInfoListArg;
    	
    	SimpleAdapter albumInfoListAdapter = new SimpleAdapter(mContext, albumInfoList, R.layout.musicinfo_item_layout,
    			new String[]{"album", "count"},
    			new int[]{R.id.left_top, R.id.left_bottom});
    	
    	return albumInfoListAdapter;
    }
    	
    public static List<Map<String, Object>> getArtistInfoList(List<MusicInfo> musicInfoArg){
    	List<MusicInfo> musicInfo = musicInfoArg;
    	List<Map<String, Object>> artistInfoList = new ArrayList<Map<String, Object>>();
    	Set<String> artistNameSet = new TreeSet<String>(collator);
    	
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
    	
    	return artistInfoList;
    }
    
    public static SimpleAdapter getArtistInfoListAdapter(List<Map<String, Object>> artistInfoListArg){
    	List<Map<String, Object>> artistInfoList = artistInfoListArg;
    	
    	SimpleAdapter artistInfoListAdapter = new SimpleAdapter(mContext, artistInfoList, R.layout.musicinfo_item_layout,
                new String[]{"artist", "count"},
                new int[]{R.id.left_top, R.id.left_bottom});
    	
    	return artistInfoListAdapter;
    }
    
    public static List<MusicInfo> getMusicInfo(String selection, String[] selectionArgs, String sortOrder){
    	return GetMusicInfo.getMusicInfo(mContext, selection, selectionArgs, sortOrder);
    }
    
    public static List<Map<String, Object>> getMusicInfoList(List<MusicInfo> musicInfoArg){
    	List<MusicInfo> musicInfo = musicInfoArg;
    	List<Map<String, Object>> musicInfoList = new ArrayList<Map<String, Object>>();
	
        for(int i=0;i<musicInfo.size();++i){
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("title", musicInfo.get(i).getTitle());
        	map.put("artist", musicInfo.get(i).getArtist());
        	map.put("album", musicInfo.get(i).getAlbum());
        	float duration = (float) (musicInfo.get(i).getDuration()/60.0/1000.0);
        	int pre = (int)duration;
        	float suf = (duration-pre)*60;
        	map.put("duration",String.valueOf(pre)+":"+decimalFormat.format(suf));
        	musicInfoList.add(map);
        }
        
        return musicInfoList;
	}

    public static SimpleAdapter getMusicInfoListAdapter(List<Map<String, Object>> musicInfoListArg){
    	List<Map<String, Object>> musicInfoList = musicInfoListArg;
    	
    	SimpleAdapter musicInfoListAdapter = new SimpleAdapter(mContext, musicInfoList, R.layout.musicinfo_item_layout,
                new String[]{"title", "artist", "duration"},
                new int[]{R.id.left_top, R.id.left_bottom, R.id.right});
    	
    	return musicInfoListAdapter;
    }
    
    public class SetElementComparator extends Collator{
    	
    	@Override
    	public int compare(String s1, String s2){
    		return s1.compareTo(s2);
    	}

    	@Override
    	public CollationKey getCollationKey(String source){
    		return null;
    	}

    	@Override
    	public int hashCode(){
    		return 0;
    	}
	}
}