/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.bluetooth.avrcp;

import android.media.session.MediaSession;

import java.util.List;
import java.util.Arrays;

/*************************************************************************************************
 * Helper classes used for callback/response of browsing commands:-
 *     1) To bundle parameters for  native callbacks/response.
 *     2) Stores information of Addressed and Browsed Media Players.
 ************************************************************************************************/

class AvrcpCmd {

    public AvrcpCmd() {}

    /* Helper classes to pass parameters from callbacks to Avrcp handler */
    class FolderItemsCmd {
        byte mScope;
        int mStartItem;
        int mEndItem;
        byte mNumAttr;
        int[] mAttrIDs;
        public byte[] mAddress;

        public FolderItemsCmd(byte[] address,byte scope, int startItem, int endItem, byte numAttr,
                int[] attrIds) {
            mAddress = address;
            this.mScope = scope;
            this.mStartItem = startItem;
            this.mEndItem = endItem;
            this.mNumAttr = numAttr;
            this.mAttrIDs = attrIds;
        }
    }

    class ItemAttrCmd {
        byte mScope;
        byte[] mUid;
        int mUidCounter;
        byte mNumAttr;
        int[] mAttrIDs;
        public byte[] mAddress;

        public ItemAttrCmd(byte[] address, byte scope, byte[] uid, int uidCounter, byte numAttr,
                int[] attrIDs) {
            mAddress = address;
            mScope = scope;
            mUid = uid;
            mUidCounter = uidCounter;
            mNumAttr = numAttr;
            mAttrIDs = attrIDs;
        }
    }

    class ElementAttrCmd {
        byte mNumAttr;
        int[] mAttrIDs;
        public byte[] mAddress;

        public ElementAttrCmd(byte[] address, byte numAttr, int[] attrIDs) {
            mAddress = address;
            mNumAttr = numAttr;
            mAttrIDs = attrIDs;
        }
    }
}

/* Helper classes to pass parameters to native response */
class MediaPlayerListRsp {
    byte mStatus;
    short mUIDCounter;
    byte itemType;
    byte[] mPlayerTypes;
    int[] mPlayerSubTypes;
    byte[] mPlayStatusValues;
    short[] mFeatureBitMaskValues;
    String[] mPlayerNameList, mPackageNameList;
    List<MediaController> mControllersList;
    int mNumItems;

    public MediaPlayerListRsp(byte status, short UIDCounter, int numItems, byte itemType,
            byte[] playerTypes, int[] playerSubTypes, byte[] playStatusValues,
            short[] featureBitMaskValues, String[] playerNameList, String packageNameList[],
            List<MediaController> mediaControllerList) {
        this.mStatus = status;
        this.mUIDCounter = UIDCounter;
        this.mNumItems = numItems;
        this.itemType = itemType;
        this.mPlayerTypes = playerTypes;
        this.mPlayerSubTypes = new int[numItems];
        this.mPlayerSubTypes = playerSubTypes;
        this.mPlayStatusValues = new byte[numItems];
        this.mPlayStatusValues = playStatusValues;
        int bitMaskSize = AvrcpConstants.AVRC_FEATURE_MASK_SIZE;
        this.mFeatureBitMaskValues = new short[numItems * bitMaskSize];
        for (int bitMaskIndex = 0; bitMaskIndex < (numItems * bitMaskSize); bitMaskIndex++) {
            this.mFeatureBitMaskValues[bitMaskIndex] = featureBitMaskValues[bitMaskIndex];
        }
        this.mPlayerNameList = playerNameList;
        this.mPackageNameList = packageNameList;
        this.mControllersList = mediaControllerList;
    }
}

class FolderItemsRsp {
    byte mStatus;
    short mUIDCounter;
    byte mScope;
    int mNumItems;
    byte[] mFolderTypes;
    byte[] mPlayable;
    byte[] mItemTypes;
    byte[] mItemUid;
    String[] mDisplayNames; /* display name of the item. Eg: Folder name or song name */
    int[] mAttributesNum;
    int[] mAttrIds;
    String[] mAttrValues;

    public FolderItemsRsp(byte Status, short UIDCounter, byte scope, int numItems,
            byte[] folderTypes, byte[] playable, byte[] ItemTypes, byte[] ItemsUid,
            String[] displayNameArray, int[] AttributesNum, int[] AttrIds, String[] attrValues) {
        this.mStatus = Status;
        this.mUIDCounter = UIDCounter;
        this.mScope = scope;
        this.mNumItems = numItems;
        this.mFolderTypes = folderTypes;
        this.mPlayable = playable;
        this.mItemTypes = ItemTypes;
        this.mItemUid = ItemsUid;
        this.mDisplayNames = displayNameArray;
        this.mAttributesNum = AttributesNum;
        this.mAttrIds = AttrIds;
        this.mAttrValues = attrValues;
    }
}

class ItemAttrRsp {
    byte mStatus;
    byte mNumAttr;
    int[] mAttributesIds;
    String[] mAttributesArray;

