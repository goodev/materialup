package org.goodev.material.model;

import android.content.res.ColorStateList;
import android.databinding.BaseObservable;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;

import org.goodev.material.R;
import org.goodev.material.api.Api;
import org.goodev.material.util.HtmlUtils;
import org.parceler.Parcel;
import org.parceler.Transient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yfcheng on 2015/11/26.
 */
@Parcel
public class Post extends BaseObservable {
    /**
     * if the post id is this, means this is a date divider
     */
    public static final long DATE_TYPE = -100;
    public static final String DRIBBBLE = "dribbble";
    public static final String CODEPEN = "codepen";
    public static final String BEHANCE = "behance";
    public static final String BLOGSPOT = "blogspot";
    public static final String GITHUB = "github";
    public static final String GOOGLEPLUS = "google+";
    public static final String GOOGLEPLAY = "google play";
    public static final String VIMEO = "vimeo";
    public static final String YOUTUBE = "youtube";
    public static final String TWITTER = "twitter";
    public static final String PINTEREST = "pinterest";
    public static final String MEDIUM = "medium";
    public long id;
    public String title;
    public String imageUrl;
    /**
     * 800 X 600
     */
    public String previewUrl;
    /**
     * 400 X 300
     */
    public String teaserUrl;
    public String redirect;
    public String source;
    public String avatarUrl;
    public String userUrl;
    public String userName;
    public String statusCount;
    public String statusLabel;
    public String description;
    public String createTime;
    public String price;
    public boolean gif;
    public List<User> upvoters;
    public boolean hasComments;
    public String url;
    public int votes;
    public boolean voted;
    public int background;
    @Transient
    private Spanned parsedDescription;

    public List<User> getUpvoters() {
        return upvoters;
    }

    public void addUpvoter(User upvoter) {
        if (upvoters == null) {
            upvoters = new ArrayList<>();
        }

        upvoters.add(upvoter);
    }

    public void setUpvoters(List<User> upvoters) {
        this.upvoters = upvoters;
    }

    public boolean hasDes() {
        return !TextUtils.isEmpty(description);
    }

    public boolean showCircleLabel() {
        return !TextUtils.isEmpty(getCircleLabel());
    }

    public String getCircleLabel() {
        if (votes > 0) {
            return String.valueOf(votes);
        } else {
            return price;
        }
    }

    public CharSequence getPrice() {
        if(price != null && price.lastIndexOf("$") > 0) {
            int i = price.lastIndexOf("$");
            SpannableStringBuilder ssb = new SpannableStringBuilder(price);
            ssb.setSpan(new ForegroundColorSpan(Color.GRAY),0,i-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new StrikethroughSpan(),0,i-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ssb;
        }
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isGif() {
        return gif;
    }

    public void setGif(boolean gif) {
        this.gif = gif;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isHasComments() {
        return hasComments;
    }

    public void setHasComments(boolean hasComments) {
        this.hasComments = hasComments;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public String getTeaserUrl() {
        return teaserUrl;
    }

    public void setTeaserUrl(String teaserUrl) {
        this.teaserUrl = teaserUrl;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatusCount() {
        return statusCount;
    }

    public void setStatusCount(String statusCount) {
        this.statusCount = statusCount;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public boolean hasName() {
        return !TextUtils.isEmpty(userName);
    }

    public boolean hasSource() {
        return !TextUtils.isEmpty(source);
    }

    public int getSourceIcon() {
        if (TextUtils.isEmpty(source)) {
            return R.drawable.web;
        }
        if (source.contains(DRIBBBLE)) {
            return R.drawable.dribbble;
        } else if (source.contains(CODEPEN)) {
            return R.drawable.codepen;
        } else if (source.contains(BEHANCE)) {
            return R.drawable.behance;
        } else if (source.contains(BLOGSPOT)) {
            return R.drawable.blogger1;
        } else if (source.contains(GITHUB)) {
            return R.drawable.github;
        } else if (source.contains(GOOGLEPLUS)) {
            return R.drawable.gplus;
        } else if (source.contains(VIMEO)) {
            return R.drawable.vimeo;
        } else if (source.contains(YOUTUBE)) {
            return R.drawable.youtube;
        } else if (source.contains(TWITTER)) {
            return R.drawable.twitter;
        } else if (source.contains(PINTEREST)) {
            return R.drawable.pinterest;
        } else if (source.contains(MEDIUM)) {
            return R.drawable.medium;
        }
        return R.drawable.web;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", source='" + source + '\'' +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                ", teaserUrl='" + teaserUrl + '\'' +
                ", redirect='" + redirect + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", userUrl='" + userUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", statusCount='" + statusCount + '\'' +
                ", statusLabel='" + statusLabel + '\'' +
                ", url='" + url + '\'' +
                ", votes=" + votes +
                '}';
    }

    public Spanned getParsedDescription(ColorStateList linkTextColor,
                                        @ColorInt int linkHighlightColor) {
        if (parsedDescription == null && !TextUtils.isEmpty(description)) {
            parsedDescription = HtmlUtils.parseHtml(description, linkTextColor, linkHighlightColor);
        }
        return parsedDescription;
    }

    public String getFullUrl() {
        String t = url.startsWith("/") ? Api.getEndpoint() + url : url;
        return t;
    }
}
