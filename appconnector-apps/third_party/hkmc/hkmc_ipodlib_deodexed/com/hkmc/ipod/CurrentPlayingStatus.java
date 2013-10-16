// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;

import android.os.Parcel;
import android.os.Parcelable;

public final class CurrentPlayingStatus
    implements Parcelable
{

    public CurrentPlayingStatus()
    {
    }

    public CurrentPlayingStatus(int i, int j, int k, int l)
    {
        mPlayStatus = i;
        mTrackIndex = j;
        mTotalTime = k;
        mElapsedTime = l;
    }

    public CurrentPlayingStatus(Parcel parcel)
    {
        mPlayStatus = parcel.readInt();
        mTrackIndex = parcel.readInt();
        mTotalTime = parcel.readInt();
        mElapsedTime = parcel.readInt();
    }

    public int describeContents()
    {
        return 0;
    }

    public int getElaspedTime()
    {
        return mElapsedTime;
    }

    public int getPlayStatus()
    {
        return mPlayStatus;
    }

    public int getTotalTime()
    {
        return mTotalTime;
    }

    public int getTrackIndex()
    {
        return mTrackIndex;
    }

    public void readFromParcel(Parcel parcel)
    {
        mPlayStatus = parcel.readInt();
        mTrackIndex = parcel.readInt();
        mTotalTime = parcel.readInt();
        mElapsedTime = parcel.readInt();
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append((new StringBuilder()).append("CurrentPlayingStatus { PlayStaus:").append(mPlayStatus).toString());
        stringbuilder.append((new StringBuilder()).append("TrackIndex:").append(mTrackIndex).toString());
        stringbuilder.append((new StringBuilder()).append("TotalTime:").append(mTotalTime).toString());
        stringbuilder.append((new StringBuilder()).append("ElaspsedTime:").append(mElapsedTime).toString());
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(mPlayStatus);
        parcel.writeInt(mTrackIndex);
        parcel.writeInt(mTotalTime);
        parcel.writeInt(mElapsedTime);
    }

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public CurrentPlayingStatus createFromParcel(Parcel parcel)
        {
            return new CurrentPlayingStatus(parcel);
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public CurrentPlayingStatus[] newArray(int i)
        {
            return new CurrentPlayingStatus[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    }
;
    private int mElapsedTime;
    private int mPlayStatus;
    private int mTotalTime;
    private int mTrackIndex;

}
