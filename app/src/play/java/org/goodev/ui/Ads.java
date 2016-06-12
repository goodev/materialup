package org.goodev.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adxmi.customizedad.AdManager;
import com.adxmi.customizedad.ContentAdModel;
import com.adxmi.customizedad.ContentAdRequestListener;

import org.goodev.material.App;
import org.goodev.material.BuildConfig;
import org.goodev.material.R;
import org.goodev.material.model.Post;
import org.goodev.material.util.UI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ads {
    static List<Post> sAdShots = new ArrayList<>();

    public static Post toAdShot(ContentAdModel adModel, long id) {

        Post shot = new Post();
        shot.id = id;
        shot.createTime = adModel.getBtn(); //attachmentsUrl
        shot.userName = adModel.getCategory();//bucketsUrl
        shot.description = adModel.getDes();
        shot.teaserUrl = adModel.getIcon();//commentsUrl
        shot.title = adModel.getName();
        shot.price = adModel.getId(); //htmlUrl
        shot.imageUrl = adModel.getCreatives();//reboundsUrl
        shot.source = adModel.getPgn();//reboundSourceUrl
        shot.votes = (int) (adModel.getRating() * 100);//likesCount
        shot.avatarUrl = adModel.getSize();//projectsUrl

        return shot;

        //创建一个广告类型的 item， 如果判断是广告，单独显示，使用 Adapter 的type来判断， 添加一个ad类型
    }

    public static ContentAdModel toAd(Post data) {
        ContentAdModel ad = new ContentAdModel();
        ad.setBtn(data.createTime);
        ad.setCategory(data.userName);
        ad.setDes(data.description);
        ad.setIcon(data.teaserUrl);
        ad.setName(data.title);
        ad.setId(data.price);
        ad.setCreatives(data.imageUrl);
        ad.setPgn(data.source);
        ad.setRating(data.votes / 100f);
        ad.setSize(data.avatarUrl);
        return ad;
    }

    public static boolean isAd(long id) {
        return id <= sAdStartId;
    }

    public static void setSupportAds(Activity context, AdsAdapter adapter) {
        AdManager.getInstance(context).registerRequestAdListener(new ContentAdRequestListener() {
            @Override
            public void onRequestResult(List<ContentAdModel> list) {
                if (list == null || list.isEmpty()) {
                    Toast.makeText(context, R.string.empty_apps, Toast.LENGTH_SHORT).show();
                    return;
                }

                adapter.addData(list);
            }
        });
        AdManager.getInstance(context).requestAd(16);
    }

    public static long sAdStartId = -3111L;

    public static void checkUpdate(Activity context) {
        AdManager.getInstance(context).init(BuildConfig.AppID, BuildConfig.AppSecret , AdManager.TYPE_CONTENT);
        AdManager.getInstance(context).setEnableDebugLog(BuildConfig.DEBUG);
        AdManager.getInstance(context).registerRequestAdListener(new ContentAdRequestListener() {
            @Override
            public void onRequestResult(List<ContentAdModel> list) {
                if (list == null) {
                    return;
                }
                sAdShots.clear();
                for (int i = 0; i < list.size(); i++) {
                    sAdShots.add(toAdShot(list.get(i), sAdStartId - i));
                }
            }
        });
        AdManager.getInstance(context).requestAd(6);
    }

    static Random sRandom = new Random();

    public static void addAdToShot(List<Post> shots, List<Post> all) {
        if (sAdShots.isEmpty()) {
            return;
        }
        Post shot = sAdShots.get(sRandom.nextInt(sAdShots.size()));
        if (all == null) {
            shots.add(shot);
            return;
        }
        if (!isAppInstalled(App.getIns(), shot.source) && !all.contains(shot)) {
            shots.add(shot);
        }
    }

    public static void onClickAd(Activity context, String id) {
        AdManager.getInstance(context).onClickAd(id, context.getResources().getColor(
                R.color.colorPrimary));
    }

    public static void onBackKey(Activity context) {
        AdManager.getInstance(context).onKeyBack();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        return UI.isAppInstalled(context, packageName);
    }
}
