package com.vincent.easymusic.utils;

import java.text.CollationKey;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Locale;

import com.vincent.easymusic.R;

import android.app.Activity;
import android.content.Context;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;

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
	
	public static void initStatusBarColor(Activity activity){
        if (android.os.Build.VERSION.SDK_INT > 18) {
			Window window = activity.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
			WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			SystemBarTintManager tintManager = new SystemBarTintManager(activity);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setNavigationBarTintEnabled(true);
			tintManager.setStatusBarTintResource(R.color.statusbar_color);
		}
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