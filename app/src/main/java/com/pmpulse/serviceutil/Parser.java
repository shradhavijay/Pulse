package com.pmpulse.serviceutil;

import com.pmpulse.data.ChapterAudio;
import com.pmpulse.data.FreeAudios;
import com.pmpulse.data.KeyValues;
import com.pmpulse.data.Package;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static int userNumber = 0;
    public static String statusLogin = "";
    public static String percentageCompleted = "";
    public static String subscriptionDaysRemain = "";
    public static String moduleName = "";
    public static int moduleId = 0;
    public static String success = "success";
    public static String Success = "Success";
    //public static String failure = "Failure";
    public static String userName = "";
    public static List<String> topic = new ArrayList<>();
    public static List<String> topicId = new ArrayList<>();
    public static List<Package> packageList = new ArrayList<>();
    public static List<ChapterAudio> chapterAudios = new ArrayList<>();
    public static String statusLogout = "";
    public static List<FreeAudios> freeAudio = new ArrayList<>();
    public static String notGiven = "Not Given";
    public static int selectedModuleId = 0;

    String loginDetails = "LoginDetails";
    String userId = "userid";
    String statusMessage = "statusMessage";
    String trainingCategory = "TrainingCategory";
    String mainCategoryName = "MainCategoryName";
    String trainingName = "TrainingName";
    String completed = "PercComplete";
    String daysRemain = "DaysLeft";
    String firstName = "firstName";
    String lastName = "lastName";
    String otherDetails = "OtherDetails";
    String mainCategoryID = "MainCategoryID";
    String audioPath = "AudioPath";
    String isPlayed = "IsPlayed";
    String userRating = "UserRating";
    String category = "Category";
    String audioName = "AudioName";
    String fileURL = "FileURL";
    String domainName = "DomainName";
    String audioId = "AudioID";
    String isLoggedIn = "IsLoggedIn";
    String trainingID = "TrainingID";
    //parameter
    String categoryID = "CategoryID";

    String PackageID = "PackageID";
    String TrainingID = "TrainingID";
    String PackageName = "PackageName";
    String TotalDays = "TotalDays";
    String Price = "Price";
    String ImagePath = "ImagePath";

    //parser for login and topics
    public String loginParse(String response) {
        try {
            response = toJSON(response);
            if (KeyValues.isDebug)
                System.out.println("parser " + response);
            JSONObject parse = new JSONObject(response);

            if (parse.has(loginDetails)) {
                JSONArray loginDetail = (JSONArray) parse.get(loginDetails);
                userNumber = ((JSONObject) loginDetail.get(0)).getInt(userId);
                statusLogin = ((JSONObject) loginDetail.get(0)).getString(statusMessage);
                if (statusLogin.equalsIgnoreCase(Parser.success)) {
                    //valid session and/or user
                    userName = ((JSONObject) loginDetail.get(0)).getString(firstName) + " " + ((JSONObject) loginDetail.get(0)).getString(lastName);

                    //for fetching details on days remaining and completion percentage
                   /* JSONArray trainingDetail = (JSONArray) parse.get(other);
                    JSONObject trainingDetailObj = (JSONObject) trainingDetail.get(0);
                    percentageCompleted = trainingDetailObj.getString(completed);
                    subscriptionDaysRemain = trainingDetailObj.getString(daysRemain);*/

                    //parse topics
                    /*JSONArray training = (JSONArray) parse.get(trainingCategory);
                    topic.clear();
                    topicId.clear();
                    for (int topicCount = 0; topicCount < training.length(); topicCount++) {
                        topic.add(((JSONObject) training.get(topicCount)).getString(mainCategoryName));
                        moduleName = ((JSONObject) training.get(topicCount)).getString(trainingName);
                        moduleId = ((JSONObject) training.get(topicCount)).getInt(trainingID);
                        topicId.add(((JSONObject) training.get(topicCount)).getString(mainCategoryID));
                    }*/

                }
            }
        } catch (JSONException e) {
            if (KeyValues.isDebug)
                System.out.println("in parser catch :" + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return statusLogin;
    }

    public String packageParser(String response) {
        try {
            response = toJSON(response);
            if (KeyValues.isDebug)
                System.out.println("parser " + response);
            JSONArray parse = new JSONArray(response);
            packageList.clear();
            for (int countPackage = 0; countPackage < parse.length(); countPackage++) {
                Package userPackage = new Package();
                userPackage.setPackageId(((JSONObject) parse.get(countPackage)).getString(PackageID));
                userPackage.setTrainingId(((JSONObject) parse.get(countPackage)).getString(TrainingID));
                userPackage.setPackageName(((JSONObject) parse.get(countPackage)).getString(PackageName));
                userPackage.setTotalDays(((JSONObject) parse.get(countPackage)).getString(TotalDays));
                userPackage.setPrice(((JSONObject) parse.get(countPackage)).getString(Price));
                userPackage.setImagePath(((JSONObject) parse.get(countPackage)).getString(ImagePath));
                packageList.add(userPackage);
            }
        } catch (JSONException e) {
            if (KeyValues.isDebug)
                System.out.println("in packageParser parser catch :" + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void topicParser(String response) {
        try {
            response = toJSON(response);
            if (KeyValues.isDebug)
                System.out.println("topicParser " + response);
            JSONObject parse = new JSONObject(response);

            //for fetching details on days remaining and completion percentage
            JSONArray trainingDetail = (JSONArray) parse.get(otherDetails);
            JSONObject trainingDetailObj = (JSONObject) trainingDetail.get(0);
            percentageCompleted = trainingDetailObj.getString(completed);
            subscriptionDaysRemain = trainingDetailObj.getString(daysRemain);

            //parse topics
            JSONArray training = (JSONArray) parse.get(trainingCategory);
            topic.clear();
            topicId.clear();
            for (int topicCount = 0; topicCount < training.length(); topicCount++) {
                topic.add(((JSONObject) training.get(topicCount)).getString(mainCategoryName));
                moduleName = ((JSONObject) training.get(topicCount)).getString(trainingName);
                moduleId = ((JSONObject) training.get(topicCount)).getInt(trainingID);
                topicId.add(((JSONObject) training.get(topicCount)).getString(mainCategoryID));
            }
        } catch (JSONException e) {
            if (KeyValues.isDebug)
                System.out.println("in topicParser catch :" + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getAudioParser(String response) {
        try {
            response = toJSON(response);
            if (KeyValues.isDebug)
                System.out.println("parser " + response);
            JSONArray parse = new JSONArray(response);
            chapterAudios.clear();
            for (int countAudio = 0; countAudio < parse.length(); countAudio++) {
                ChapterAudio chapterAudio = new ChapterAudio();
                chapterAudio.setMainCategoryName(((JSONObject) parse.get(countAudio)).getString(mainCategoryName));

                String fileUrl = "";
                String domain = ((JSONObject) parse.get(countAudio)).getString(domainName);
                String name = ((JSONObject) parse.get(countAudio)).getString(audioPath);
                name = name.substring(0, name.length() - 19);
                fileUrl = domain + "Audios/2c334da84b9f/" + name + ".mp3";
                chapterAudio.setAudioPath(fileUrl);

                chapterAudio.setIsPlayed(((JSONObject) parse.get(countAudio)).getBoolean(isPlayed));
                chapterAudio.setUserRating(((JSONObject) parse.get(countAudio)).getString(userRating));
                chapterAudio.setAudioId(((JSONObject) parse.get(countAudio)).getString(audioId));
                chapterAudio.setCategoryId(((JSONObject) parse.get(countAudio)).getString(categoryID));
                chapterAudios.add(chapterAudio);
            }

        } catch (JSONException e) {
            if (KeyValues.isDebug)
                System.out.println("in getAudioParser parser catch :" + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return "" + chapterAudios.size();
    }

    public String logoutParser(String response) {
        try {
            response = toJSON(response);
            if (KeyValues.isDebug)
                System.out.println("parser logoutParser" + response);
            JSONArray parse = new JSONArray(response);
            statusLogout = ((JSONObject) parse.get(0)).getString(statusMessage);

        } catch (JSONException e) {
            if (KeyValues.isDebug)
                System.out.println("logoutParser catch :" + e.getMessage());
            e.printStackTrace();
        }
        return statusLogout;
    }

    public boolean isLoggedInParser(String response) {
        boolean isLogged = false;
        try {
            response = toJSON(response);
            if (KeyValues.isDebug)
                System.out.println("parser logoutParser" + response);
            JSONArray parse = new JSONArray(response);
            isLogged = ((JSONObject) parse.get(0)).getBoolean(isLoggedIn);

        } catch (JSONException e) {
            if (KeyValues.isDebug)
                System.out.println("logoutParser catch :" + e.getMessage());
            e.printStackTrace();
        }
        return isLogged;
    }

    public String freeAudioParser(String response) {
        try {
            response = toJSON(response);
            if (KeyValues.isDebug)
                System.out.println("parser freeAudioParser" + response);
            JSONArray parse = new JSONArray(response);
            freeAudio.clear();
            for (int countFreeAudio = 0; countFreeAudio < parse.length(); countFreeAudio++) {
                FreeAudios freeAudios = new FreeAudios();
                freeAudios.setAudioName(((JSONObject) parse.get(countFreeAudio)).getString(audioName));
                freeAudios.setCategory(((JSONObject) parse.get(countFreeAudio)).getString(category));

                String fileUrl = "";
                String domain = ((JSONObject) parse.get(countFreeAudio)).getString(domainName);
                String name = ((JSONObject) parse.get(countFreeAudio)).getString(fileURL);
                name = name.substring(0, name.length() - 19);
                fileUrl = domain + "FreeAudio/" + name + ".mp3";
                freeAudios.setFileURL(fileUrl);
                freeAudio.add(freeAudios);
            }
        } catch (JSONException e) {
            if (KeyValues.isDebug)
                System.out.println("freeAudioParser catch :" + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return "" + freeAudio.size();
    }

    //parser for urlGetAudioUrl
    public String getAudioUrlParser(String response) {
        String fileUrl = "";
        try {
            response = toJSON(response);
            if (KeyValues.isDebug)
                System.out.println("parser getAudioUrlParser" + response);
            JSONArray parse = new JSONArray(response);
            String domain = ((JSONObject) parse.get(0)).getString(domainName);
            String name = ((JSONObject) parse.get(0)).getString(audioPath);
            name = name.substring(0, name.length() - 19);
            fileUrl = domain + "Audios/2c334da84b9f/" + name + ".mp3";

        } catch (JSONException e) {
            if (KeyValues.isDebug)
                System.out.println("getAudioUrlParser catch :" + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return fileUrl;
    }

    public String toJSON(String response) {
        response = response.replace("\\", "").replace("rn", "");
        response = response.substring(1, response.length() - 1);
        return response;
    }
}
