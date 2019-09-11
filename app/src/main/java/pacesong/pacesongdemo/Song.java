package pacesong.pacesongdemo;

import java.io.Serializable;

/**
 * model song class which contains song full name which is full path
 * short name which is only song name
 * song type - slow, medium or fast. null if a user doesn't classified the song yet
 * isSelected - this parameter changes when user select/deselect song on Choose song screens
 */

public class Song implements Serializable {
    private String name;
    private String shortName;
    private SongType type;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Song(String name) {
        this.name = name;
        int slashIndex = name.lastIndexOf("/");
        if (slashIndex != -1) {
            shortName = name.substring(slashIndex + 1);
        }
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public SongType getType() {
        return type;
    }

    public void setType(SongType type) {
        this.type = type;
    }

    /*
     * Song categories
     */
    enum SongType {
        LOW("Low"), MEDIUM("Medium"), FAST("Fast");

        private String name;

        SongType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
