// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;

import android.os.*;

public interface IIPodObserverListener
    extends IInterface
{
    public static abstract class Stub extends Binder
        implements IIPodObserverListener
    {

        public static IIPodObserverListener asInterface(IBinder ibinder)
        {
            if(ibinder == null)
                return null;
            IInterface iinterface = ibinder.queryLocalInterface("com.hkmc.ipod.IIPodObserverListener");
            if(iinterface != null && (iinterface instanceof IIPodObserverListener))
                return (IIPodObserverListener)iinterface;
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
                parcel1.writeString("com.hkmc.ipod.IIPodObserverListener");
                return true;

            case 1: // '\001'
                parcel.enforceInterface("com.hkmc.ipod.IIPodObserverListener");
                onConnectionChanged(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString());
                parcel1.writeNoException();
                return true;
            }
        }

        private static final String DESCRIPTOR = "com.hkmc.ipod.IIPodObserverListener";
        static final int TRANSACTION_onConnectionChanged = 1;

        public Stub()
        {
            attachInterface(this, "com.hkmc.ipod.IIPodObserverListener");
        }
    }

    private static class Stub.Proxy
        implements IIPodObserverListener
    {

        public IBinder asBinder()
        {
            return mRemote;
        }

        public String getInterfaceDescriptor()
        {
            return "com.hkmc.ipod.IIPodObserverListener";
        }

        public void onConnectionChanged(int i, String s, String s1, String s2)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodObserverListener");
            parcel.writeInt(i);
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

        private IBinder mRemote;

        Stub.Proxy(IBinder ibinder)
        {
            mRemote = ibinder;
        }
    }


    public abstract void onConnectionChanged(int i, String s, String s1, String s2)
        throws RemoteException;
}
