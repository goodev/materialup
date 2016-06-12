package org.goodev.material.model;

import org.goodev.material.api.JsoupUtil;
import org.parceler.Parcel;

/**
 * Created by yfcheng on 2015/12/5.
 */
@Parcel
public class Collection {
    public long id;
    public String name;
    public String description;
    public String path;
    public String slug;
    public String teaserUrl;
    public long[] postIds;
    public String image1;
    public String image2;
    public String image3;
    public boolean followed;
    public String userPath;
    public String userAvatar;
    public int count;
    public String countText;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long[] getPostIds() {
        return postIds;
    }

    public void setPostIds(long[] postIds) {
        this.postIds = postIds;
    }

    public String getCountText() {
        return countText;
    }

    public void setCountText(String countText) {
        this.countText = countText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        if (path != null && path.startsWith(JsoupUtil.PRE_COLLECTIONS)) {
            this.slug = path.substring(JsoupUtil.PRE_COLLECTIONS.length());
        }
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTeaserUrl() {
        return teaserUrl;
    }

    public void setTeaserUrl(String teaserUrl) {
        this.teaserUrl = teaserUrl;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public String getUserPath() {
        return userPath;
    }

    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
