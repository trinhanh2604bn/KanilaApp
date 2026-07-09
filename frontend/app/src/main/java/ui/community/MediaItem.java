package ui.community;

import android.net.Uri;

public class MediaItem {
    private final Uri uri;
    private final boolean isVideo;
    private boolean selected;

    public MediaItem(Uri uri, boolean isVideo) {
        this.uri = uri;
        this.isVideo = isVideo;
        this.selected = false;
    }

    public Uri getUri() {
        return uri;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaItem mediaItem = (MediaItem) o;
        return uri.equals(mediaItem.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }
}
