package org.goodev.material.model;

import android.databinding.BaseObservable;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import org.goodev.material.util.HtmlUtils;
import org.parceler.Parcel;
import org.parceler.Transient;

import java.util.Date;

/**
 * Created by yfcheng on 2015/11/30.
 */
@Parcel
public class Comment extends BaseObservable {
    public long id;
    public String body;
    public String htmlBody;
    public Date createdAt;
    public String relativeCreatedAt;
    public boolean persisted;
    public int commentLikesCount;
    public long postId;
    public boolean liked;
    public boolean deleted;
    public User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getRelativeCreatedAt() {
        return relativeCreatedAt;
    }

    public void setRelativeCreatedAt(String relativeCreatedAt) {
        this.relativeCreatedAt = relativeCreatedAt;
    }

    public boolean isPersisted() {
        return persisted;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

    public int getCommentLikesCount() {
        return commentLikesCount;
    }

    public void setCommentLikesCount(int commentLikesCount) {
        this.commentLikesCount = commentLikesCount;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Transient
    private Spanned parsedBody;

    public Spanned getParsedBody(TextView textView) {
        if (parsedBody == null && !TextUtils.isEmpty(body)) {
            parsedBody = HtmlUtils.parseHtml(body, textView.getLinkTextColors(), textView
                    .getHighlightColor());
        }
        return parsedBody;
    }
}
