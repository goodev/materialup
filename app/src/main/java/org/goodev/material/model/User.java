package org.goodev.material.model;

import android.databinding.BaseObservable;
import android.text.Spanned;

import org.goodev.material.util.UI;
import org.parceler.Parcel;
import org.parceler.Transient;

/**
 * Created by yfcheng on 2015/11/30.
 */
@Parcel
public class User extends BaseObservable {
    public long id;
    public String avatarUrl;
    public String teaserUrl;
    public String fullName;
    public String twitterUsername;
    public String path;
    public boolean moderator;
    public boolean verified;
    public boolean admin;

    public String getNameWithId() {
        return nameWithId;
    }

    public void setNameWithId(String nameWithId) {
        this.nameWithId = nameWithId;
    }

    public String nameWithId;
    public String website;
    public String twitter;
    public String dribbble;
    public String behance;
    public String github;
    public String google;
    public String codepen;
    public String slack;
    public String headline;
    public String location;
    public boolean followed;
    ///goo_dev
    public int upvoted;
    public int created;
    public int showcased;
    public int collections;
    public int followers;
    public int following;

    @Transient
    private Spanned parsedDescription;

    public Spanned getParsedText() {
        if (parsedDescription == null) {
            parsedDescription = UI.getParsedText(headline);
        }
        return parsedDescription;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getTeaserUrl() {
        return teaserUrl;
    }

    public void setTeaserUrl(String teaserUrl) {
        this.teaserUrl = teaserUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getDribbble() {
        return dribbble;
    }

    public void setDribbble(String dribbble) {
        this.dribbble = dribbble;
    }

    public String getBehance() {
        return behance;
    }

    public void setBehance(String behance) {
        this.behance = behance;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getGoogle() {
        return google;
    }

    public void setGoogle(String google) {
        this.google = google;
    }

    public String getCodepen() {
        return codepen;
    }

    public void setCodepen(String codepen) {
        this.codepen = codepen;
    }

    public String getSlack() {
        return slack;
    }

    public void setSlack(String slack) {
        this.slack = slack;
    }

    public int getUpvoted() {
        return upvoted;
    }

    public void setUpvoted(int upvoted) {
        this.upvoted = upvoted;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getShowcased() {
        return showcased;
    }

    public void setShowcased(int showcased) {
        this.showcased = showcased;
    }

    public int getCollections() {
        return collections;
    }

    public void setCollections(int collections) {
        this.collections = collections;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", fullName='" + fullName + '\'' +
                ", twitterUsername='" + twitterUsername + '\'' +
                ", path='" + path + '\'' +
                ", moderator=" + moderator +
                ", verified=" + verified +
                ", admin=" + admin +
                ", nameWithId='" + nameWithId + '\'' +
                ", website='" + website + '\'' +
                ", twitter='" + twitter + '\'' +
                ", dribbble='" + dribbble + '\'' +
                ", behance='" + behance + '\'' +
                ", github='" + github + '\'' +
                ", google='" + google + '\'' +
                ", codepen='" + codepen + '\'' +
                ", slack='" + slack + '\'' +
                ", headline='" + headline + '\'' +
                ", location='" + location + '\'' +
                ", followed=" + followed +
                ", upvoted=" + upvoted +
                ", created=" + created +
                ", showcased=" + showcased +
                ", collections=" + collections +
                ", followers=" + followers +
                ", following=" + following +
                '}';
    }

    public static String getUserId(String path) {
        if (path.startsWith("/") && !path.endsWith("/")) {
            return path.substring(1);
        }

        int start = 0;
        int end = 0;
        if (path.endsWith("/")) {
            end = path.length() - 1;
            start = path.lastIndexOf("/", end - 1);
        } else {
            end = path.length();
            start = path.lastIndexOf("/", end);
        }
        try {
            return path.substring(start + 1, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
