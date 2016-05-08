package com.vincent.easymusic;

import java.io.File;
import java.io.IOException;
import java.text.CollationKey;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ResourceAsColor")
public class EasyMusicMainActivity extends Activity {
	
	public static Context mContext = null;
	
	public static String musicSortOrder = MediaStore.Audio.Media.TITLE_KEY+" ASC";
	public static String albumSortOrder = null;  //MediaStore.Audio.Media.ALBUM_KEY+" ASC";
	public static String artistSortOrder = null;  //MediaStore.Audio.Media.ARTIST_KEY+" ASC";
	
	public static Uri uri = null;
	public static String songPath = null;
	public static MediaPlayer mediaPlayer = null;
	
	public static List<MusicInfo> musicInfos = null;
	public static List<Map<String, Object>>musicMapList = null;
	public static SimpleAdapter musicInfoListAdapter = null;
	public static DecimalFormat decimalFormat = new DecimalFormat("00");
	public static Collator collator = SetElementComparator.getInstance(Locale.CHINA);
	
	public static int positionPlay = 0;
	
	public static int buttonPressColor = 0;
	public static int buttonNormalColor = 0;
	
	public static boolean isMusicPlaying = false;
	public static boolean isGetMusicListFlag = true;
	
	private static SeekBar musicPlaySeekBar;
	private static TextView musicPlayName = null;
	private static TextView musicTimePlay = null;
	private static TextView musicTimeEnd = null;
	private static ImageView musicPlayPause = null;
	private static Button localMusicTitle = null;
	private static Button localAlbumTitle = null;
	private static Button localArtistTitle = null;
	public static Fragment listFragment = null;
	private static View musicPlayParamsLayout = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.easymusic_main_layout);
        
        buttonNormalColor = getResources().getColor(R.color.button_normal);
        buttonPressColor = getResources().getColor(R.color.button_pressed);
        
        localMusicTitle = (Button)findViewById(R.id.local_music_title);
        localMusicTitle.setBackgroundColor(buttonPressColor);
        localAlbumTitle = (Button)findViewById(R.id.local_album_title);
        localArtistTitle = (Button)findViewById(R.id.local_artist_title);
        
        musicPlayParamsLayout = (View)findViewById(R.id.music_playparams_layout);
        
        mContext = this;
        
        musicMapList = new ArrayList<Map<String, Object>>();
        
        initMusicViews();

        setSeekBarOnClickListener();
        
		setSeekBarMoveListener();
		
        setMusicInfoListFragment(R.id.local_music_title);
    }
    
	@Override
    protected void onDestroy(){
    	super.onDestroy();
    	
    	isMusicPlaying = false;
    	
    	if(mediaPlayer != null){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
    }
    
    public void LocalTypeSelection(View localView){
    	int id = localView.getId();
    	setMusicInfoListFragment(id);
    }
    
	public void setMusicInfoListFragment(int localMusicType){
		musicPlayParamsLayout.setVisibility(View.VISIBLE);
    	localMusicTitle.setBackgroundColor(buttonNormalColor);
		localAlbumTitle.setBackgroundColor(buttonNormalColor);
		localArtistTitle.setBackgroundColor(buttonNormalColor);
    	switch (localMusicType) {
		case R.id.local_music_title:
			listFragment = new LocalMusicListFragment();
			localMusicTitle.setBackgroundColor(buttonPressColor);
			break;
		case R.id.local_album_title:
			listFragment = new LocalAlbumListFragment();
			localAlbumTitle.setBackgroundColor(buttonPressColor);
			break;
		case R.id.local_artist_title:
			listFragment = new LocalArtistListFragment();
			localArtistTitle.setBackgroundColor(buttonPressColor);
			break;
		default:
			break;
		}
    	
        FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
        fTransaction.add(R.id.musicinfo_list_fragment, listFragment);
        fTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        fTransaction.commit();
    }
    
    public void initMusicViews(){
        musicPlaySeekBar = (SeekBar)findViewById(R.id.music_play_seekbar);
        musicTimePlay = (TextView)findViewById(R.id.music_time_play);
        musicPlayName = (TextView)findViewById(R.id.music_play_name);
        musicTimeEnd = (TextView)findViewById(R.id.music_time_end);
        musicPlayPause = (ImageView)findViewById(R.id.music_play_pause);
        musicPlaySeekBar.setProgress(0);
        musicTimePlay.setText("0:00");
        musicTimeEnd.setText("0:00");
    }
    
    public void setSeekBarOnClickListener(){
    	musicPlaySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(mediaPlayer != null){
					mediaPlayer.seekTo(seekBar.getProgress());
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				float duration = progress/60.0f/1000.0f;
	        	int pre = (int)duration;
	        	int suf = (int)((duration-pre)*60);
	        	musicTimePlay.setText(String.valueOf(pre)+":"+decimalFormat.format(suf));
			}
		});
    }
    
    public static void getMusicInfos(String selection, String[] selectionArgs, String sortOrder){
    	musicInfos = GetMusicInfoList.getMusicList(mContext, selection, selectionArgs, sortOrder);
    	
    	if(isGetMusicListFlag){
	    	musicMapList.clear();
	
	        for(int i=0;i<musicInfos.size();++i){
	        	Map<String, Object> map = new HashMap<String, Object>();
	        	map.put("title", musicInfos.get(i).getTitle());
	        	map.put("artist", musicInfos.get(i).getArtist());
	        	map.put("album", musicInfos.get(i).getAlbum());
	        	float duration = (float) (musicInfos.get(i).getDuration()/60.0/1000.0);
	        	int pre = (int)duration;
	        	float suf = (duration-pre)*60;
	        	map.put("duration",String.valueOf(pre)+":"+decimalFormat.format(suf));
	        	musicMapList.add(map);
	        }
    	}
    }

    public static void getMusicInfoListAdapter(){
    	musicInfoListAdapter = new SimpleAdapter(mContext, musicMapList, R.layout.musicinfo_layout,
                new String[]{"title", "artist", "duration"},
                new int[]{R.id.left_top, R.id.left_bottom, R.id.right});
    }
    
    public void MusicPlayControl(View playView){
    	int id = playView.getId();
    	switch (id) {
		case R.id.music_play_next:
			MusicPlay((positionPlay+1)%musicInfos.size());
			break;
		case R.id.music_play_pre:
			MusicPlay((musicInfos.size()+positionPlay-1)%musicInfos.size());
			break;
		case R.id.music_play_pause:
			if(isMusicPlaying){
				isMusicPlaying = false;
				mediaPlayer.pause();
				musicPlayPause.setBackgroundResource(R.drawable.music_to_pause);
			}else{
				if(mediaPlayer != null){
					isMusicPlaying = true;
					mediaPlayer.start();
					musicPlayPause.setBackgroundResource(R.drawable.music_to_start);
				}else{
					MusicPlay(positionPlay);
				}
			}
			break;
		default:
			break;
		}
    }
    
    public static void MusicPlay(int position){
    	isMusicPlaying = false;
    	int totalTime = musicInfos.get(position).getDuration();
		positionPlay = position;
		musicPlaySeekBar.setMax(totalTime);
		songPath = musicInfos.get(position).getPath();
		File songFile = new File(songPath);
		if(!songFile.exists()){
			Toast.makeText(mContext, "The music file doesn't exists, already updated music list.", Toast.LENGTH_SHORT).show();
			//only remove the special file
			musicInfos.remove(position);
			musicMapList.remove(position);
			musicInfoListAdapter.notifyDataSetChanged();
			return;
		}
		uri = Uri.fromFile(songFile);
		try {
			if(mediaPlayer != null){
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(mContext, uri);
			mediaPlayer.prepare();
			mediaPlayer.start();
			isMusicPlaying = true;
			setMusicViewInfos();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void setMusicViewInfos(){
    	musicPlayName.setText(musicInfos.get(positionPlay).getTitle());
        musicTimeEnd.setText((CharSequence) musicMapList.get(positionPlay).get("duration"));
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				musicPlayPause.setBackgroundResource(R.drawable.music_to_pause);
				String str = musicTimeEnd.getText().toString();
				int result = Integer.parseInt(str.substring(str.indexOf(":")+1));
				float duration = (float)((mediaPlayer.getCurrentPosition())/60.0/1000.0);
	        	int pre = (int)duration;
	        	int suf = (int)((duration-pre)*60);
	        	if(suf != result){
	        		++suf;
	        		if(suf == 60){
	        			suf = 0;
	        			++pre;
	        		}
	        	}
	        	Log.i("AudioInfo", String.valueOf(suf));
	        	musicTimePlay.setText(String.valueOf(pre)+":"+decimalFormat.format(suf));
	        	
	        	//go to play next music after current item finished
	        	MusicPlay((positionPlay+1)%musicInfos.size());
			}
		});
		musicPlayPause.setBackgroundResource(R.drawable.music_to_start);
    }
    
    public void setSeekBarMoveListener(){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
	            	try {
						Thread.sleep(500);
						if(isMusicPlaying){
							musicPlaySeekBar.setProgress(mediaPlayer.getCurrentPosition());
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            }
			}
		}).start();
    }
    
    public class SetElementComparator extends Collator
    {
    	@Override
    	public int compare(String s1, String s2)
    	{
    		return s1.compareTo(s2);
    	}

    	@Override
    	public CollationKey getCollationKey(String source)
    	{
    		return null;
    	}

    	@Override
    	public int hashCode()
    	{
    		return 0;
    	}
    }
}