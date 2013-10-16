// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;

import android.os.*;

public interface IIPodListener
    extends IInterface
{
    public static abstract class Stub extends Binder
        implements IIPodListener
    {

        public static IIPodListener asInterface(IBinder ibinder)
        {
            if(ibinder == null)
                return null;
            IInterface iinterface = ibinder.queryLocalInterface("com.hkmc.ipod.IIPodListener");
            if(iinterface != null && (iinterface instanceof IIPodListener))
                return (IIPodListener)iinterface;
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
                parcel1.writeString("com.hkmc.ipod.IIPodListener");
                return true;

            case 1: // '\001'
                parcel.enforceInterface("com.hkmc.ipod.IIPodListener");
                onConnectionInfoReceived(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString());
                parcel1.writeNoException();
                return true;

            case 2: // '\002'
                parcel.enforceInterface("com.hkmc.ipod.IIPodListener");
                onPlayStatusChanged(parcel.readInt(), parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 3: // '\003'
                parcel.enforceInterface("com.hkmc.ipod.IIPodListener");
                onAppOpenSession(parcel.readInt(), parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 4: // '\004'
                parcel.enforceInterface("com.hkmc.ipod.IIPodListener");
                onAppCloseSession(parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 5: // '\005'
                parcel.enforceInterface("com.hkmc.ipod.IIPodListener");
                onAppDataReceived(parcel.readInt(), parcel.createByteArray());
                parcel1.writeNoException();
                return true;

            case 6: // '\006'
                parcel.enforceInterface("com.hkmc.ipod.IIPodListener");
                onIPodNotification(parcel.readInt());
                parcel1.writeNoException();
                return true;

            case 7: // '\007'
                parcel.enforceInterface("com.hkmc.ipod.IIPodListener");
                onNowPlayingApplicationNameData(parcel.readInt(), parcel.readString());
                parcel1.writeNoException();
                return true;

            case 8: // '\b'
                parcel.enforceInterface("com.hkmc.ipod.IIPodListener");
                onOtgReset();
                parcel1.writeNoException();
                return true;
            }
        }

        private static final String DESCRIPTOR = "com.hkmc.ipod.IIPodListener";
        static final int TRANSACTION_onAppCloseSession = 4;
        static final int TRANSACTION_onAppDataReceived = 5;
        static final int TRANSACTION_onAppOpenSession = 3;
        static final int TRANSACTION_onConnectionInfoReceived = 1;
        static final int TRANSACTION_onIPodNotification = 6;
        static final int TRANSACTION_onNowPlayingApplicationNameData = 7;
        static final int TRANSACTION_onOtgReset = 8;
        static final int TRANSACTION_onPlayStatusChanged = 2;

        public Stub()
        {
            attachInterface(this, "com.hkmc.ipod.IIPodListener");
        }
    }

    private static class Stub.Proxy
        implements IIPodListener
    {

        public IBinder asBinder()
        {
            return mRemote;
        }

        public String getInterfaceDescriptor()
        {
            return "com.hkmc.ipod.IIPodListener";
        }

        public void onAppCloseSession(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodListener");
            parcel.writeInt(i);
            mRemote.transact(4, parcel, parcel1, 0);
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

        public void onAppDataReceived(int i, byte abyte0[])
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodListener");
            parcel.writeInt(i);
            parcel.writeByteArray(abyte0);
            mRemote.transact(5, parcel, parcel1, 0);
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

        public void onAppOpenSession(int i, int j)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodListener");
            parcel.writeInt(i);
            parcel.writeInt(j);
            mRemote.transact(3, parcel, parcel1, 0);
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

        public void onConnectionInfoReceived(int i, int j, int k, int l, int i1, String s, String s1, 
                String s2)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodListener");
            parcel.writeInt(i);
            parcel.writeInt(j);
            parcel.writeInt(k);
            parcel.writeInt(l);
            parcel.writeInt(i1);
            parcel.writeString(s);
            parcel.writeString(s1);
            parcel.writeString(s2);
            mRemote.transact(1, parcel, parcel1, 0);
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

        public void onIPodNotification(int i)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodListener");
            parcel.writeInt(i);
            mRemote.transact(6, parcel, parcel1, 0);
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

        public void onNowPlayingApplicationNameData(int i, String s)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodListener");
            parcel.writeInt(i);
            parcel.writeString(s);
            mRemote.transact(7, parcel, parcel1, 0);
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

        public void onOtgReset()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodListener");
            mRemote.transact(8, parcel, parcel1, 0);
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

        public void onPlayStatusChanged(int i, int j)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodListener");
            parcel.writeInt(i);
            parcel.writeInt(j);
            mRemote.transact(2, parcel, parcel1, 0);
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

        private IBinder mRemote;

        Stub.Proxy(IBinder ibinder)
        {
            mRemote = ibinder;
        }
    }


    public abstract void onAppCloseSession(int i)
        throws RemoteException;

    public abstract void onAppDataReceived(int i, byte abyte0[])
        throws RemoteException;

    public abstract void onAppOpenSession(int i, int j)
        throws RemoteException;

    public abstract void onConnectionInfoReceived(int i, int j, int k, int l, int i1, String s, String s1, 
            String s2)
        throws RemoteException;

    public abstract void onIPodNotification(int i)
        throws RemoteException;

    public abstract void onNowPlayingApplicationNameData(int i, String s)
        throws RemoteException;

    public abstract void onOtgReset()
        throws RemoteException;

    public abstract void onPlayStatusChanged(int i, int j)
        throws RemoteException;
}
