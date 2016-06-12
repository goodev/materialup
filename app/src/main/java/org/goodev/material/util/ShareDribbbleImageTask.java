/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.goodev.material.util;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;

import org.goodev.material.R;
import org.goodev.material.model.Post;

import java.io.File;


/**
 * An AsyncTask which retrieves a File from the Glide cache then shares it.
 */
public class ShareDribbbleImageTask extends AsyncTask<Void, Void, File> {

    private final Activity activity;
    private final Post shot;

    public ShareDribbbleImageTask(Activity activity, Post shot) {
        this.activity = activity;
        this.shot = shot;
    }

    @Override
    protected File doInBackground(Void... params) {
        final String url = shot.getTeaserUrl();
        try {
            ImageRequest imageRequest = ImageRequest.fromUri(url);
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest);
//            ImagePipeline imagePipeline = Fresco.getImagePipeline();
//            imagePipeline.prefetchToDiskCache(imageRequest,activity);
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
            File file = ((FileBinaryResource) resource).getFile();

            String fileName = url;
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
            File renamed = new File(file.getParent(), fileName);
            if (!renamed.exists()) {
                FileUtil.copy(file, renamed);
            }
            return renamed;
        } catch (Exception ex) {
            Log.w("SHARE", "Sharing " + url + " failed", ex);
            return null;
        }
    }

    @Override
    protected void onPostExecute(File result) {
        if (result == null) {
            return;
        }
        // glide cache uses an unfriendly & extension-less name,
        // massage it based on the original
//        result.renameTo(renamed);
        Uri uri = FileProvider.getUriForFile(activity,
                activity.getString(R.string.share_authority), result);
        ShareCompat.IntentBuilder.from(activity)
                .setText(getShareText())
                .setType(getImageMimeType(result.getName()))
                .setSubject(shot.title)
                .setStream(uri)
                .startChooser();
    }


    private String getShareText() {
        return new StringBuilder()
                .append("“")
                .append(shot.title)
                .append("” by ")
                .append(shot.userName)
                .append("\n")
                .append(shot.getFullUrl())
                .toString();
    }

    private String getImageMimeType(@NonNull String fileName) {
        if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        return "image/jpeg";
    }
}
