package com.vincent.easymusic;

import java.io.File;
import java.io.IOException;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "ResourceAsColor", "InflateParams" })
public class EasyMusicMainActivity extends Activity {
	
	private Context mContext = null;
	
	private String musicSortOrder = MediaStore.Audio.Media.TITLE_KEY+" ASC";
	private String albumSortOrder = null;  //MediaStore.Audio.Media.ALBUM_KEY+" ASC";
	private String artistSortOrder = null;  //MediaStore.Audio.Media.ARTIST_KEY+" ASC";
	
	private Uri uri = null;
	private String songPath = null;
	private MediaPlayer mediaPlayer = null;
	
	private List<MusicInfo> musicInfos = null;
	private ListView musicInfoList = null;
	private ListView albumInfoList = null;
	private ListView artistInfoList = null;
	private List<Map<String, Object>> musicMapList = null;
	private String albumName = null;
	private String artistName = null;
	private ArrayList<View> viewPagerContainter = null;
	private SimpleAdapter musicInfoListAdapter = null;
	private DecimalFormat decimalFormat = new DecimalFormat("00");
	private Collator collator = SetElementComparator.getInstance(Locale.CHINA);
	
	private int positionPlay = 0;
	
	private boolean isMusicPlaying = false;
	private boolean isGetMusicListFlag = true;
	
	private int buttonPressColor = 0;
	private int buttonNormalColor = 0;
	
