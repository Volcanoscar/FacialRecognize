package com.prize.facialrecognize.common;

import com.baidu.aip.client.BaseClient;
import com.baidu.aip.error.AipError;
import com.baidu.aip.face.FaceConsts;
import com.baidu.aip.http.AipRequest;
import com.baidu.aip.util.Base64Util;
import com.baidu.aip.util.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/3/14.
 */

public class PrizeAipFace extends BaseClient {
    public static final String FACE_DETECT_URL = "https://aip.baidubce.com/rest/2.0/face/v1/detect";
    public static final String FACE_MATCH_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/match";
    public static final String FACE_SEARCH_FACESET_USER_ADD_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/faceset/user/add";
    public static final String FACE_SEARCH_FACESET_USER_UPDATE_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/faceset/user/update";
    public static final String FACE_SEARCH_FACESET_USER_DELETE_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/faceset/user/delete";
    public static final String FACE_SEARCH_VERIFY_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/verify";
    public static final String FACE_SEARCH_IDENTIFY_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/identify";
    public static final String FACE_SEARCH_FACESET_USER_GET_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/faceset/user/get";
    public static final String FACE_SEARCH_FACESET_GROUP_GET_LIST_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/faceset/group/getlist";
    public static final String FACE_SEARCH_FACESET_GROUP_GET_USERS_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/faceset/group/getusers";
    public static final String FACE_SEARCH_FACESET_GROUP_ADD_USER_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/faceset/group/adduser";
    public static final String FACE_SEARCH_FACESET_GROUP_DELETE_USER_URL = "https://aip.baidubce.com/rest/2.0/faceverify/v1/faceset/group/deleteuser";

    public PrizeAipFace(String appId, String aipKey, String aipToken) {
        super(appId, aipKey, aipToken);
    }

    public JSONObject detect(String imgPath, HashMap<String, String> options) {
        try {
            byte[] e = Util.readFileByBytes(imgPath);
            return this.detect(e, options);
        } catch (IOException var4) {
            var4.printStackTrace();
            return AipError.IMAGE_READ_ERROR.toJsonResult();
        }
    }

    public JSONObject detect(byte[] imgData, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        this.preOperation(request);
        String base64Content = Base64Util.encode(imgData);
        if((long)base64Content.length() > FaceConsts.FACE_DETECT_MAX_IMAGE_SIZE.longValue()) {
            return AipError.IMAGE_SIZE_ERROR.toJsonResult();
        } else {
            request.addBody("image", base64Content);
            request.addBody(options);
            request.setUri("https://aip.baidubce.com/rest/2.0/face/v1/detect");
            this.postOperation(request);
            return this.requestServer(request);
        }
    }

    public JSONObject match(String orig, String toCompare) {
        AipRequest request = new AipRequest();
        ArrayList e = new ArrayList();
        e.add(orig);
        e.add(toCompare);

        String imgDataAll1 = Util.mkString(e.iterator(), ',');
        if((long)imgDataAll1.length() > FaceConsts.FACE_MATCH_MAX_IMAGE_SIZE.longValue()) {
            return AipError.IMAGE_SIZE_ERROR.toJsonResult();
        } else {
            this.preOperation(request);
            request.addBody("images", imgDataAll1);
            request.setUri("https://aip.baidubce.com/rest/2.0/faceverify/v1/match");
            this.postOperation(request);
            return this.requestServer(request);
        }
    }
}
