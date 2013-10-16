// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;

import android.os.Parcel;
import android.os.Parcelable;

public final class IosAppData
    implements Parcelable
{

    public IosAppData(Parcel parcel)
    {
        appName = parcel.readString();
        bundleSeed = parcel.readString();
        sdkProtocol = parcel.readString();
    }

    public IosAppData(String s, String s1, String s2)
    {
        appName = s;
        bundleSeed = s1;
        sdkProtocol = s2;
    }

    public int describeContents()
    {
        return 0;
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append((new StringBuilder()).append("IOS App Data {App Name: ").append(appName).toString());
        stringbuilder.append((new StringBuilder()).append("Bundle Seed: ").append(bundleSeed).toString());
        stringbuilder.append((new StringBuilder()).append("SDK Protocol: ").append(sdkProtocol).toString());
        stringbuilder.append("}");
        return stringbuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(appName);
        parcel.writeString(bundleSeed);
        parcel.writeString(sdkProtocol);
    }

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public IosAppData createFromParcel(Parcel parcel)
        {
            return new IosAppData(parcel);
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public IosAppData[] newArray(int i)
        {
            return new IosAppData[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    }
;
    String appName;
    String bundleSeed;
    String sdkProtocol;

}