	private Button localMusicTitle = null;
	private Button localAlbumTitle = null;
	private Button localArtistTitle = null;
	private SeekBar musicPlaySeekBar;
	private TextView musicPlayName = null;
	private TextView musicTimePlay = null;
	private TextView musicTimeEnd = null;
	private ImageView musicPlayPause = null;
	private ViewPager viewPager = null;
	private View pagerMusic = null;
	private View pagerAlbum = null;
	private View pagerArtist = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.easymusic_main_layout);
        
        mContext = this;
        
        buttonNormalColor = getResources().getColor(R.color.button_normal);
        buttonPressColor = getResources().getColor(R.color.button_pressed);
        
        musicMapList = new ArrayList<Map<String, Object>>();
        viewPagerContainter = new ArrayList<View>();
        
        initMusicViews();
        
        setViewPagerAdapter();
        setViewPagerChangeListener();
        
        setPagerMusicListAdapter();
        //setPagerAlbumListAdapter();
        //setPagerArtistListAdapter();

        setSeekBarOnClickListener();
		setSeekBarMoveListener();
    }
    
    //action for title button click
    public void LocalTypeSelection(View localView){
    	localMusicTitle.setBackgroundColor(buttonNormalColor);
		localAlbumTitle.setBackgroundColor(buttonNormalColor);
		localArtistTitle.setBackgroundColor(buttonNormalColor);
		if(localView.getId() == R.id.local_music_title){
 			setPagerMusicListAdapter();
 			viewPager.setCurrentItem(0);
 			localMusicTitle.setBackgroundColor(buttonPressColor);
 		}else if(localView.getId() == R.id.local_album_title){
 			setPagerAlbumListAdapter();
 			localAlbumTitle.setBackgroundColor(buttonPressColor);
 			viewPager.setCurrentItem(1);
 		}else if(localView.getId() == R.id.local_artist_title){
 			setPagerArtistListAdapter();
 			viewPager.setCurrentItem(2);
 			localArtistTitle.setBackgroundColor(buttonPressColor);
 		}
    }
    
	public void initMusicViews(){
    	viewPager = (ViewPager) findViewById(R.id.musicinfo_list_fragment);

    	pagerMusic = LayoutInflater.from(mContext).inflate(R.layout.musicinfo_list_layout, null);
        pagerAlbum = LayoutInflater.from(mContext).inflate(R.layout.musicinfo_list_layout, null);
        pagerArtist = LayoutInflater.from(mContext).inflate(R.layout.musicinfo_list_layout, null);
          
        viewPagerContainter.add(pagerMusic);
        viewPagerContainter.add(pagerAlbum);
        viewPagerContainter.add(pagerArtist);
        
        localMusicTitle = (Button)findViewById(R.id.local_music_title);
        localAlbumTitle = (Button)findViewById(R.id.local_album_title);
        localArtistTitle = (Button)findViewById(R.id.local_artist_title);
        musicPlaySeekBar = (SeekBar)findViewById(R.id.music_play_seekbar);
        musicTimePlay = (TextView)findViewById(R.id.music_time_play);
        musicPlayName = (TextView)findViewById(R.id.music_play_name);
        musicTimeEnd = (TextView)findViewById(R.id.music_time_end);
        musicPlayPause = (ImageView)findViewById(R.id.music_play_pause);
        
        musicInfoList = (ListView) pagerMusic.findViewById(R.id.music_info_list);
        albumInfoList = (ListView) pagerAlbum.findViewById(R.id.music_info_list);
        artistInfoList = (ListView) pagerArtist.findViewById(R.id.music_info_list);
        
        localMusicTitle.setBackgroundColor(buttonPressColor);
        musicPlaySeekBar.setProgress(0);
        musicTimePlay.setText("0:00");
        musicTimeEnd.setText("0:00");
    }
    
    public void setViewPagerAdapter(){
    	 viewPager.setAdapter(new PagerAdapter() {

         	@Override
         	public int getCount() {
         		return viewPagerContainter.size();
         	}

         	@Override
         	public void destroyItem(ViewGroup container, int position, Object object) {
         		((ViewPager) container).removeView(viewPagerContainter.get(position));
         	}

         	@Override
         	public Object instantiateItem(ViewGroup container, int position) {
         		((ViewPager) container).addView(viewPagerContainter.get(position));
         		return viewPagerContainter.get(position);
         	}

         	@Override
         	public boolean isViewFromObject(View arg0, Object arg1) {
         		return arg0 == arg1;
         	}

         	@Override
         	public int getItemPosition(Object object) {
         		return super.getItemPosition(object);
         	}

         	@Override
         	public CharSequence getPageTitle(int position) {
         		return null;
         	}
         });
    }
    
    @SuppressWarnings("deprecation")
	public void setViewPagerChangeListener(){
    	viewPager.setOnPageChangeListener(new OnPageChangeListener() {
    		
        	@Override
        	public void onPageScrollStateChanged(int arg0) {
        	}

        	@Override
        	public void onPageScrolled(int arg0, float arg1, int arg2) {
        	}

        	@Override
        	public void onPageSelected(int arg0) {
        		localMusicTitle.setBackgroundColor(buttonNormalColor);
        		localAlbumTitle.setBackgroundColor(buttonNormalColor);
        		localArtistTitle.setBackgroundColor(buttonNormalColor);
        		if(arg0 == 0){
         			setPagerMusicListAdapter();
         			localMusicTitle.setBackgroundColor(buttonPressColor);
         		}else if(arg0 == 1){
         			setPagerAlbumListAdapter();
         			localAlbumTitle.setBackgroundColor(buttonPressColor);
         		}else if(arg0 == 2){
         			setPagerArtistListAdapter();
         			localArtistTitle.setBackgroundColor(buttonPressColor);
         		}
        	}
        });
    }
    
    public void setPagerMusicListAdapter(){
    	isGetMusicListFlag = true;
    	
    	getMusicInfos(null, null, musicSortOrder);
        
    	getMusicInfoListAdapter();

    	musicInfoList.setAdapter(musicInfoListAdapter);
    	musicInfoList.setOnItemClickListener(new OnItemClickListener() {

    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			MusicPlay(position);
    		}
    	});
    }
    
    public void setPagerAlbumListAdapter(){
    	isGetMusicListFlag = false;

     	getAlbumInfos(null, null, albumSortOrder);

     	getAlbumInfoListAdapter();
     	
     	albumInfoList.setAdapter(musicInfoListAdapter);
     	albumInfoList.setOnItemClickListener(new OnItemClickListener() {
     		
     		@Override
     		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
     			isGetMusicListFlag = true;
     			albumName = (String) musicMapList.get(position).get("album");
     			getMusicInfos(MediaStore.Audio.Media.ALBUM+"=?", new String[]{albumName}, musicSortOrder);
     			getMusicInfoListAdapter();
     			albumInfoList.setAdapter(musicInfoListAdapter);
     			albumInfoList.setOnItemClickListener(new OnItemClickListener() {
     				
     				@Override
     				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
     					MusicPlay(position);
     				}
     			});
     		}
     	});
    }
    
    public void getAlbumInfos(String selection, String[] selectionArgs, String sortOrder){
    	getMusicInfos(selection, selectionArgs, sortOrder);
    	
    	musicMapList.clear();
    	
    	Set<String>albumNameSet = new TreeSet<String>(collator);
    	for(int i=0; i<musicInfos.size(); ++i){
    		albumName = musicInfos.get(i).getAlbum();
    		albumNameSet.add(albumName);
    	}
    	
    	int albumCountArray[] = new int[albumNameSet.size()];
    	int index = 0;
    	for(Iterator<String>iter = albumNameSet.iterator(); iter.hasNext();){
    		String albumNameInSet = iter.next();
    		String albumArtist = null;
    		for(int i=0; i<musicInfos.size(); ++i){
    			albumName = musicInfos.get(i).getAlbum();
    			if(albumNameInSet.equals(albumName)){
    				albumCountArray[index] += 1;
    				albumArtist = musicInfos.get(i).getArtist();
    			}
    		}
    		
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("album", albumNameInSet);
    		map.put("count", albumCountArray[index]+" 首 - "+albumArtist);
    		musicMapList.add(map);
    		++index;
    	}
    }

    public void getAlbumInfoListAdapter(){
    	musicInfoListAdapter = new SimpleAdapter(mContext, musicMapList, R.layout.musicinfo_item_layout,
    			new String[]{"album", "count"},
    			new int[]{R.id.left_top, R.id.left_bottom});
    }
    
    public void setPagerArtistListAdapter(){
    	isGetMusicListFlag = false;

    	getArtistInfos(null, null, artistSortOrder);

     	getArtistInfoListAdapter();
    	
    	artistInfoList.setAdapter(musicInfoListAdapter);
    	artistInfoList.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				isGetMusicListFlag = true;
				artistName = (String) musicMapList.get(position).get("artist");
				getMusicInfos(MediaStore.Audio.Media.ARTIST+"=?", new String[]{artistName}, musicSortOrder);
				getMusicInfoListAdapter();
				artistInfoList.setAdapter(musicInfoListAdapter);
				artistInfoList.setOnItemClickListener(new OnItemClickListener() {
		        	
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						MusicPlay(position);
					}
				});
			}
		});
    }
    
    public void getArtistInfos(String selection, String[] selectionArgs, String sortOrder){
    	getMusicInfos(selection, selectionArgs, sortOrder);
    	
    	musicMapList.clear();
    	
    	Set<String>artistNameSet = new TreeSet<String>(collator);
    	for(int i=0; i<musicInfos.size(); ++i){
        	artistName = musicInfos.get(i).getArtist();
        	artistNameSet.add(artistName);
        }
    	
    	int artistCountArray[] = new int[artistNameSet.size()];
    	int index = 0;
    	for(Iterator<String>iter = artistNameSet.iterator(); iter.hasNext();){
    		String artistNameInSet = iter.next();
    		for(int i=0; i<musicInfos.size(); ++i){
            	artistName = musicInfos.get(i).getArtist();
            	if(artistNameInSet.equals(artistName)){
            		artistCountArray[index] += 1;
            	}
            }
    		
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("artist", artistNameInSet);
    		map.put("count", artistCountArray[index]+" 首 ");
    		musicMapList.add(map);
        	++index;
    	}
    }
    
    public void getArtistInfoListAdapter(){
    	musicInfoListAdapter = new SimpleAdapter(mContext, musicMapList, R.layout.musicinfo_item_layout,
                new String[]{"artist", "count"},
                new int[]{R.id.left_top, R.id.left_bottom});
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
    
    public void getMusicInfos(String selection, String[] selectionArgs, String sortOrder){
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

    public void getMusicInfoListAdapter(){
    	musicInfoListAdapter = new SimpleAdapter(mContext, musicMapList, R.layout.musicinfo_item_layout,
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
    
    public void MusicPlay(int position){
    	isMusicPlaying = false;
    	int totalTime = musicInfos.get(position).getDuration();
		positionPlay = position;
		musicPlaySeekBar.setMax(totalTime);
		songPath = musicInfos.get(position).getPath();
		File songFile = new File(songPath);
		if(!songFile.exists()){
			Toast.makeText(mContext, "The music file doesn't exists, already updated music list.", Toast.LENGTH_SHORT).show();
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
    
    public void setMusicViewInfos(){
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
	        	
	        	MusicPlay((positionPlay+1)%musicInfos.size());
			}
		});
		musicPlayPause.setBackgroundResource(R.drawable.music_to_start);
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
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	
    	isMusicPlaying = false;
    	musicMapList.clear();
    	musicMapList = null;
    	viewPagerContainter.clear();
    	viewPagerContainter = null;
    	
    	if(mediaPlayer != null){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
    }
}