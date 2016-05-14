package com.vincent.easymusic.utils;

import java.text.CollationKey;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Locale;
import android.content.Context;
import android.provider.MediaStore;

public class Utils{
	
	public static Context mContext = null;
	
	public static String LOG = "EasyMusicLogFlag";
	
	public static String musicSortOrder = MediaStore.Audio.Media.TITLE_KEY+" ASC";
	public static String albumSortOrder = MediaStore.Audio.Media.ALBUM_KEY+" ASC";
	public static String artistSortOrder = MediaStore.Audio.Media.ARTIST_KEY+" ASC";
	
	public static DecimalFormat decimalFormat = new DecimalFormat("00");
	public static Collator collator = SetElementComparator.getInstance(Locale.CHINA);
	
	public static boolean isInAlbumMusicList = false;
	public static boolean isInArtistMusicList = false;
    	
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