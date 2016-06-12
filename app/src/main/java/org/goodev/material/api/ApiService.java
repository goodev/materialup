package org.goodev.material.api;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.goodev.material.model.Collect;
import org.goodev.material.model.Collection;
import org.goodev.material.model.Comment;
import org.goodev.material.model.Follow;
import org.goodev.material.model.MuResponse;
import org.goodev.material.model.SearchRes;
import org.goodev.material.model.Upvote;

import java.util.List;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by yfcheng on 2015/11/26.
 */
public interface ApiService {

    //    http://www.materialup.com/?page=2&sort=
    @Headers("Accept: application/json")
    @GET("/")
    Observable<MuResponse> getHomeStream(@Query("page") int page, @Query("sort") String sort);

    //   http://www.materialup.com/posts/c/inspiration/website?sort=popular
    @Headers("Accept: application/json")
    @GET("/posts/c/{cat}/{sub}")
    Observable<MuResponse> getCategoryStream(@Path("cat") String cat, @Path("sub") String sub, @Query("page") int page, @Query("sort") String sort);

    //    http://www.materialup.com/collections/web-inspiration-7b588f7d-ea10-4706-b9a8-4995de46be27?page=2
    @Headers("Accept: application/json")
    @GET("/collections/{id}")
    Observable<MuResponse> getCollectionPosts(@Path("id") String id, @Query("page") int page);

    @Headers("Accept: application/json")
    @GET("/collections")
    Observable<MuResponse> getFeaturedCollection(@Query("page") int page);


    @Headers("Accept: application/json")
    @GET("/collections/{sub}")
    Observable<MuResponse> getCollections(@Path("sub") String sub, @Query("page") int page);

    //www.materialup.com/users/MatKoziorowski/upvotes
    @Headers("Accept: application/json")
    @GET("/users/{id}/{type}")
    Observable<MuResponse> getUserPosts(@Path("id") String id, @Path("type") String type, @Query("page") int page);

    @Headers("Accept: application/json")
    @GET("/users/{id}/{type}")
    Observable<MuResponse> getUserFans(@Path("id") String id, @Path("type") String type, @Query("page") int page);

    @Headers("Accept: application/json")
    @GET("/users/{id}/collections")
    Observable<MuResponse> getUserCollections(@Path("id") String id, @Query("page") int page);

    @Headers("Accept: application/json")
    @GET("/users/{id}/upvotes")
    Observable<MuResponse> getUserUpvoted(@Path("id") String id, @Query("page") int page);

    @Headers("Accept: application/json")
    @GET("/users/{id}/posts")
    Observable<MuResponse> getUserCreated(@Path("id") String id, @Query("page") int page);

    @Headers("Accept: application/json")
    @GET("/users/{id}/showcases")
    Observable<MuResponse> getUserShowcased(@Path("id") String id, @Query("page") int page);

    @Headers("Accept: application/json")
    @GET("/posts/{id}")
    Observable<MuResponse> getPost(@Path("id") long id);

    @Headers("Accept: application/json")
    @GET("/posts/{id}/sidebar")
    Observable<MuResponse> getPostSidebar(@Path("id") long id);

    @Headers("Accept: application/json")
    @GET("/posts/{id}/sidebar")
    Observable<MuResponse> getPostSidebar(@Path("id") String id);

    @Headers("Accept: application/json")
    @GET("/posts/{id}")
    Observable<MuResponse> getPost(@Path("id") String id);

    @Headers("Accept: application/json")
    @GET("/posts/{id}/comments")
    Observable<List<Comment>> getComments(@Path("id") long id);

    @Headers("Accept: application/json")
    @GET("/users/my_collections")
    Observable<List<Collection>> getMyCollections(@Query("page") int page);

    @Headers("Accept: application/json")
    @PUT("/posts/{id}/upvote")
    Observable<Upvote> upvotes(@Path("id") long id, @Body String content);

    @Headers("Accept: application/json")
    @PUT("/posts/{id}/downvote")
    Observable<Upvote> downvote(@Path("id") long id, @Body String content);


