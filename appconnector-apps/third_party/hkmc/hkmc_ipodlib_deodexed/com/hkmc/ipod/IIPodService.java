// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;

import android.os.*;

// Referenced classes of package com.hkmc.ipod:
//            IIPodListener, ArtWorkFormat, DisplayImageLimits, CurrentPlayingStatus, 
//            IosAppData

public interface IIPodService
    extends IInterface
{
    public static abstract class Stub extends Binder
        implements IIPodService
    {

        public static IIPodService asInterface(IBinder ibinder)
        {
            if(ibinder == null)
                return null;
            IInterface iinterface = ibinder.queryLocalInterface("com.hkmc.ipod.IIPodService");
            if(iinterface != null && (iinterface instanceof IIPodService))
                return (IIPodService)iinterface;
            else
                return new Proxy(ibinder);
        }

        public IBinder asBinder()
        {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel1, int j)
            throws RemoteException
        {
            switch(i)
            {
            default:
                return super.onTransact(i, parcel, parcel1, j);

            case 1598968902: 
                parcel1.writeString("com.hkmc.ipod.IIPodService");
                return true;

            case 1: // '\001'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                addListener(IIPodListener.Stub.asInterface(parcel.readStrongBinder()));
                parcel1.writeNoException();
                return true;

            case 2: // '\002'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                removeListener(IIPodListener.Stub.asInterface(parcel.readStrongBinder()));
                parcel1.writeNoException();
                return true;

            case 3: // '\003'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                ParcelFileDescriptor parcelfiledescriptor;
                int l14;
                IosAppData aiosappdata[];
                int i15;
                if(parcel.readInt() != 0)
                    parcelfiledescriptor = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(parcel);
                else
                    parcelfiledescriptor = null;
                l14 = parcel.readInt();
                aiosappdata = (IosAppData[])parcel.createTypedArray(IosAppData.CREATOR);
                i15 = openIPodDevice(parcelfiledescriptor, l14, aiosappdata);
                parcel1.writeNoException();
                parcel1.writeInt(i15);
                return true;

            case 4: // '\004'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k14 = closeIPodDevice();
                parcel1.writeNoException();
                parcel1.writeInt(k14);
                return true;

            case 5: // '\005'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j14 = play();
                parcel1.writeNoException();
                parcel1.writeInt(j14);
                return true;

            case 6: // '\006'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i14 = stop();
                parcel1.writeNoException();
                parcel1.writeInt(i14);
                return true;

            case 7: // '\007'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l13 = togglePause();
                parcel1.writeNoException();
                parcel1.writeInt(l13);
                return true;

            case 8: // '\b'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k13 = skipFwdStart();
                parcel1.writeNoException();
                parcel1.writeInt(k13);
                return true;

            case 9: // '\t'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j13 = skipRwdStart();
                parcel1.writeNoException();
                parcel1.writeInt(j13);
                return true;

            case 10: // '\n'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i13 = skipStop();
                parcel1.writeNoException();
                parcel1.writeInt(i13);
                return true;

            case 11: // '\013'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l12 = playNextTrack();
                parcel1.writeNoException();
                parcel1.writeInt(l12);
                return true;

            case 12: // '\f'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k12 = playPrevTrack();
                parcel1.writeNoException();
                parcel1.writeInt(k12);
                return true;

            case 13: // '\r'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j12 = setPlayPosition(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(j12);
                return true;

            case 14: // '\016'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i12 = getShuffleSetting();
                parcel1.writeNoException();
                parcel1.writeInt(i12);
                return true;

            case 15: // '\017'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l11 = setShuffleSetting(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(l11);
                return true;

            case 16: // '\020'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k11 = getRepeatSetting();
                parcel1.writeNoException();
                parcel1.writeInt(k11);
                return true;

            case 17: // '\021'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j11 = setRepeatSetting(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(j11);
                return true;

            case 18: // '\022'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                boolean flag1 = isAppSupported();
                parcel1.writeNoException();
                int i11;
                if(flag1)
                    i11 = 1;
                else
                    i11 = 0;
                parcel1.writeInt(i11);
                return true;

            case 19: // '\023'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                String s3 = getNowPlayingApplicationID();
                parcel1.writeNoException();
                parcel1.writeString(s3);
                return true;

            case 20: // '\024'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l10 = resumeMusicApp();
                parcel1.writeNoException();
                parcel1.writeInt(l10);
                return true;

            case 21: // '\025'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k10 = dbReset();
                parcel1.writeNoException();
                parcel1.writeInt(k10);
                return true;

            case 22: // '\026'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j10 = dbGetCount(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(j10);
                return true;

            case 23: // '\027'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i10 = dbSelection(parcel.readInt(), parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(i10);
                return true;

            case 24: // '\030'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l9 = dbPlaySelected(parcel.readInt(), parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(l9);
                return true;

            case 25: // '\031'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                String s2 = dbGetEntry(parcel.readInt(), parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeString(s2);
                return true;

            case 26: // '\032'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k9 = getPlayStatus();
                parcel1.writeNoException();
                parcel1.writeInt(k9);
                return true;

            case 27: // '\033'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j9 = getPlayTrackIndex();
                parcel1.writeNoException();
                parcel1.writeInt(j9);
                return true;

            case 28: // '\034'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i9 = getPlayTrackCount();
                parcel1.writeNoException();
                parcel1.writeInt(i9);
                return true;

            case 29: // '\035'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l8 = playTrack(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(l8);
                return true;

            case 30: // '\036'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                String s1 = getPlayTrackInfo(parcel.readInt(), parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeString(s1);
                return true;

            case 31: // '\037'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k8 = getPlayingTrackTotaltime();
                parcel1.writeNoException();
                parcel1.writeInt(k8);
                return true;

            case 32: // ' '
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j8 = getPlayingTrackElapsedtime();
                parcel1.writeNoException();
                parcel1.writeInt(j8);
                return true;

            case 33: // '!'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j7 = parcel.readInt();
                int k7 = parcel.readInt();
                byte abyte1[];
                int l7;
                int i8;
                if(k7 < 0)
                    abyte1 = null;
                else
                    abyte1 = new byte[k7];
                l7 = parcel.readInt();
                i8 = getPlayingTrackAlbumArt(j7, abyte1, l7);
                parcel1.writeNoException();
                parcel1.writeInt(i8);
                parcel1.writeByteArray(abyte1);
                return true;

            case 34: // '"'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                byte abyte0[] = getIPodSWVersion();
                parcel1.writeNoException();
                parcel1.writeByteArray(abyte0);
                return true;

            case 35: // '#'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i7 = getNumEqProfiles();
                parcel1.writeNoException();
                parcel1.writeInt(i7);
                return true;

            case 36: // '$'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                String s = getIndexedEqProfileName(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeString(s);
                return true;

            case 37: // '%'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l6 = getCurrentEqIndex();
                parcel1.writeNoException();
                parcel1.writeInt(l6);
                return true;

            case 38: // '&'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k6 = setCurrentEqIndex(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(k6);
                return true;

            case 39: // '\''
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j6 = getVolume();
                parcel1.writeNoException();
                parcel1.writeInt(j6);
                return true;

            case 40: // '('
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i6 = setVolume(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(i6);
                return true;

            case 41: // ')'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                boolean flag = isTaggingSupported();
                parcel1.writeNoException();
                int l5;
                if(flag)
                    l5 = 1;
                else
                    l5 = 0;
                parcel1.writeInt(l5);
                return true;

            case 42: // '*'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k5 = openTagFile(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString());
                parcel1.writeNoException();
                parcel1.writeInt(k5);
                return true;

            case 43: // '+'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j5 = getMaxNumTags(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(j5);
                return true;

            case 44: // ','
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i5 = addTag(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readString(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.createByteArray());
                parcel1.writeNoException();
                parcel1.writeInt(i5);
                return true;

            case 45: // '-'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l4 = closeTagFile(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(l4);
                return true;

            case 46: // '.'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k4 = appSendData(parcel.readInt(), parcel.readInt(), parcel.createByteArray());
                parcel1.writeNoException();
                parcel1.writeInt(k4);
                return true;

            case 47: // '/'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j4 = appLaunch(parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(j4);
                return true;

            case 48: // '0'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                onModeChanged(parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 49: // '1'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i4 = enterIpodRemoteUIMode();
                parcel1.writeNoException();
                parcel1.writeInt(i4);
                return true;

            case 50: // '2'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l3 = exitIpodRemoteUIMode();
                parcel1.writeNoException();
                parcel1.writeInt(l3);
                return true;

            case 51: // '3'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i3 = parcel.readInt();
                int j3 = parcel.readInt();
                int ai[];
                int k3;
                if(j3 < 0)
                    ai = null;
                else
                    ai = new int[j3];
                k3 = getLingoProtocolVersion(i3, ai);
                parcel1.writeNoException();
                parcel1.writeInt(k3);
                parcel1.writeIntArray(ai);
                return true;

            case 52: // '4'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l2 = getRemoteUIMode();
                parcel1.writeNoException();
                parcel1.writeInt(l2);
                return true;

            case 53: // '5'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k2 = setDisplayImage(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.createByteArray());
                parcel1.writeNoException();
                parcel1.writeInt(k2);
                return true;

            case 54: // '6'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                DisplayImageLimits displayimagelimits = new DisplayImageLimits();
                int j2 = getColorDisplayImageLimits(displayimagelimits);
                parcel1.writeNoException();
                parcel1.writeInt(j2);
                if(displayimagelimits != null)
                {
                    parcel1.writeInt(1);
                    displayimagelimits.writeToParcel(parcel1, 1);
                } else
                {
                    parcel1.writeInt(0);
                }
                return true;

            case 55: // '7'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l1 = parcel.readInt();
                ArtWorkFormat aartworkformat[];
                int i2;
                if(l1 < 0)
                    aartworkformat = null;
                else
                    aartworkformat = new ArtWorkFormat[l1];
                i2 = getArtWorkFormats(aartworkformat);
                parcel1.writeNoException();
                parcel1.writeInt(i2);
                parcel1.writeTypedArray(aartworkformat, 1);
                return true;

            case 56: // '8'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                CurrentPlayingStatus currentplayingstatus = new CurrentPlayingStatus();
                int k1 = getCurrentPlayingStatus(currentplayingstatus);
                parcel1.writeNoException();
                parcel1.writeInt(k1);
                if(currentplayingstatus != null)
                {
                    parcel1.writeInt(1);
                    currentplayingstatus.writeToParcel(parcel1, 1);
                } else
                {
                    parcel1.writeInt(0);
                }
                return true;

            case 57: // '9'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int j1 = getPlayingTrackChapterCount();
                parcel1.writeNoException();
                parcel1.writeInt(j1);
                return true;

            case 58: // ':'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                setAudioMute(parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 59: // ';'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int i1 = getAudioMute();
                parcel1.writeNoException();
                parcel1.writeInt(i1);
                return true;

            case 60: // '<'
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int l = pause();
                parcel1.writeNoException();
                parcel1.writeInt(l);
                return true;

            case 61: // '='
                parcel.enforceInterface("com.hkmc.ipod.IIPodService");
                int k = cancelCmd(parcel.readInt(), parcel.readInt());
                parcel1.writeNoException();
                parcel1.writeInt(k);
                return true;
            }
        }

        private static final String DESCRIPTOR = "com.hkmc.ipod.IIPodService";
        static final int TRANSACTION_addListener = 1;
        static final int TRANSACTION_addTag = 44;
        static final int TRANSACTION_appLaunch = 47;
        static final int TRANSACTION_appSendData = 46;
        static final int TRANSACTION_cancelCmd = 61;
        static final int TRANSACTION_closeIPodDevice = 4;
        static final int TRANSACTION_closeTagFile = 45;
        static final int TRANSACTION_dbGetCount = 22;
        static final int TRANSACTION_dbGetEntry = 25;
        static final int TRANSACTION_dbPlaySelected = 24;
        static final int TRANSACTION_dbReset = 21;
        static final int TRANSACTION_dbSelection = 23;
        static final int TRANSACTION_enterIpodRemoteUIMode = 49;
        static final int TRANSACTION_exitIpodRemoteUIMode = 50;
        static final int TRANSACTION_getArtWorkFormats = 55;
        static final int TRANSACTION_getAudioMute = 59;
        static final int TRANSACTION_getColorDisplayImageLimits = 54;
        static final int TRANSACTION_getCurrentEqIndex = 37;
        static final int TRANSACTION_getCurrentPlayingStatus = 56;
        static final int TRANSACTION_getIPodSWVersion = 34;
        static final int TRANSACTION_getIndexedEqProfileName = 36;
        static final int TRANSACTION_getLingoProtocolVersion = 51;
        static final int TRANSACTION_getMaxNumTags = 43;
        static final int TRANSACTION_getNowPlayingApplicationID = 19;
        static final int TRANSACTION_getNumEqProfiles = 35;
        static final int TRANSACTION_getPlayStatus = 26;
        static final int TRANSACTION_getPlayTrackCount = 28;
        static final int TRANSACTION_getPlayTrackIndex = 27;
        static final int TRANSACTION_getPlayTrackInfo = 30;
        static final int TRANSACTION_getPlayingTrackAlbumArt = 33;
        static final int TRANSACTION_getPlayingTrackChapterCount = 57;
        static final int TRANSACTION_getPlayingTrackElapsedtime = 32;
        static final int TRANSACTION_getPlayingTrackTotaltime = 31;
        static final int TRANSACTION_getRemoteUIMode = 52;
        static final int TRANSACTION_getRepeatSetting = 16;
        static final int TRANSACTION_getShuffleSetting = 14;
        static final int TRANSACTION_getVolume = 39;
        static final int TRANSACTION_isAppSupported = 18;
        static final int TRANSACTION_isTaggingSupported = 41;
        static final int TRANSACTION_onModeChanged = 48;
        static final int TRANSACTION_openIPodDevice = 3;
        static final int TRANSACTION_openTagFile = 42;
        static final int TRANSACTION_pause = 60;
        static final int TRANSACTION_play = 5;
        static final int TRANSACTION_playNextTrack = 11;
        static final int TRANSACTION_playPrevTrack = 12;
        static final int TRANSACTION_playTrack = 29;
        static final int TRANSACTION_removeListener = 2;
        static final int TRANSACTION_resumeMusicApp = 20;
        static final int TRANSACTION_setAudioMute = 58;
        static final int TRANSACTION_setCurrentEqIndex = 38;
        static final int TRANSACTION_setDisplayImage = 53;
        static final int TRANSACTION_setPlayPosition = 13;
        static final int TRANSACTION_setRepeatSetting = 17;
        static final int TRANSACTION_setShuffleSetting = 15;
        static final int TRANSACTION_setVolume = 40;
        static final int TRANSACTION_skipFwdStart = 8;
        static final int TRANSACTION_skipRwdStart = 9;
        static final int TRANSACTION_skipStop = 10;
        static final int TRANSACTION_stop = 6;
        static final int TRANSACTION_togglePause = 7;

        public Stub()
        {
            attachInterface(this, "com.hkmc.ipod.IIPodService");
        }
    }

    private static class Stub.Proxy
        implements IIPodService
    {

        public void addListener(IIPodListener iipodlistener)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            if(iipodlistener == null)
                break MISSING_BLOCK_LABEL_59;
            IBinder ibinder = iipodlistener.asBinder();
_L1:
            parcel.writeStrongBinder(ibinder);
            mRemote.transact(1, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            ibinder = null;
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int addTag(int i, int j, int k, String s, String s1, String s2, String s3, 
                int l, int i1, String s4, String s5, String s6, int j1, String s7, 
                int k1, String s8, String s9, byte abyte0[])
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int l1;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            parcel.writeInt(k);
            parcel.writeString(s);
            parcel.writeString(s1);
            parcel.writeString(s2);
            parcel.writeString(s3);
            parcel.writeInt(l);
            parcel.writeInt(i1);
            parcel.writeString(s4);
            parcel.writeString(s5);
            parcel.writeString(s6);
            parcel.writeInt(j1);
            parcel.writeString(s7);
            parcel.writeInt(k1);
            parcel.writeString(s8);
            parcel.writeString(s9);
            parcel.writeByteArray(abyte0);
            mRemote.transact(44, parcel, parcel1, 0);
            parcel1.readException();
            l1 = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return l1;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int appLaunch(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(47, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int appSendData(int i, int j, byte abyte0[])
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int k;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            parcel.writeByteArray(abyte0);
            mRemote.transact(46, parcel, parcel1, 0);
            parcel1.readException();
            k = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return k;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public IBinder asBinder()
        {
            return mRemote;
        }

        public int cancelCmd(int i, int j)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int k;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            mRemote.transact(61, parcel, parcel1, 0);
            parcel1.readException();
            k = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return k;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int closeIPodDevice()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(4, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int closeTagFile(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(45, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int dbGetCount(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(22, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public String dbGetEntry(int i, int j)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            String s;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            mRemote.transact(25, parcel, parcel1, 0);
            parcel1.readException();
            s = parcel1.readString();
            parcel1.recycle();
            parcel.recycle();
            return s;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int dbPlaySelected(int i, int j)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int k;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            mRemote.transact(24, parcel, parcel1, 0);
            parcel1.readException();
            k = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return k;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int dbReset()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(21, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int dbSelection(int i, int j)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int k;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            mRemote.transact(23, parcel, parcel1, 0);
            parcel1.readException();
            k = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return k;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int enterIpodRemoteUIMode()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(49, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int exitIpodRemoteUIMode()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(50, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getArtWorkFormats(ArtWorkFormat aartworkformat[])
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            if(aartworkformat != null)
                break MISSING_BLOCK_LABEL_67;
            parcel.writeInt(-1);
_L1:
            int i;
            mRemote.transact(55, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.readTypedArray(aartworkformat, ArtWorkFormat.CREATOR);
            parcel1.recycle();
            parcel.recycle();
            return i;
            parcel.writeInt(aartworkformat.length);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getAudioMute()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(59, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getColorDisplayImageLimits(DisplayImageLimits displayimagelimits)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(54, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            if(parcel1.readInt() != 0)
                displayimagelimits.readFromParcel(parcel1);
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getCurrentEqIndex()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(37, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getCurrentPlayingStatus(CurrentPlayingStatus currentplayingstatus)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(56, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            if(parcel1.readInt() != 0)
                currentplayingstatus.readFromParcel(parcel1);
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public byte[] getIPodSWVersion()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            byte abyte0[];
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(34, parcel, parcel1, 0);
            parcel1.readException();
            abyte0 = parcel1.createByteArray();
            parcel1.recycle();
            parcel.recycle();
            return abyte0;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public String getIndexedEqProfileName(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            String s;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(36, parcel, parcel1, 0);
            parcel1.readException();
            s = parcel1.readString();
            parcel1.recycle();
            parcel.recycle();
            return s;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public String getInterfaceDescriptor()
        {
            return "com.hkmc.ipod.IIPodService";
        }

        public int getLingoProtocolVersion(int i, int ai[])
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            if(ai != null)
                break MISSING_BLOCK_LABEL_75;
            parcel.writeInt(-1);
_L1:
            int j;
            mRemote.transact(51, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.readIntArray(ai);
            parcel1.recycle();
            parcel.recycle();
            return j;
            parcel.writeInt(ai.length);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getMaxNumTags(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(43, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public String getNowPlayingApplicationID()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            String s;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(19, parcel, parcel1, 0);
            parcel1.readException();
            s = parcel1.readString();
            parcel1.recycle();
            parcel.recycle();
            return s;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getNumEqProfiles()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(35, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getPlayStatus()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(26, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getPlayTrackCount()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(28, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getPlayTrackIndex()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(27, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public String getPlayTrackInfo(int i, int j)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            String s;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            mRemote.transact(30, parcel, parcel1, 0);
            parcel1.readException();
            s = parcel1.readString();
            parcel1.recycle();
            parcel.recycle();
            return s;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getPlayingTrackAlbumArt(int i, byte abyte0[], int j)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            if(abyte0 != null)
                break MISSING_BLOCK_LABEL_87;
            parcel.writeInt(-1);
_L1:
            int k;
            parcel.writeInt(j);
            mRemote.transact(33, parcel, parcel1, 0);
            parcel1.readException();
            k = parcel1.readInt();
            parcel1.readByteArray(abyte0);
            parcel1.recycle();
            parcel.recycle();
            return k;
            parcel.writeInt(abyte0.length);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getPlayingTrackChapterCount()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(57, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getPlayingTrackElapsedtime()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(32, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getPlayingTrackTotaltime()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(31, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getRemoteUIMode()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(52, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getRepeatSetting()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(16, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getShuffleSetting()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(14, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int getVolume()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(39, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public boolean isAppSupported()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(18, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            boolean flag;
            if(i != 0)
                flag = true;
            else
                flag = false;
            parcel1.recycle();
            parcel.recycle();
            return flag;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public boolean isTaggingSupported()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(41, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            boolean flag;
            if(i != 0)
                flag = true;
            else
                flag = false;
            parcel1.recycle();
            parcel.recycle();
            return flag;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void onModeChanged(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(48, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int openIPodDevice(ParcelFileDescriptor parcelfiledescriptor, int i, IosAppData aiosappdata[])
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            if(parcelfiledescriptor == null)
                break MISSING_BLOCK_LABEL_88;
            parcel.writeInt(1);
            parcelfiledescriptor.writeToParcel(parcel, 0);
_L1:
            int j;
            parcel.writeInt(i);
            parcel.writeTypedArray(aiosappdata, 0);
            mRemote.transact(3, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            parcel.writeInt(0);
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int openTagFile(int i, int j, int k, String s, String s1)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int l;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            parcel.writeInt(k);
            parcel.writeString(s);
            parcel.writeString(s1);
            mRemote.transact(42, parcel, parcel1, 0);
            parcel1.readException();
            l = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return l;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int pause()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(60, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int play()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(5, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int playNextTrack()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(11, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int playPrevTrack()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(12, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int playTrack(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(29, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void removeListener(IIPodListener iipodlistener)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            if(iipodlistener == null)
                break MISSING_BLOCK_LABEL_59;
            IBinder ibinder = iipodlistener.asBinder();
_L1:
            parcel.writeStrongBinder(ibinder);
            mRemote.transact(2, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            ibinder = null;
              goto _L1
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int resumeMusicApp()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(20, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public void setAudioMute(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(58, parcel, parcel1, 0);
            parcel1.readException();
            parcel1.recycle();
            parcel.recycle();
            return;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int setCurrentEqIndex(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(38, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int setDisplayImage(int i, int j, int k, int l, byte abyte0[])
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i1;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            parcel.writeInt(j);
            parcel.writeInt(k);
            parcel.writeInt(l);
            parcel.writeByteArray(abyte0);
            mRemote.transact(53, parcel, parcel1, 0);
            parcel1.readException();
            i1 = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i1;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int setPlayPosition(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(13, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int setRepeatSetting(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(17, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int setShuffleSetting(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(15, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int setVolume(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            parcel.writeInt(i);
            mRemote.transact(40, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int skipFwdStart()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(8, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int skipRwdStart()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(9, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int skipStop()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(10, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int stop()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(6, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public int togglePause()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodService");
            mRemote.transact(7, parcel, parcel1, 0);
            parcel1.readException();
            i = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return i;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        private IBinder mRemote;

        Stub.Proxy(IBinder ibinder)
        {
            mRemote = ibinder;
        }
    }


    public abstract void addListener(IIPodListener iipodlistener)
        throws RemoteException;

    public abstract int addTag(int i, int j, int k, String s, String s1, String s2, String s3, 
            int l, int i1, String s4, String s5, String s6, int j1, String s7, 
            int k1, String s8, String s9, byte abyte0[])
        throws RemoteException;

    public abstract int appLaunch(int i)
        throws RemoteException;

    public abstract int appSendData(int i, int j, byte abyte0[])
        throws RemoteException;

    public abstract int cancelCmd(int i, int j)
        throws RemoteException;

    public abstract int closeIPodDevice()
        throws RemoteException;

    public abstract int closeTagFile(int i)
        throws RemoteException;

    public abstract int dbGetCount(int i)
        throws RemoteException;

    public abstract String dbGetEntry(int i, int j)
        throws RemoteException;

    public abstract int dbPlaySelected(int i, int j)
        throws RemoteException;

    public abstract int dbReset()
        throws RemoteException;

    public abstract int dbSelection(int i, int j)
        throws RemoteException;

    public abstract int enterIpodRemoteUIMode()
        throws RemoteException;

    public abstract int exitIpodRemoteUIMode()
        throws RemoteException;

    public abstract int getArtWorkFormats(ArtWorkFormat aartworkformat[])
        throws RemoteException;

    public abstract int getAudioMute()
        throws RemoteException;

    public abstract int getColorDisplayImageLimits(DisplayImageLimits displayimagelimits)
        throws RemoteException;

    public abstract int getCurrentEqIndex()
        throws RemoteException;

    public abstract int getCurrentPlayingStatus(CurrentPlayingStatus currentplayingstatus)
        throws RemoteException;

    public abstract byte[] getIPodSWVersion()
        throws RemoteException;

    public abstract String getIndexedEqProfileName(int i)
        throws RemoteException;

    public abstract int getLingoProtocolVersion(int i, int ai[])
        throws RemoteException;

    public abstract int getMaxNumTags(int i)
        throws RemoteException;

    public abstract String getNowPlayingApplicationID()
        throws RemoteException;

    public abstract int getNumEqProfiles()
        throws RemoteException;

    public abstract int getPlayStatus()
        throws RemoteException;

    public abstract int getPlayTrackCount()
        throws RemoteException;

    public abstract int getPlayTrackIndex()
        throws RemoteException;

    public abstract String getPlayTrackInfo(int i, int j)
        throws RemoteException;

    public abstract int getPlayingTrackAlbumArt(int i, byte abyte0[], int j)
        throws RemoteException;

    public abstract int getPlayingTrackChapterCount()
        throws RemoteException;

    public abstract int getPlayingTrackElapsedtime()
        throws RemoteException;

    public abstract int getPlayingTrackTotaltime()
        throws RemoteException;

    public abstract int getRemoteUIMode()
        throws RemoteException;

    public abstract int getRepeatSetting()
        throws RemoteException;

    public abstract int getShuffleSetting()
        throws RemoteException;

    public abstract int getVolume()
        throws RemoteException;

    public abstract boolean isAppSupported()
        throws RemoteException;

    public abstract boolean isTaggingSupported()
        throws RemoteException;

    public abstract void onModeChanged(int i)
        throws RemoteException;

    public abstract int openIPodDevice(ParcelFileDescriptor parcelfiledescriptor, int i, IosAppData aiosappdata[])
        throws RemoteException;

    public abstract int openTagFile(int i, int j, int k, String s, String s1)
        throws RemoteException;

    public abstract int pause()
        throws RemoteException;

    public abstract int play()
        throws RemoteException;

    public abstract int playNextTrack()
        throws RemoteException;

    public abstract int playPrevTrack()
        throws RemoteException;

    public abstract int playTrack(int i)
        throws RemoteException;

    public abstract void removeListener(IIPodListener iipodlistener)
        throws RemoteException;

    public abstract int resumeMusicApp()
        throws RemoteException;

    public abstract void setAudioMute(int i)
        throws RemoteException;

    public abstract int setCurrentEqIndex(int i)
        throws RemoteException;

    public abstract int setDisplayImage(int i, int j, int k, int l, byte abyte0[])
        throws RemoteException;

    public abstract int setPlayPosition(int i)
        throws RemoteException;

    public abstract int setRepeatSetting(int i)
        throws RemoteException;

    public abstract int setShuffleSetting(int i)
        throws RemoteException;

    public abstract int setVolume(int i)
        throws RemoteException;

    public abstract int skipFwdStart()
        throws RemoteException;

    public abstract int skipRwdStart()
        throws RemoteException;

    public abstract int skipStop()
        throws RemoteException;

    public abstract int stop()
        throws RemoteException;

    public abstract int togglePause()
        throws RemoteException;
}
