package com.vincent.easymusic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.vincent.easymusic.R;
import com.vincent.easymusic.info.MusicInfo;
import com.vincent.easymusic.utils.Utils;
import com.vincent.easymusic.fragment.*;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "ResourceAsColor", "InflateParams" })
public class EasyMusicMainActivity extends FragmentActivity{
	
	public static Uri uri = null;
	public static String songPath = null;
	public static MediaPlayer mediaPlayer = null;
	
	public static List<MusicInfo> musicInfo = null;
	
	public static List<Fragment> fragmentList = null;
	public static Fragment localMusicListFragment = null;
	public static Fragment localAlbumListFragment = null;
	public static Fragment localArtistListFragment = null;
	
	public static boolean isMusicPlaying = false;
	public static int positionPlay = 0;
	public static int indexViewPager = 0;
	
	public static int buttonPressColor = 0;
	public static int buttonNormalColor = 0;
	
	private static int offsetCursor = 0;
	private static int widthCursor = 0;

	private static ImageView imageViewCursor = null;
	private static SeekBar musicPlaySeekBar;
	private static TextView musicPlayName = null;
	private static TextView musicTimePlay = null;
	private static TextView musicTimeEnd = null;
	public static ImageView musicPlayPause = null;
	private static ViewPager viewPager = null;
	private static FragmentAdapter fragmentAdapter = null;
	
	public static RemoteViews mRemoteView = null;
	private static NotificationManager mNotificationManager = null;
	private static Notification mNotification = null;
	private static int mNotificationId = 0;
	
	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        Utils.initStatusBarColor(this); 
        
        setContentView(R.layout.easymusic_main_layout);
        
        Utils.mContext = this;
        
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    	scanIntent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
    	Utils.mContext.sendBroadcast(scanIntent);
        
        isMusicPlaying = false;
    	positionPlay = 0;
    	indexViewPager = 0;
        
        fragmentList = new ArrayList<Fragment>();
        localMusicListFragment = new LocalMusicListFragment();
        localAlbumListFragment = new LocalAlbumListFragment();
        localArtistListFragment = new LocalArtistListFragment();
        
        initMusicViews();

        setViewPagerChangeListener();

