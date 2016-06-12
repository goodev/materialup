package org.goodev.material.api;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.ResponseBody;

import org.goodev.material.App;
import org.goodev.material.model.Collection;
import org.goodev.material.model.MuResponse;
import org.goodev.material.model.Notification;
import org.goodev.material.model.Post;
import org.goodev.material.model.User;
import org.goodev.material.util.L;
import org.goodev.material.util.Pref;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yfcheng on 2015/12/3.
 */
public class JsoupUtil {
    public static final String PRE_COLLECTIONS = "/collections/";
    private static final String BG_COLOR = "background-color:";
    private static final int START = BG_COLOR.length();
    public static final String OK = "ok";
    private static final String TAG = "jsoup";

    public static boolean isLogin(MuResponse response) {
        return response != null && response.content != null && response.content.contains("notification__header");
    }

    public static boolean isLogin(retrofit.Response<String> response) {

        boolean login = response != null && response.raw().request().urlString().contains("users/settings");
        if (login) {
            if (Pref.get(App.getIns()).getUserName() == null) {
                paserUserInfo(response.body());
            }
        } else {
            Pref.get(App.getIns()).clearUserInfo();
        }

        return login;
    }

    private static void paserUserInfo(String body) {
        Element doc = Jsoup.parse(body);
        Element profile = doc.select(".header__menu-item--profile").first();
        String avatar = attr(profile.select("img").first(), "src");
        String path = attr(profile.select("li a").first(), "href");
        if (path != null && path.startsWith("/")) {
            Api.getApiService().getUserInfo(path.substring(1))
                    .map(responseBody -> JsoupUtil.convertToUser(null, responseBody))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        Pref.get(App.getIns()).setLoginUserInfo(s);
                    }, new ErrorCallback(App.getIns()));
        }
    }

    public static boolean isLogina(retrofit.Response<String> response) {
        if (response.code() >= 300) {
            return false;
        }

        if (response.headers().get("Location") != null) {
            return false;
        }

        return true;
    }

    public static List<User> getUsers(MuResponse mu) {
        List<User> data = new ArrayList<>();
        if (!OK.equalsIgnoreCase(mu.status)) {
            return data;
        }

        final Element document = Jsoup.parse(mu.content);
        final Elements elements = document.select(".user__list-item");
        Element e;
        Element t;

        for (int i = 0; i < elements.size(); i++) {
            e = elements.get(i);
            User user = new User();
            t = e.select(".user__list-item-avatar a").first();
            user.setPath(attr(t, "href"));
            t = e.select(".user__list-item-avatar img").first();
            user.setAvatarUrl(attr(t, "src"));
            t = e.select(".user__list-item-name").first();
            user.setFullName(text(t));
            t = e.select(".user__list-item-headline").first();
            user.setHeadline(html(t));
            t = e.select(".user__list-item-follow .btn-follow").first();
            String style = attr(t, "class");
            user.setFollowed(style != null && style.contains("btn-follow--following"));

            data.add(user);
        }

        return data;
    }

    private static String text(Element e) {
        if (e == null) {
            return null;
        }
        return e.text();
    }

    private static String html(Element e) {
        if (e == null) {
            return null;
        }
        return e.html();
    }

    private static String attr(Element e, String name) {
        if (e == null) {
            return null;
        }
        return e.attr(name);
    }

    public static List<Post> getResources(MuResponse mu) {
        List<Post> cos = new ArrayList<>();
        if (!OK.equalsIgnoreCase(mu.status)) {
            return cos;
        }

        final Element document = Jsoup.parse(mu.content);
        final Elements elements = document.select(".post-list-items .post-list");
        Element e;

        for (int i = 0; i < elements.size(); i++) {
            Post post = new Post();
            e = elements.get(i);
            Element link = e.select(".upvote-link--material").first();
            if (link != null) {
                String clazz = attr(link, "class");
                boolean upvoted = clazz != null && clazz.contains("upvote-link--upvoted");
                post.setVoted(upvoted);
                Element count = link.select(upvoted ? ".count-up" : ".count").first();
                String c = text(count);
                try {
                    post.setVotes(Integer.parseInt(c));
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();
                }
            }

            link = e.select(".center__image-wrapper").first();
            if (link != null) {
                post.setBackground(getColor(attr(link, "style")));
                Element img = link.select("img.post-preview__img").first();
                post.setImageUrl(attr(img, "src"));
            }

            Element content = e.select(".post-list__content").first();
            if (content != null) {
                Element des = content.select(".post-list__content-description").first();
                if (des != null) {
                    link = des.select("a").first();
                    post.setUrl(attr(link, "href"));
                    try {
                        post.setId(Long.parseLong(attr(link, "id")));
//                        Log.e(TAG, "getResources: id "+post.getId() );
                    } catch (NumberFormatException e1) {
                        e1.printStackTrace();
                    }

                    post.setTitle(text(link.select("h2").first()));

                    post.setDescription(html(des.select("p.truncate").first()));
//                    L.w("post--list__source %s", des.select("span").last());
                    post.setRedirect(attr(des.select("a.post-list__source").first(), "href"));
                    String text = text(des.select("span").last());
                    if (text != null) {
                        post.setSource(text.toLowerCase());
                    }
                }
            }

            Element maker = e.select(".post-list__actions .post-maker").first();
            if (maker != null) {
                link = maker.select(".avatar__wrapper").first();
                post.setUserUrl(attr(link, "href"));
                post.setAvatarUrl(attr(maker.select("img.avatar").first(), "src"));
            }

            cos.add(post);
        }

        return cos;
    }

    public static List<Collection> getCollections(MuResponse mu) {
        List<Collection> cos = new ArrayList<>();
        if (!OK.equalsIgnoreCase(mu.status)) {
            return cos;
        }

        final Element document = Jsoup.parse(mu.content);
        final Elements elements = document.select(".collection__list-items .collection__list-item");
        Element e;
        for (int i = 0; i < elements.size(); i++) {
            Collection co = new Collection();
            e = elements.get(i);

            Element img = e.select("img.preview").first();
            if (img != null) {
                co.setTeaserUrl(img.attr("src"));
            }

            Element follow = e.select(".btn-follow").first();
            if (follow != null) {
                String clazz = follow.attr("class");
                if (clazz != null) {
                    co.setFollowed(clazz.contains("btn-follow--following"));
                }
            }

            Element maker = e.select(".post-maker a").first();
            if (maker != null) {
                co.setUserPath(maker.attr("href"));
            }
            maker = e.select(".post-maker img").first();
            if (maker != null) {
                co.setUserAvatar(maker.attr("src"));
            }
            maker = e.select(".truncate a").first();
            if (maker != null) {
                co.setName(maker.text());
                co.setPath(maker.attr("href"));
            }

            maker = e.select("p").last();
            if (maker != null) {
                co.setCountText(maker.text());
            }
            cos.add(co);
        }

        return cos;
    }

    public static List<User> getUpvoters(MuResponse mu) {
        List<User> users = new ArrayList<>();
        if (!OK.equalsIgnoreCase(mu.status)) {
            return users;
        }

        final Element document = Jsoup.parse(mu.content);
        final Elements upvoters = document.select(".post__upvoters .post__upvoter");
        if (upvoters != null && !upvoters.isEmpty()) {
            int size = upvoters.size();
            for (int i = size - 1; i >= 0; i--) {
                Element e = upvoters.get(i);
                Element link = e.select("a").first();
                String path = attr(link, "href");
                if (TextUtils.isEmpty(path)) {
                    continue;
                }
                Element img = e.select("img").first();
                String avatar = attr(img, "src");
                String alt = attr(img, "alt");
                User user = new User();
                user.setAvatarUrl(avatar);
                user.setPath(path);
                users.add(user);
            }
        }

        return users;
    }
    public static Post getPostContent(Post post, MuResponse mu) {
        boolean full = post == null;
        if (full) {
            post = new Post();
        }
        if (!OK.equalsIgnoreCase(mu.status)) {
            return post;
        }

        final Element document = Jsoup.parse(mu.content);
        if (post.imageUrl == null || post.imageUrl.equals(post.teaserUrl)) {
            Element img = document.select(".post__main .post__preview-container").first();
            if (img != null) {
                img = img.select("img.preview").first();
            }
            if (img != null) {
                post.setImageUrl(img.attr("src"));
            }
        }

        //Format is changed!!
        if (true) {
            return post;
        }


        if (full) {
            Element body = document.select("div.post__body").first();
            if (body != null) {
                Element id = body.select("div").first();
                if (id != null && id.hasAttr("id")) {
                    try {
                        post.setId(Long.parseLong(attr(id, "id")));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                Element header = document.select(".post__header").first();
                Element title = header.select("h1 a").first();
                post.setTitle(text(title));
                String url = attr(title, "href");
                if (url != null) {
                    if (url.endsWith("/redirect")) {
                        post.setUrl(url.substring(0, url.lastIndexOf("/redirect")));
                    } else {
                        post.setUrl(url);
                    }
                }
            }
        }
        final Element element = document.select(".post__description .post__description-content").first();

        if (post.redirect == null) {
            Element re = document.select(".post__action-source a").first();
            post.setRedirect(attr(re, "href"));
            if (re != null) {
                String text = re.text();
                if (text != null) {
                    post.setSource(text.toLowerCase());
                }
            }
        }

        if (post.avatarUrl != null && !TextUtils.isEmpty(post.userName)) {
            setPostTime(post, document);
        } else {
            final Element postVia = document.select(".post-via").first();
            if (postVia != null) {
                final Element avatar = postVia.select("img.avatar").first();
                if (avatar != null) {
//                    Log.e("TAG", "avatar: " + avatar.toString());
                    post.setAvatarUrl(avatar.attr("src"));
                }
                setPostTime(post, postVia);
                setUserNameAndUrl(post, postVia);

            }
        }
        if (element != null) {
            post.setDescription(element.html());
        }

        Element a = document.select(".upvote-link--material").first();
        if (a != null) {
            String clazz = attr(a, "class");
            boolean upvoted = clazz != null && clazz.contains("upvote-link--upvoted");
            post.setVoted(upvoted);
            Element count = a.select(upvoted ? ".count-up" : ".count").first();
            String c = text(count);
            try {
                post.setVotes(Integer.parseInt(c));
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
            }
        }
        Elements comments = document.select(".post__comments");
        post.setHasComments(!comments.isEmpty());

        final Elements upvoters = document.select(".post__upvoters .post__upvoter");
        if (upvoters != null && !upvoters.isEmpty()) {
            int size = upvoters.size();
            for (int i = size - post.getVotes(); i < size; i++) {
                Element e = upvoters.get(i);
                Element link = e.select("a").first();
                String path = attr(link, "href");
                if (TextUtils.isEmpty(path)) {
                    continue;
                }
                Element img = e.select("img").first();
                String avatar = attr(img, "src");
                String alt = attr(img, "alt");
                User user = new User();
                user.setAvatarUrl(avatar);
                user.setPath(path);
                post.addUpvoter(user);
            }
        }

        Element addComment = document.select(".comment__form .comment__avatar-wrapper").first();
        if (addComment != null) {
            Element img = addComment.select("img.avatar").first();
            if (img != null) {
                Pref.get().setUserAvatar(attr(img, "src"));
            }
        }
        return post;
    }

    public static Post getPostContent1(Post post, MuResponse mu) {
        boolean full = post == null;
        if (full) {
            post = new Post();
        }
        if (!OK.equalsIgnoreCase(mu.status)) {
            return post;
        }

        final Element document = Jsoup.parse(mu.content);
        if (full) {
            Element id = document.select("div").first();
            String idtext = attr(id, "data-ng-init");
            if (idtext != null) {
                int index = idtext.indexOf(",");
                if(index > "init(".length()){
                    try {
                        post.setId(Long.parseLong(idtext.substring("init(".length(), index)));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        if (post.getTitle() == null) {
            Element header = document.select("div.post__sidebar-header").first();
            if (header != null) {
                Element heading = header.select("div.post__sidebar-header-heading").first();
                Element title = heading.select("h1").first();
                post.setTitle(text(title));
            }
        }

        if (post.getUrl() == null) {
            Element link = document.select("a.link-source").first();
            String url = attr(link, "href");
            if (url != null) {
                if (url.endsWith("/redirect")) {
                    post.setRedirect(url);
                        String text = link.text();
                        if (text != null) {
                            post.setSource(text.toLowerCase());
                        }

                    post.setUrl(url.substring(0, url.lastIndexOf("/redirect")));
                } else {
                    post.setUrl(url);
                }
            }
        }
//        final Element element = document.select(".post__description .post__description-content").first();

        if (post.redirect == null) {
            Element link = document.select("a.link-source").first();
            String url = attr(link, "href");
            if (url != null) {
                if (url.endsWith("/redirect")) {
                    post.setRedirect(url);
                    String text = link.text();
                    if (text != null) {
                        post.setSource(text.toLowerCase());
                    }
                }
            }
        }



        if (post.avatarUrl != null && !TextUtils.isEmpty(post.userName)) {
            setPostTime(post, document);
        } else {
            final Element postVia = document.select(".post-via").first();
            if (postVia != null) {
                final Element avatar = postVia.select("img.avatar").first();
                if (avatar != null) {
//                    Log.e("TAG", "avatar: " + avatar.toString());
                    post.setAvatarUrl(avatar.attr("src"));
                }
                setPostTime(post, postVia);
                setUserNameAndUrl(post, postVia);

            }
        }
        if (post.getDescription() == null) {
            Element des = document.select("div.post__description").first();
            if (des != null) {
                Element more = des.select("div").last();
                if (more != null) {
                    post.setDescription(more.html());
                } else {
                    post.setDescription(des.html());
                }
            }
        }

        Element a = document.select(".upvote-link--material").first();
        if (a != null) {
            String clazz = attr(a, "class");
            boolean upvoted = clazz != null && clazz.contains("upvote-link--upvoted");
            post.setVoted(upvoted);
            Element count = a.select(upvoted ? ".count-up" : ".count").first();
            String c = text(count);
            try {
                post.setVotes(Integer.parseInt(c));
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
            }
        }
//        Elements comments = document.select(".post__comments");
//        post.setHasComments(!comments.isEmpty());
        post.setHasComments(true);

//        final Elements upvoters = document.select(".post__upvoters .post__upvoter");
//        if (upvoters != null && !upvoters.isEmpty()) {
//            int size = upvoters.size();
//            for (int i = size - post.getVotes(); i < size; i++) {
//                Element e = upvoters.get(i);
//                Element link = e.select("a").first();
//                String path = attr(link, "href");
//                if (TextUtils.isEmpty(path)) {
//                    continue;
//                }
//                Element img = e.select("img").first();
//                String avatar = attr(img, "src");
//                String alt = attr(img, "alt");
//                User user = new User();
//                user.setAvatarUrl(avatar);
//                user.setPath(path);
//                post.addUpvoter(user);
//            }
//        }

//        Element addComment = document.select(".comment__form .comment__avatar-wrapper").first();
//        if (addComment != null) {
//            Element img = addComment.select("img.avatar").first();
//            if (img != null) {
//                Pref.get().setUserAvatar(attr(img, "src"));
//            }
//        }
        return post;
    }

    private static void setPostTime(Post post, Element document) {
        final Element des = document.select(".post-via__description").first();
//        Log.e("TAG", "des: " + des.toString());
        if (des != null) {
            final Element time = des.select("small").first();
            if (time != null) {
                post.setCreateTime(time.text());
            }
        }
    }

    private static void setUserNameAndUrl(Post post, Element document) {
        final Element a = document.select(".post-via__description a").first();
        if (a != null) {
//            Log.e("TAG", "a: " + a.toString());
            post.setUserName(a.text());
            post.setUserUrl(a.attr("href"));
        }
    }

    public static int parseColor(String style) {
        if (TextUtils.isEmpty(style)) {
            return 0;
        }

        try {
            if (style.length() == 4) {
                //#RGB
                int oldRgb = Integer.parseInt(style.substring(1), 16);
                int newRgb = 17 * (((oldRgb & 0xF00) << 8) | ((oldRgb & 0xF0) << 4) | (oldRgb & 0xF));
                return newRgb;
            } else {
                return Color.parseColor(style.trim());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getColor(String style) {
        if (TextUtils.isEmpty(style)) {
            return 0;
        }
        style = style.toLowerCase(Locale.ENGLISH);
        int start = style.indexOf(BG_COLOR);
        if (start >= 0) {
            try {
                int end = style.indexOf(";", start);
                if (end > start) {
                    style = style.substring(start + START, end).trim();
//                                L.d("background !%s!", style);
                    if (style.length() == 4) {
                        //#RGB
                        int oldRgb = Integer.parseInt(style.substring(1), 16);
                        int newRgb = 17 * (((oldRgb & 0xF00) << 8) | ((oldRgb & 0xF0) << 4) | (oldRgb & 0xF));
                        return newRgb;
                    } else {
                        return Color.parseColor(style.trim());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static List<Post> streams(MuResponse mu) {
        ArrayList<Post> posts = new ArrayList<>();
//        L.d("thread main " + (Looper.myLooper() == Looper.getMainLooper()));
//        L.d("response %s, %s", mu.status, mu.content);
        if (OK.equalsIgnoreCase(mu.status)) {
            final Element document = Jsoup.parse(mu.content);
            final Elements elements = document.select(".post-list-items .post-list-item");
            for (Element element : elements) {
                Post post = new Post();
                post.setId(Integer.parseInt(element.attr("id")));
                post.setTitle(element.select("h2").first().text());
                Element imageLink = element.select("a").first();
                String style = imageLink.attr("style");
                if (!TextUtils.isEmpty(style)) {
                    //background-color: #196D94;
                    style = style.toLowerCase(Locale.ENGLISH);
                    int start = style.indexOf(BG_COLOR);
                    if (start >= 0) {
                        try {
                            int end = style.indexOf(";", start);
                            if (end > start) {
                                style = style.substring(start + START, end).trim();
//                                L.d("background !%s!", style);
                                if (style.length() == 4) {
                                    //#RGB
                                    int oldRgb = Integer.parseInt(style.substring(1), 16);
                                    int newRgb = 17 * (((oldRgb & 0xF00) << 8) | ((oldRgb & 0xF0) << 4) | (oldRgb & 0xF));
                                    post.setBackground(newRgb);
                                } else {
                                    post.setBackground(Color.parseColor(style.trim()));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                post.setUrl(imageLink.attr("href"));
                try {
                    Element pre = element.select("img.post-preview__img--preview").first();
                    post.setPreviewUrl(pre.attr("src"));
                    String ngsrc = pre.attr("data-ng-src");
                    if (TextUtils.isEmpty(ngsrc)) {
                        post.setImageUrl(pre.attr("src"));
                    } else {
                        String[] srcs = ngsrc.split("'");
                        for (int i = 0; i < srcs.length; i++) {
                            if (srcs[i] != null && srcs[i].startsWith("http")) {
                                post.setImageUrl(srcs[i]);
                                if (srcs[i].endsWith(".gif")) {
                                    post.setGif(true);
                                    break;
                                }
                            }
                        }
                        if (post.getImageUrl() == null) {
                            post.setImageUrl(pre.attr("src"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                post.setTeaserUrl(element.select("img.post-preview__img--teaser").attr("src"));
                Element upvote = element.select("div.upvote").first();
                Element price = element.select("div.post-price").first();
                if (upvote != null) {
                    Element a = upvote.select("a").first();
                    boolean voted = false;
                    if (a != null) {
                        String upstyle = a.attr("class");
                        if (upstyle != null) {
                            voted = upstyle.contains("upvote-link--upvoted");
                            post.setVoted(voted);

                        }
                    }
                    try {
                        int votes = Integer.parseInt(element.select(voted ? ".count-up" : ".count").first().text());
                        post.setVotes(votes);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (price != null) {
                    post.setPrice(price.text());
                }
                posts.add(post);

                Element redirect = element.select("a.post_extras-source").first();
                if (redirect != null) {
                    post.setRedirect(redirect.attr("href"));
                    String source = redirect.select("span").first().text();
                    if (!TextUtils.isEmpty(source)) {
                        source = source.toLowerCase();
                    }
                    post.setSource(source);
                }

                Element userUrl = element.select("a.avatar__wrapper").first();
                // 可能没有头像
//                Log.e(TAG, "streams: " + userUrl);
                if (userUrl != null) {
                    Element avatar = userUrl.select("img.avatar").first();
//                    Log.e(TAG, "streams: " + avatar);
                    if (avatar != null) {
                        post.setAvatarUrl(avatar.attr("src"));
                    }
                }

                Element truncate = element.select("p.truncate").first();
//                Log.e(TAG, "truncate: " + truncate);
                if (truncate != null) {
                    Element name = truncate.select("a").first();
//                    Log.e(TAG, "name: " + name);
                    if (name != null) {
                        String url = name.attr("href");
                        if (!TextUtils.isEmpty(url) && !url.startsWith("/posts/")) {
                            post.setUserName(name.text());
                            post.setUserUrl(url);
                        }
                    }
                }

//                Element postStats = element.select("div.post__stats").first();
//                if (postStats != null) {
//                    Element link = postStats.select("a").first();
//                    post.setStatusLabel(link.attr("title"));
//                    post.setStatusCount(link.ownText());
//                }


//                L.d("post %s", post.toString());
            }
        }
        return posts;
    }

    public static User convertToUser(User user, ResponseBody body) {
        String text = null;
        try {
            text = body.string();
        } catch (IOException e) {
            e.printStackTrace();
            return user;
        }
        final Element document = Jsoup.parse(text);
        final Element userCard = document.select(".user__card").first();

        if (user == null) {
            user = new User();
        }

        if (userCard != null) {
            Element element = userCard.select(".user__card-avatar a").first();
            if (element != null) {
                user.setPath(element.attr("href"));
                user.setAvatarUrl(element.select("img").first().attr("src"));
            }

            Elements elements = userCard.select(".user__card-social-items .user__card-social-item a");
            for (Element e : elements) {
                String title = e.attr("title").toLowerCase();
                String url = e.attr("href");
                if (title.contains("website")) {
                    user.setWebsite(url);
                } else if (title.contains("twitter")) {
                    user.setTwitter(url);
                } else if (title.contains("dribbble")) {
                    user.setDribbble(url);
                } else if (title.contains("behance")) {
                    user.setBehance(url);
                } else if (title.contains("github")) {
                    user.setGithub(url);
                } else if (title.contains("google")) {
                    user.setGoogle(url);
                } else if (title.contains("codepen")) {
                    user.setCodepen(url);
                } else if (title.contains("slack")) {
                    user.setSlack(url);
                }

            }
            elements = userCard.select(".col-sm-2 a");
            for (Element e : elements) {
                String title = e.text().toLowerCase();
                String url = e.select("strong").first().text();
                int count = Integer.valueOf(url);
                if (title.contains("upvoted")) {
                    user.setUpvoted(count);
                } else if (title.contains("created")) {
                    user.setCreated(count);
                } else if (title.contains("created")) {
                    user.setCreated(count);
                } else if (title.contains("showcased")) {
                    user.setShowcased(count);
                } else if (title.contains("collections")) {
                    user.setCollections(count);
                } else if (title.contains("followers")) {
                    user.setFollowers(count);
                } else if (title.contains("following")) {
                    user.setFollowing(count);
                }

            }

            element = userCard.select(".user__card-name h1").first();
            if (element != null) {
                user.setFullName(element.ownText());
                user.setNameWithId(element.text());
            }

            element = userCard.select(".user__card-headline").first();
            if (element != null) {
                user.setHeadline(element.text());
            }
            element = userCard.select(".user__card-location").first();
            if (element != null) {
                user.setLocation(element.ownText());
            }
            element = userCard.select("a.btn-follow").first();
            if (element != null) {
                user.setFollowed(element.attr("class").contains("btn-follow--following"));
            }
        }
        return user;

    }


    public static List<Notification> notifications(MuResponse mu) {
        ArrayList<Notification> data = new ArrayList<>();
//        L.d("response %s, %s", mu.status, mu.content);
        if (OK.equalsIgnoreCase(mu.status)) {
            final Element document = Jsoup.parse(mu.content);
            final Elements elements = document.select(".notification__list-items .notification__list-item");
            if (elements != null) {
                for (int i = 0; i < elements.size(); i++) {
                    Element e = elements.get(i);
                    Notification noti = new Notification();
                    noti.postPath = attr(e, "href");
                    noti.viewed = isNotificationViewed(e);
                    noti.userAvatar = attr(e.select(".notification__avatar").first(), "src");
                    Element msg = e.select(".notification__message-wrapper").first();
                    noti.username = text(msg.select(".notification__name").first());
                    noti.message = text(msg.select(".notification__message").first());
                    noti.timestamp = text(msg.select(".notification__timestamp").first());
                    data.add(noti);
                }
            }
        }

        return data;
    }

    private static boolean isNotificationViewed(Element e) {
        try {
            return attr(e, "class").contains("notification__list-item--checked");
        } catch (Exception e1) {
            e1.printStackTrace();
            return true;
        }
    }


}
