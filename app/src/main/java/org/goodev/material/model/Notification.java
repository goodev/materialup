package org.goodev.material.model;

import org.parceler.Parcel;

/**
 * Created by yfcheng on 2015/12/22.
 */
@Parcel
public class Notification {
    public String postPath;
    public boolean viewed;
    public String userAvatar;
    public String username;
    public String message;
    public String timestamp;

    public Post toPost() {
        Post post = new Post();
        post.setUserName(username);
        post.url = postPath;
        return post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        if (postPath != null ? !postPath.equals(that.postPath) : that.postPath != null)
            return false;
        if (!username.equals(that.username)) return false;
        return timestamp.equals(that.timestamp);

    }

    @Override
    public int hashCode() {
        int result = postPath != null ? postPath.hashCode() : 0;
        result = 31 * result + username.hashCode();
        result = 31 * result + timestamp.hashCode();
        return result;
    }
}