        setSeekBarOnClickListener();
		setSeekBarMoveListener();
    }

    //action for title button click
    public void LocalTypeSelection(View localView){
    	int viewId = localView.getId();
    	switch (viewId) {
		case R.id.local_music_title:
			indexViewPager = 0;
			break;
		case R.id.local_album_title:
			indexViewPager = 1;
			break;
		case R.id.local_artist_title:
			indexViewPager = 2;
			break;
		default:
			break;
		}
		viewPager.setCurrentItem(indexViewPager);
		
		if(indexViewPager == 0){
			LocalMusicListFragment.initListView();
		}else if(indexViewPager == 1){
			LocalAlbumListFragment.initAlbumInfoListView();
		}else if(indexViewPager == 2){
			LocalArtistListFragment.initArtistInfoListView();
		}
    }
    
	public void initMusicViews(){
    	viewPager = (ViewPager) findViewById(R.id.musicinfo_list_viewpager);
        
        musicPlaySeekBar = (SeekBar)findViewById(R.id.music_play_seekbar);
        musicTimePlay = (TextView)findViewById(R.id.music_time_play);
        musicPlayName = (TextView)findViewById(R.id.music_play_name);
        musicTimeEnd = (TextView)findViewById(R.id.music_time_end);
        musicPlayPause = (ImageView)findViewById(R.id.music_play_pause);
       
        fragmentList.add(localMusicListFragment);
        fragmentList.add(localAlbumListFragment);
        fragmentList.add(localArtistListFragment);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(fragmentAdapter);
        
        imageViewCursor= (ImageView) findViewById(R.id.cursor);
        
        widthCursor = imageViewCursor.getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offsetCursor = (screenW / fragmentList.size() - widthCursor - 4) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offsetCursor, 0);
        imageViewCursor.setImageMatrix(matrix);

        musicPlaySeekBar.setProgress(0);
        musicTimePlay.setText("0:00");
        musicTimeEnd.setText("0:00");
        
    	mNotificationManager = (NotificationManager)Utils.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    	mNotificationId = (int) System.currentTimeMillis();
    	mNotification = createNotification();
    }
    
    @SuppressWarnings("deprecation")
	public void setViewPagerChangeListener(){
    	final int baseOffset = offsetCursor * 2 + widthCursor;
    	
    	viewPager.setOnPageChangeListener(new OnPageChangeListener(){
    		
        	@Override
        	public void onPageScrollStateChanged(int arg0){
        	}

        	@Override
        	public void onPageScrolled(int arg0, float arg1, int arg2){
        	}

        	@Override
        	public void onPageSelected(int arg0){
        		Animation animation = new TranslateAnimation(baseOffset*indexViewPager, baseOffset*arg0, 0, 0);
                animation.setFillAfter(true);
                animation.setDuration(300);
                imageViewCursor.startAnimation(animation);
                
        		indexViewPager = arg0;
        		
        		if(indexViewPager == 0){
        			LocalMusicListFragment.initListView();
        		}else if(indexViewPager == 1){
        			LocalAlbumListFragment.initAlbumInfoListView();
        		}else if(indexViewPager == 2){
        			LocalArtistListFragment.initArtistInfoListView();
        		}
        	}
        });
    }
    
    public void setSeekBarOnClickListener(){
    	musicPlaySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onStopTrackingTouch(SeekBar seekBar){
				if(mediaPlayer != null){
					mediaPlayer.seekTo(seekBar.getProgress());
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar){
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				float duration = progress/60.0f/1000.0f;
	        	int pre = (int)duration;
	        	int suf = (int)((duration-pre)*60);
	        	musicTimePlay.setText(String.valueOf(pre)+":"+Utils.decimalFormat.format(suf));
			}
		});
    }
    
    public void setSeekBarMoveListener(){
    	new Thread(new Runnable(){
			
			@Override
			public void run(){
				while (true){
	            	try{
						Thread.sleep(500);
						if(isMusicPlaying){
							musicPlaySeekBar.setProgress(mediaPlayer.getCurrentPosition());
						}
					} catch (InterruptedException e){
						e.printStackTrace();
					}
	            }
			}
		}).start();
    }
    
    public void MusicPlayControl(View playView){
    	if(musicInfo == null || musicInfo.size() == 0){
    		Toast.makeText(Utils.mContext, "There has no musics now.", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	int id = playView.getId();
    	switch (id){
		case R.id.music_play_next:
			MusicPlay((positionPlay+1)%musicInfo.size());
			break;
		case R.id.music_play_pre:
			MusicPlay((musicInfo.size()+positionPlay-1)%musicInfo.size());
			break;
		case R.id.music_play_pause:
			if(isMusicPlaying){
				isMusicPlaying = false;
				mediaPlayer.pause();
				musicPlayPause.setBackgroundResource(R.drawable.music_to_pause);
				updateNotification(musicInfo.get(positionPlay).getTitle(), musicInfo.get(positionPlay).getArtist(), true, true);
			}else{
				if(mediaPlayer != null){
					songPath = musicInfo.get(positionPlay).getPath();
					File songFile = new File(songPath);
					if(!songFile.exists()){
						UpdateMusicInfo(positionPlay);
						return;
					}
					isMusicPlaying = true;
					mediaPlayer.start();
					musicPlayPause.setBackgroundResource(R.drawable.music_to_start);
					updateNotification(musicInfo.get(positionPlay).getTitle(), musicInfo.get(positionPlay).getArtist(), false, true);
				}else{
					MusicPlay(positionPlay);
				}
			}
			break;
		default:
			break;
		}
    }
	
    public static void MusicPlay(final int position){
    	isMusicPlaying = false;
    	if(musicInfo == null || musicInfo.size() == 0){
    		Toast.makeText(Utils.mContext, "There has no musics now.", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	int totalTime = musicInfo.get(position).getDuration();
		musicPlaySeekBar.setMax(totalTime);
		songPath = musicInfo.get(position).getPath();
		File songFile = new File(songPath);
		if(!songFile.exists()){
			UpdateMusicInfo(position);
			return;
		}
		uri = Uri.fromFile(songFile);
		try{
			if(mediaPlayer != null){
				if(mediaPlayer.isPlaying()){
					mediaPlayer.pause();
					mediaPlayer.stop();
				}
				mediaPlayer.reset();
				mediaPlayer = null;
			}
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(Utils.mContext, uri);
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
					isMusicPlaying = true;
					positionPlay = position;
					setMusicViewInfos();
					updateNotification(musicInfo.get(positionPlay).getTitle(), musicInfo.get(positionPlay).getArtist(), false, true);
				}
			});
			mediaPlayer.prepareAsync();
		} catch (IllegalStateException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
    }
    
    public static void UpdateMusicInfo(int position){
    	Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    	scanIntent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
    	Utils.mContext.sendBroadcast(scanIntent);
    
    	Toast.makeText(Utils.mContext, "The music file doesn't exists, already updated music list.", Toast.LENGTH_SHORT).show();
		if(mediaPlayer != null && mediaPlayer.isPlaying() && positionPlay != position){
			isMusicPlaying = true;
			if(positionPlay > position){
				positionPlay -= 1;
			}
		}else{
			if(mediaPlayer != null){
				if(mediaPlayer.isPlaying()){
					mediaPlayer.pause();
					mediaPlayer.stop();
				}
				mediaPlayer.reset();
				mediaPlayer = null;
			}
			musicPlayPause.setBackgroundResource(R.drawable.music_to_pause);
			musicPlaySeekBar.setProgress(0);
	        musicTimePlay.setText("0:00");
	        musicTimeEnd.setText("0:00");
	        musicPlayName.setText("");
			if(positionPlay == musicInfo.size()-1){
				positionPlay = 0;
			}
		}
		if(Utils.isPlayingInMusicList){
			LocalMusicListFragment.updateMusicInfoListAdapter(position);
		}else if(Utils.isPlayingInAlbumMusicList){
			LocalAlbumListFragment.updateAlbumInfoListAdapter(position);
		}else if(Utils.isPlayingInArtistMusicList){
			LocalArtistListFragment.updateArtistInfoListAdapter(position);
		}
    }
    
    public static void setMusicViewInfos(){
    	musicPlayName.setText(musicInfo.get(positionPlay).getTitle());
    	float duration = (float) (musicInfo.get(positionPlay).getDuration()/60.0/1000.0);
    	int pre = (int)duration;
    	float suf = (duration-pre)*60;
        musicTimeEnd.setText(String.valueOf(pre)+":"+Utils.decimalFormat.format(suf));
		mediaPlayer.setOnCompletionListener(new OnCompletionListener(){
			
			@Override
			public void onCompletion(MediaPlayer mp){
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
	        	musicTimePlay.setText(String.valueOf(pre)+":"+Utils.decimalFormat.format(suf));
	        	if(musicInfo == null || musicInfo.size() == 0){
	        		Toast.makeText(Utils.mContext, "There has no musics now.", Toast.LENGTH_SHORT).show();
	        		return;
	        	}
	        	MusicPlay((positionPlay+1)%musicInfo.size());
			}
		});
		musicPlayPause.setBackgroundResource(R.drawable.music_to_start);
    }
    
    public class FragmentAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragmentList;
        
        public FragmentAdapter(FragmentManager fragmentManager, List<Fragment> fragmentListArg){
            super(fragmentManager);
            fragmentList = fragmentListArg;
        }
        
        @Override
        public Fragment getItem(int arg0){
            return fragmentList.get(arg0);
        }
        
        @Override
        public int getCount(){
            return fragmentList.size();
        }
    } 
	
    public Notification createNotification(){
		NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.mContext);

		Intent main = new Intent(Utils.mContext, EasyMusicMainActivity.class);
		PendingIntent mainPi = PendingIntent.getActivity(Utils.mContext, 0, main, 0);
		
		Intent play = new Intent(Utils.ACTION_CONTROL_PLAY_PAUSE);
		PendingIntent playPi = PendingIntent.getBroadcast(Utils.mContext, 0, play, 0);
		
		Intent next = new Intent(Utils.ACTION_CONTROL_PLAY_NEXT);
		PendingIntent nextPi = PendingIntent.getBroadcast(Utils.mContext, 0, next, 0);
		
		Intent pre = new Intent(Utils.ACTION_CONTROL_PLAY_PRE);
		PendingIntent prePi = PendingIntent.getBroadcast(Utils.mContext, 0, pre, 0);
		
		mRemoteView = new RemoteViews(Utils.mContext.getPackageName(), R.layout.notify_show_playmusic);
		mRemoteView.setOnClickPendingIntent(R.id.img_notifyIcon, mainPi);
		mRemoteView.setOnClickPendingIntent(R.id.img_notifyPlayOrPause, playPi);
		mRemoteView.setOnClickPendingIntent(R.id.img_notifyNext, nextPi);
		mRemoteView.setOnClickPendingIntent(R.id.img_notifyPre, prePi);
		
		builder.setContent(mRemoteView).setSmallIcon(R.drawable.launcher_icon)
				.setContentTitle("Title").setContentText("Artist")
				.setContentIntent(mainPi);
		
		return builder.build();
	}
    
	public static void updateNotification(String title, String artist, boolean isPlaying, boolean hasNext){
		if(!TextUtils.isEmpty(title)){
			mRemoteView.setTextViewText(R.id.txt_notifyMusicName, title);
		}
		if(!TextUtils.isEmpty(artist)){
			mRemoteView.setTextViewText(R.id.txt_notifyNickName, artist);
		}
		if(isPlaying){
			mRemoteView.setImageViewResource(R.id.img_notifyPlayOrPause, R.drawable.ic_pause);
		}else{
			mRemoteView.setImageViewResource(R.id.img_notifyPlayOrPause, R.drawable.ic_play);
		}
		mNotificationManager.notify(mNotificationId, mNotification);
	}
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	
    	isMusicPlaying = false;
    	
    	if(musicInfo != null){
			musicInfo.clear();
			musicInfo = null;
		}
    	if(fragmentList != null){
	    	fragmentList.clear();
	    	fragmentList = null;
    	}
    	localMusicListFragment = null;
    	localAlbumListFragment = null;
    	localArtistListFragment = null;
    	
    	if(mediaPlayer != null){
    		if(mediaPlayer.isPlaying()){
	    		mediaPlayer.pause();
				mediaPlayer.stop();
    		}
			mediaPlayer.release();
			mediaPlayer = null;
		}
    }
}