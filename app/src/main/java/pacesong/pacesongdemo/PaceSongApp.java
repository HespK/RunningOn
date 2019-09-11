package pacesong.pacesongdemo;

import android.app.Application;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Application setting - songs storage, logic for persist/resore and get next song to play
 */

public class PaceSongApp extends Application {



    //crutch due to restore app data after restart by android
    static {
        restoreSongs();
    }

    public static List<Song> songs = new ArrayList<>();
    public static List<Song> allSongs = new ArrayList<>();
    public static List<Song> slowSongs = new ArrayList<>();
    public static List<Song> mediumSongs = new ArrayList<>();
    public static List<Song> fastSongs = new ArrayList<>();

    //current song category
    public static Song.SongType currentType = Song.SongType.LOW;

    /*
     * Randomly get next song from provided type
     */
    public static Song findNext(Song.SongType type) {
        List<Song> songs = new ArrayList<>();
        switch (type) {
            case LOW:
                songs = slowSongs;
                break;

            case MEDIUM:
                songs = mediumSongs;
                break;

            case FAST:
                songs = fastSongs;
                break;
        }
        if (songs.size() != 0) {
            Random generator = new Random();
            int i = generator.nextInt(songs.size());
            return songs.get(i);
        } else {
            return null;
        }
    }

    /*
     * find next song by current category or id it is empty by other category
     * fast ==> no fast songs==>play medium song
     * slow==>no slow song==>play medium song
     * medium==> no medium song ==>play fast song
     */
    public static Song findNext() {
        Song song = findNext(currentType);

        if (song != null) {
            return song;
        } else {
            switch (currentType) {
                case LOW:
                    if (mediumSongs.size() != 0) {
                        song = findNext(Song.SongType.MEDIUM);
                    } else {
                        song = findNext(Song.SongType.FAST);
                    }
                    break;

                case MEDIUM:
                    if (fastSongs.size() != 0) {
                        song = findNext(Song.SongType.FAST);
                    } else {
                        song = findNext(Song.SongType.LOW);
                    }
                    break;

                case FAST:
                    if (mediumSongs.size() != 0) {
                        song = findNext(Song.SongType.MEDIUM);
                    } else {
                        song = findNext(Song.SongType.LOW);
                    }
                    break;
            }
            return song;
        }
    }

    /*
     * save songs to temp file to restore them after app restart
     */
    public static void persistSongs() {
        List<Song> songs = new ArrayList<>();

        songs.addAll(slowSongs);
        songs.addAll(mediumSongs);
        songs.addAll(fastSongs);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("songs.tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(songs);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //restore songs from temp file
    private static void restoreSongs() {
        try {
            FileInputStream fis = new FileInputStream("songs.tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            songs = (List<Song>) ois.readObject();
            slowSongs = new ArrayList<>();
            mediumSongs = new ArrayList<>();
            fastSongs = new ArrayList<>();
            for (Song song : songs) {
                switch (song.getType()) {
                    case LOW:
                        slowSongs.add(song);
                        break;

                    case MEDIUM:
                        mediumSongs.add(song);
                        break;

                    case FAST:
                        fastSongs.add(song);
                        break;
                }
            }
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setSlowSongs() {
        slowSongs = new ArrayList<>();
        mediumSongs = new ArrayList<>();
        for (Song song : songs) {
            Song copy = new Song(song.getName());
            if (song.isSelected()) {
                copy.setType(Song.SongType.LOW);
                slowSongs.add(copy);
            } else {
                mediumSongs.add(copy);
            }
        }
    }

    public static void setMediumAndFastSongsAndPersist() {
        fastSongs = new ArrayList<>();
        List<Song> mediumSongs = PaceSongApp.mediumSongs;
        PaceSongApp.mediumSongs = new ArrayList<>();
        for (Song song : mediumSongs) {
            Song copy = new Song(song.getName());
            if (song.isSelected()) {
                copy.setType(Song.SongType.MEDIUM);
                PaceSongApp.mediumSongs.add(copy);
            } else {
                copy.setType(Song.SongType.FAST);
                fastSongs.add(copy);
            }
        }

        persistSongs();
    }

    /*
     * called when for songs which user wants to classify
     */
    public static void setSelectedSongs() {
        songs = new ArrayList<>();
        for (Song song : allSongs) {
            if (song.isSelected()) {
                Song copy = new Song(song.getName());
                songs.add(copy);
            }
        }
    }
    
}
