// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;

import android.os.Parcel;
import android.os.Parcelable;

public final class ArtWorkFormat
    implements Parcelable
{

    public ArtWorkFormat()
    {
    }

    public ArtWorkFormat(int i, int j, int k, int l)
    {
        mId = i;
        mFormat = j;
        mWidth = k;
        mHeight = l;
    }

    public ArtWorkFormat(Parcel parcel)
    {
        mId = parcel.readInt();
        mFormat = parcel.readInt();
        mWidth = parcel.readInt();
        mHeight = parcel.readInt();
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

    public int getId()
    {
        return mId;
    }

    public int getWidth()
    {
        return mWidth;
    }

    public void readFromParcel(Parcel parcel)
    {
        mId = parcel.readInt();
        mFormat = parcel.readInt();
        mWidth = parcel.readInt();
        mHeight = parcel.readInt();
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append((new StringBuilder()).append("ArtworkFormat { Id:").append(mId).toString());
        stringbuilder.append((new StringBuilder()).append("Format:").append(mFormat).toString());
        stringbuilder.append((new StringBuilder()).append("Width:").append(mWidth).toString());
        stringbuilder.append((new StringBuilder()).append("Height:").append(mHeight).toString());
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(mId);
        parcel.writeInt(mFormat);
        parcel.writeInt(mWidth);
        parcel.writeInt(mHeight);
    }

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public ArtWorkFormat createFromParcel(Parcel parcel)
        {
            return new ArtWorkFormat(parcel);
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public ArtWorkFormat[] newArray(int i)
        {
            return new ArtWorkFormat[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    }
;
    int mFormat;
    int mHeight;
    int mId;
    int mWidth;

}
