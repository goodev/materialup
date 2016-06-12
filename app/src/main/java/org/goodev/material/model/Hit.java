package org.goodev.material.model;

import org.goodev.material.api.JsoupUtil;

/**
 * Created by yfcheng on 2015/12/15.
 */
public class Hit {
    public String name;
    public String label;
    public String category_name;
    public String category_friendly_name;
    public String subcategory_name;
    public String subcategory_friendly_name;
    public String maker_full_name;
    public String maker_nickname;
    public String tags;
    public String description;
    public String platform_friendly_name;
    public String teaser_url;
    public String link_url;
    public String link_path;
    public String background_color;
    public long id;
    public int upvotes_count;
    public String objectID;

    public int getBackground() {
        return JsoupUtil.parseColor(background_color);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_friendly_name() {
        return category_friendly_name;
    }

    public void setCategory_friendly_name(String category_friendly_name) {
        this.category_friendly_name = category_friendly_name;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getSubcategory_friendly_name() {
        return subcategory_friendly_name;
    }

    public void setSubcategory_friendly_name(String subcategory_friendly_name) {
        this.subcategory_friendly_name = subcategory_friendly_name;
    }

    public String getMaker_full_name() {
        return maker_full_name;
    }

    public void setMaker_full_name(String maker_full_name) {
        this.maker_full_name = maker_full_name;
    }

    public String getMaker_nickname() {
        return maker_nickname;
    }

    public void setMaker_nickname(String maker_nickname) {
        this.maker_nickname = maker_nickname;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlatform_friendly_name() {
        return platform_friendly_name;
    }

    public void setPlatform_friendly_name(String platform_friendly_name) {
        this.platform_friendly_name = platform_friendly_name;
    }

    public String getTeaser_url() {
        return teaser_url;
    }

    public void setTeaser_url(String teaser_url) {
        this.teaser_url = teaser_url;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public String getLink_path() {
        return link_path;
    }

    public void setLink_path(String link_path) {
        this.link_path = link_path;
    }

    public String getBackground_color() {
        return background_color;
    }

    public void setBackground_color(String background_color) {
        this.background_color = background_color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUpvotes_count() {
        return upvotes_count;
    }

    public void setUpvotes_count(int upvotes_count) {
        this.upvotes_count = upvotes_count;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    @Override
    public String toString() {
        return "Hit{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", category_name='" + category_name + '\'' +
//                ", category_friendly_name='" + category_friendly_name + '\'' +
//                ", subcategory_name='" + subcategory_name + '\'' +
//                ", subcategory_friendly_name='" + subcategory_friendly_name + '\'' +
//                ", maker_full_name='" + maker_full_name + '\'' +
//                ", maker_nickname='" + maker_nickname + '\'' +
//                ", tags='" + tags + '\'' +
                ", description='" + description + '\'' +
//                ", platform_friendly_name='" + platform_friendly_name + '\'' +
                ", teaser_url='" + teaser_url + '\'' +
                ", link_url='" + link_url + '\'' +
                ", link_path='" + link_path + '\'' +
                ", background_color='" + background_color + '\'' +
                ", id=" + id +
                ", upvotes_count=" + upvotes_count +
                ", objectID='" + objectID + '\'' +
                '}';
    }

    public Post toPost() {
        Post post = new Post();
        post.setUserName(getMaker_full_name());
        post.background = getBackground();
        post.title = getName();
        post.description = getDescription();
        post.url = getLink_path();
        post.id = getId();
        post.imageUrl = post.teaserUrl = getTeaser_url();
        return post;
    }
}