    public ItemAttrRsp(byte status, byte numAttr, int[] attributesIds, String[] attributesArray) {
        this.mStatus = status;
        this.mNumAttr = numAttr;
        this.mAttributesIds = attributesIds;
        this.mAttributesArray = attributesArray;
    }
}

/* Helps managing the NowPlayingList */
class NowPlayingListManager {
    private List<MediaSession.QueueItem> mNowPlayingItems = null;

    synchronized void setNowPlayingList(List<MediaSession.QueueItem> queue) {
        mNowPlayingItems = queue;
    }

    synchronized List<MediaSession.QueueItem> getNowPlayingList() {
        return mNowPlayingItems;
    }
}

/* stores information of Media Players in the system */
class MediaPlayerInfo {

    private String packageName;
    private byte majorType;
    private int subType;
    private byte playStatus;
    private short[] featureBitMask;
    private String displayableName;
    private MediaController mediaController;

    MediaPlayerInfo(String packageName, byte majorType, int subType, byte playStatus,
            short[] featureBitMask, String displayableName, MediaController mediaController) {
        this.setPackageName(packageName);
        this.setMajorType(majorType);
        this.setSubType(subType);
        this.playStatus = playStatus;

        // copying the FeatureBitMask array
        this.setFeatureBitMask(new short[featureBitMask.length]);
        for (int count = 0; count < featureBitMask.length; count++) {
            this.getFeatureBitMask()[count] = featureBitMask[count];
        }

        this.setDisplayableName(displayableName);
        this.setMediaController(mediaController);
    }

    /* getters and setters */
    byte getPlayStatus() {
        return playStatus;
    }

    void setPlayStatus(byte playStatus) {
        this.playStatus = playStatus;
    }

    MediaController getMediaController() {
        return mediaController;
    }

    void setMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    String getPackageName() {
        return packageName;
    }

    void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    byte getMajorType() {
        return majorType;
    }

    void setMajorType(byte majorType) {
        this.majorType = majorType;
    }

    int getSubType() {
        return subType;
    }

    void setSubType(int subType) {
        this.subType = subType;
    }

    String getDisplayableName() {
        return displayableName;
    }

    void setDisplayableName(String displayableName) {
        this.displayableName = displayableName;
    }

    short[] getFeatureBitMask() {
        return featureBitMask;
    }

    void setFeatureBitMask(short[] featureBitMask) {
        this.featureBitMask = featureBitMask;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n+++ MediaPlayerInfo: +++");
        sb.append("\nPlayer Package Name = " + getPackageName());
        sb.append("\nMajor Player Type = " + getMajorType());
        sb.append("\nPlayer SubType = " + getSubType());
        sb.append("\nPlay Status = " + playStatus);
        sb.append("\nFeatureBitMask:\n ");
        for (int count = 0; count < getFeatureBitMask().length; count++) {
            sb.append("\nFeature BitMask[" + count + "] = " + getFeatureBitMask()[count]);
        }
        sb.append("\nDisplayable Name = " + getDisplayableName());
        sb.append("\nMedia Controller = " + getMediaController().toString());

        return sb.toString();
    }
}

/* stores information for browsable Media Players available in the system */
class BrowsePlayerInfo {
    String packageName;
    String displayableName;
    String serviceClass;

    public BrowsePlayerInfo(String packageName, String displayableName, String serviceClass) {
        this.packageName = packageName;
        this.displayableName = displayableName;
        this.serviceClass = serviceClass;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n+++ BrowsePlayerInfo: +++");
        sb.append("\nPackage Name = " + packageName);
        sb.append("\nDisplayable Name = " + displayableName);
        sb.append("\nService Class = " + serviceClass);
        return sb.toString();
    }
}

class FolderItemsData {
    /* initialize sizes for rsp parameters */
    int mNumItems;
    int[] mAttributesNum;
    byte[] mFolderTypes ;
    byte[] mItemTypes;
    byte[] mPlayable;
    byte[] mItemUid;
    String[] mDisplayNames;
    int[] mAttrIds;
    String[] mAttrValues;
    int attrCounter;

    public FolderItemsData(int size) {
        mNumItems = size;
        mAttributesNum = new int[size];

        mFolderTypes = new byte[size]; /* folderTypes */
        mItemTypes = new byte[size]; /* folder or media item */
        mPlayable = new byte[size];
        Arrays.fill(mFolderTypes, AvrcpConstants.FOLDER_TYPE_MIXED);
        Arrays.fill(mItemTypes, AvrcpConstants.BTRC_ITEM_MEDIA);
        Arrays.fill(mPlayable, AvrcpConstants.ITEM_PLAYABLE);

        mItemUid = new byte[size * AvrcpConstants.UID_SIZE];
        mDisplayNames = new String[size];

        mAttrIds = null; /* array of attr ids */
        mAttrValues = null; /* array of attr values */
    }
}
