# EasyMusic
A easy music playing tool

2016.05.08
1. create project
2. search music, album, artist
3. show search result to fragment with list
4. play music, next, previous, pause
5. use seek bar, name, time to track current playing

2016.05.11
1. show list from fragment to view pager

2016.05.12
1. add action android.media.action.MEDIA_PLAY_FROM_SEARCH to EasyMusicMainActivity's intent-filter
2. add calling MediaPlayer.pause() in onDestroy()
3. change calling MediaPLayer.release() to reset() in MusicPlay()
4. change calling MediaPLayer.prepare() to prerareAsync() in MusicPlay(), need first listening MediaPlayer.OnPreparedListener
 