    //http://www.materialup.com/collections/6483/collect?post_id=2778
    @Headers("Accept: application/json")
    @PUT("/collections/{id}/collect")
    Observable<Collect> addToCollection(@Path("id") long id, @Query("post_id") long postId);

    //http://www.materialup.com/collections/6483/uncollect?post_id=2778
    @Headers("Accept: application/json")
    @DELETE("/collections/{id}/uncollect")
    Observable<Collect> deleteFromCollection(@Path("id") long id, @Query("post_id") long postId);


    //http://www.materialup.com/users/noxiousone/
    @Headers("Accept: text/html,application/xhtml+xml,application/xml")
    @GET("/users/{id}")
    Observable<ResponseBody> getUserInfo(@Path("id") String id);

    @Headers("Accept: text/html,application/xhtml+xml,application/xml")
    @GET("/users/settings")
    Observable<Response<String>> getLoginUserInfo();

    //http://www.materialup.com/notifications
    //Location
    @Headers("Accept: application/json")
    @GET("/notifications/preview")
    Observable<MuResponse> checkLogin();
//    Observable<retrofit.Response<String>> checkLogin();

    //http://www.materialup.com/posts/2778/comments/1286/like
    @Headers("Accept: application/json")
    @PUT("/posts/{pid}/comments/{cid}/like")
    Observable<Comment> likedComment(@Path("pid") long pid, @Path("cid") long cid);

    @Headers("Accept: application/json")
    @PUT("/posts/{pid}/comments/{cid}/unlike")
    Observable<Comment> unlikedComment(@Path("pid") long pid, @Path("cid") long cid);

    //http://www.materialup.com/users/zeeya1989/unfollow
    @Headers("Accept: application/json")
    @PUT("/users/{uid}/unfollow")
    Observable<Follow> unfollow(@Path("uid") String uid);

    @Headers("Accept: application/json")
    @PUT("/users/{uid}/follow")
    Observable<Follow> follow(@Path("uid") String uid);

    //http://www.materialup.com/posts/2778/comments/5575
    @Headers("Accept: application/json")
    @DELETE("/posts/{pid}/comments/{cid}")
    Observable<Comment> deleteComment(@Path("pid") long pid, @Path("cid") long cid);

    //http://www.materialup.com/posts/2778/comments
    @Headers("Accept: application/json")
    @POST("/posts/{pid}/comments")
    Observable<Comment> postComment(@Path("pid") long pid, @Body RequestBody body);

    //   http://2mxde58ue7-dsn.algolia.net/1/indexes/*/queries?x-algolia-api-key=81527b28652b683ef1e6d2836be2663e&x-algolia-application-id=2MXDE58UE7&x-algolia-agent=Algolia%20for%20AngularJS%203.10.2
    //{"requests":[{"indexName":"materialup_production","params":"query=d&hitsPerPage=9&page=0&facets=%5B%22label
    //%22%2C%22subcategory_friendly_name%22%2C%22platform_friendly_name%22%5D&facetFilters=%5B%5D"}]}
    //{"requests":[{"indexName":"materialup_production","params":""}]}
    @Headers("Accept: application/json")
    @POST("http://2mxde58ue7-dsn.algolia.net/1/indexes/*/queries?x-algolia-api-key=81527b28652b683ef1e6d2836be2663e&x-algolia-application-id=2MXDE58UE7&x-algolia-agent=Algolia%20for%20AngularJS%203.10.2")
    Observable<SearchRes> search(@Body RequestBody body);

    @Headers("Accept: application/json")
    @GET("/notifications/preview")
    Observable<MuResponse> getNotifications();

    @Headers("Accept: application/json")
    @PATCH("/notifications/viewed")
    Observable<ResponseBody> patchNotifications();

    /**
     * @param extractor website or url
     * @param format    standard  mobile_app  mobile_app_icon
     * @param url
     * @return
     */
    @Headers("Accept: application/json")
    @GET("/urls/extract")
//default_extractor=website&source_format={format}&url=
    Observable<ResponseBody> extractUrl(@Query("default_extractor") String extractor, @Query("source_format") String format, @Query("url") String url);
}
