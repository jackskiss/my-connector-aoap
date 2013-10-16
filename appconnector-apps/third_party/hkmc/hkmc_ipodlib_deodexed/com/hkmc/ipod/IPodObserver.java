// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;

import android.content.Context;
import android.os.*;
import android.util.Slog;
import com.hkmc.Service;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.hkmc.ipod:
//            IIPodObserver, IPodObserverListener

public class IPodObserver
    implements android.os.Handler.Callback
{
    private final class ServiceListenerProxy extends IIPodObserverListener.Stub
    {

        public void onConnectionChanged(int i, String s, String s1, String s2)
            throws RemoteException
        {
            Message message = Message.obtain();
            message.what = 16;
            Bundle bundle = new Bundle();
            bundle.putInt("STATE", i);
            bundle.putString("IPODNAME", s);
            bundle.putString("IPODMODEL", s1);
            bundle.putString("IPODSERIAL", s2);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        final IPodObserver this$0;

        private ServiceListenerProxy()
        {
            this$0 = IPodObserver.this;
            super();
        }

    }


    public IPodObserver(Context context)
    {
        mListeners = new ArrayList();
        mServiceListenerProxy = null;
        if(context == null)
        {
            throw new IllegalArgumentException("IPodObserver() cannot be constructed with null context");
        } else
        {
            mService = getService();
            mContext = context;
            mHandler = new Handler(mContext.getMainLooper(), this);
            return;
        }
    }

    private void ensureAddListener()
    {
        if(mServiceListenerProxy != null)
            return;
        mServiceListenerProxy = new ServiceListenerProxy();
        try
        {
            mService.addListener(mServiceListenerProxy);
            return;
        }
        catch(RemoteException remoteexception)
        {
            return;
        }
    }

    private static IIPodObserver getService()
    {
        if(sService != null)
        {
            return sService;
        } else
        {
            sService = IIPodObserver.Stub.asInterface(ServiceManager.getService(Service.IPOD_OBSERVER_SERVICE));
            return sService;
        }
    }

    public void addListener(IPodObserverListener ipodobserverlistener)
    {
        if(ipodobserverlistener == null)
            throw new IllegalArgumentException("listener is null.");
        ensureAddListener();
        synchronized(mListeners)
        {
            mListeners.add(ipodobserverlistener);
        }
        return;
        exception;
        arraylist;
        JVM INSTR monitorexit ;
        throw exception;
    }

    public String[] getConnectedDevicePaths()
    {
        String as[];
        try
        {
            as = mService.getConnectedDevicePaths();
        }
        catch(RemoteException remoteexception)
        {
            Slog.i("IPodObserver", "getConnectedDevicePaths() RemoteException!");
            return null;
        }
        return as;
    }

    public String[] getConnectedDevices()
    {
        String as[];
        try
        {
            as = mService.getConnectedDevices();
        }
        catch(RemoteException remoteexception)
        {
            Slog.i("IPodObserver", "getConnectedDevices() RemoteException!");
            return null;
        }
        return as;
    }

    public boolean handleMessage(Message message)
    {
        int i;
        String s;
        String s1;
        String s2;
        if(message.what != 16)
            break MISSING_BLOCK_LABEL_111;
        Bundle bundle = message.getData();
        i = bundle.getInt("STATE");
        s = bundle.getString("IPODNAME");
        s1 = bundle.getString("IPODMODEL");
        s2 = bundle.getString("IPODSERIAL");
        ArrayList arraylist = mListeners;
        arraylist;
        JVM INSTR monitorenter ;
        for(Iterator iterator = mListeners.iterator(); iterator.hasNext(); ((IPodObserverListener)iterator.next()).onConnectionChanged(i, s, s1, s2));
        break MISSING_BLOCK_LABEL_106;
        Exception exception;
        exception;
        arraylist;
        JVM INSTR monitorexit ;
        throw exception;
        arraylist;
        JVM INSTR monitorexit ;
        return true;
        return false;
    }

    public void removeListener(IPodObserverListener ipodobserverlistener)
    {
        if(ipodobserverlistener == null)
            throw new IllegalArgumentException("listener is null.");
        ArrayList arraylist = mListeners;
        arraylist;
        JVM INSTR monitorenter ;
        int i = mListeners.indexOf(ipodobserverlistener);
        if(i >= 0)
            break MISSING_BLOCK_LABEL_39;
        arraylist;
        JVM INSTR monitorexit ;
        return;
        boolean flag;
        mListeners.remove(i);
        flag = mListeners.isEmpty();
        if(!flag)
            break MISSING_BLOCK_LABEL_81;
        Exception exception;
        try
        {
            mService.removeListener(mServiceListenerProxy);
        }
        catch(RemoteException remoteexception) { }
        mServiceListenerProxy = null;
        arraylist;
        JVM INSTR monitorexit ;
        return;
        exception;
        arraylist;
        JVM INSTR monitorexit ;
        throw exception;
    }

    public static final String EXTRA_IPOD_CONNECTED = "connected";
    public static final String EXTRA_IPOD_DEVICE = "device";
    public static final String EXTRA_IPOD_PRODUCT_ID = "product_id";
    public static final String EXTRA_IPOD_RELEASE_NO = "release_no";
    public static final String IPOD_CONNECTION_CHANGE_ACTION = "com.hkmc.ipod.CONNECTION_CHANGE";
    private static final int MSG_CONNECTION_CHANGED = 16;
    private static final String TAG = "IPodObserver";
    private static IIPodObserver sService;
    private Context mContext;
    private Handler mHandler;
    private ArrayList mListeners;
    private IIPodObserver mService;
    private ServiceListenerProxy mServiceListenerProxy;

}
