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
5. add 3 title views to viewTitleContainter as viewPagerContainter to control background color

2016.05.13
1. add cursor line to indicate current page, remove background color with title
2. add monitor for music file modify
3. change 3 page return from list to fragment
4. split model, view, control to cast MVC
5. hide click action of album, artist list item 
 
 2016.05.14
 1. resume click action of album, artist list item, like as music
 2. initial first grade list view again after page was changed
 3. click album or artist title button will go back to first grade list if current list is album-music or artist-music

2016.05.15
1. set color for global, status bar, action bar,  list, list item press & normal background
2. remove action bar  temporarily

2016.05.16
1. fix bug--play last music in music list->go to 2 or 3 page->remove the playing music file->go back to 1 page->pause/play the music->crash--out index of music list
2. update album or artist after remove music file when current page is 2 or 3
3. add music play notification, pause/play, next, previous

2016.05.17
1. add long click action for list music item to remove the music file, not support album or artist
2. seal music info judge and pause/play sentence to isMusicInfoEmpty() and actionPauseOrPlay()