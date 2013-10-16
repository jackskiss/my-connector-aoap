// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;

import android.os.*;

// Referenced classes of package com.hkmc.ipod:
//            IIPodObserverListener

public interface IIPodObserver
    extends IInterface
{
    public static abstract class Stub extends Binder
        implements IIPodObserver
    {

        public static IIPodObserver asInterface(IBinder ibinder)
        {
            if(ibinder == null)
                return null;
            IInterface iinterface = ibinder.queryLocalInterface("com.hkmc.ipod.IIPodObserver");
            if(iinterface != null && (iinterface instanceof IIPodObserver))
                return (IIPodObserver)iinterface;
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
            boolean flag;
            switch(i)
            {
            default:
                return super.onTransact(i, parcel, parcel1, j);

            case 1598968902: 
                parcel1.writeString("com.hkmc.ipod.IIPodObserver");
                return true;

            case 1: // '\001'
                parcel.enforceInterface("com.hkmc.ipod.IIPodObserver");
                String as1[] = getConnectedDevices();
                parcel1.writeNoException();
                parcel1.writeStringArray(as1);
                return true;

            case 2: // '\002'
                parcel.enforceInterface("com.hkmc.ipod.IIPodObserver");
                String as[] = getConnectedDevicePaths();
                parcel1.writeNoException();
                parcel1.writeStringArray(as);
                return true;

            case 3: // '\003'
                parcel.enforceInterface("com.hkmc.ipod.IIPodObserver");
                addListener(IIPodObserverListener.Stub.asInterface(parcel.readStrongBinder()));
                parcel1.writeNoException();
                return true;

            case 4: // '\004'
                parcel.enforceInterface("com.hkmc.ipod.IIPodObserver");
                removeListener(IIPodObserverListener.Stub.asInterface(parcel.readStrongBinder()));
                parcel1.writeNoException();
                return true;

            case 5: // '\005'
                parcel.enforceInterface("com.hkmc.ipod.IIPodObserver");
                flag = getIpodColdPulgStatus();
                parcel1.writeNoException();
                break;
            }
            int k;
            if(flag)
                k = 1;
            else
                k = 0;
            parcel1.writeInt(k);
            return true;
        }

        private static final String DESCRIPTOR = "com.hkmc.ipod.IIPodObserver";
        static final int TRANSACTION_addListener = 3;
        static final int TRANSACTION_getConnectedDevicePaths = 2;
        static final int TRANSACTION_getConnectedDevices = 1;
        static final int TRANSACTION_getIpodColdPulgStatus = 5;
        static final int TRANSACTION_removeListener = 4;

        public Stub()
        {
            attachInterface(this, "com.hkmc.ipod.IIPodObserver");
        }
    }

    private static class Stub.Proxy
        implements IIPodObserver
    {

        public void addListener(IIPodObserverListener iipodobserverlistener)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodObserver");
            if(iipodobserverlistener == null)
                break MISSING_BLOCK_LABEL_59;
            IBinder ibinder = iipodobserverlistener.asBinder();
_L1:
            parcel.writeStrongBinder(ibinder);
            mRemote.transact(3, parcel, parcel1, 0);
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

        public IBinder asBinder()
        {
            return mRemote;
        }

        public String[] getConnectedDevicePaths()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            String as[];
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodObserver");
            mRemote.transact(2, parcel, parcel1, 0);
            parcel1.readException();
            as = parcel1.createStringArray();
            parcel1.recycle();
            parcel.recycle();
            return as;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public String[] getConnectedDevices()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            String as[];
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodObserver");
            mRemote.transact(1, parcel, parcel1, 0);
            parcel1.readException();
            as = parcel1.createStringArray();
            parcel1.recycle();
            parcel.recycle();
            return as;
            Exception exception;
            exception;
            parcel1.recycle();
            parcel.recycle();
            throw exception;
        }

        public String getInterfaceDescriptor()
        {
            return "com.hkmc.ipod.IIPodObserver";
        }

        public boolean getIpodColdPulgStatus()
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int i;
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodObserver");
            mRemote.transact(5, parcel, parcel1, 0);
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

        public void removeListener(IIPodObserverListener iipodobserverlistener)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.hkmc.ipod.IIPodObserver");
            if(iipodobserverlistener == null)
                break MISSING_BLOCK_LABEL_59;
            IBinder ibinder = iipodobserverlistener.asBinder();
_L1:
            parcel.writeStrongBinder(ibinder);
            mRemote.transact(4, parcel, parcel1, 0);
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

        private IBinder mRemote;

        Stub.Proxy(IBinder ibinder)
        {
            mRemote = ibinder;
        }
    }


    public abstract void addListener(IIPodObserverListener iipodobserverlistener)
        throws RemoteException;

    public abstract String[] getConnectedDevicePaths()
        throws RemoteException;

    public abstract String[] getConnectedDevices()
        throws RemoteException;

    public abstract boolean getIpodColdPulgStatus()
        throws RemoteException;

    public abstract void removeListener(IIPodObserverListener iipodobserverlistener)
        throws RemoteException;
}
