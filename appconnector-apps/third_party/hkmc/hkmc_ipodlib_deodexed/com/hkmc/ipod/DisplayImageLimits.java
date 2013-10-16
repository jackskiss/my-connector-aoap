// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;

import android.os.Parcel;
import android.os.Parcelable;

public final class DisplayImageLimits
    implements Parcelable
{

    public DisplayImageLimits()
    {
    }

    public DisplayImageLimits(int i, int j, int k)
    {
        mWidth = i;
        mHeight = j;
        mFormat = k;
    }

    public DisplayImageLimits(Parcel parcel)
    {
        mWidth = parcel.readInt();
        mHeight = parcel.readInt();
        mFormat = parcel.readInt();
    }

    public int describeContents()
    {
        return 0;
    }

    public int getFormat()
    {
        return mFormat;
    }

    public int getHeight()
    {
        return mHeight;
    }

    public int getWidth()
    {
        return mWidth;
    }

    public void readFromParcel(Parcel parcel)
    {
        mWidth = parcel.readInt();
        mHeight = parcel.readInt();
        mFormat = parcel.readInt();
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append((new StringBuilder()).append("Display Image Limits {Width:").append(mWidth).toString());
        stringbuilder.append((new StringBuilder()).append("Height:").append(mHeight).toString());
        stringbuilder.append((new StringBuilder()).append("Format:").append(mFormat).toString());
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(mWidth);
        parcel.writeInt(mHeight);
        parcel.writeInt(mFormat);
    }

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public DisplayImageLimits createFromParcel(Parcel parcel)
        {
            return new DisplayImageLimits(parcel);
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public DisplayImageLimits[] newArray(int i)
        {
            return new DisplayImageLimits[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    }
;
    int mFormat;
    int mHeight;
    int mWidth;

}
