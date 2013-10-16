// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;

import android.content.Context;
import android.os.*;
import android.util.Log;
import com.hkmc.Service;
import java.util.*;

// Referenced classes of package com.hkmc.ipod:
//            IIPodObserver, IIPodService, IPodListener, IosAppData, 
//            ArtWorkFormat, DisplayImageLimits, CurrentPlayingStatus

public class IPodManager
{
    private final class IPodListenerWrapper extends IIPodListener.Stub
    {

        public void onAppCloseSession(int i)
        {
            Map map = mSessionIdMap;
            map;
            JVM INSTR monitorenter ;
            Iterator iterator = mSessionIdMap.entrySet().iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
                int j = ((Integer)entry.getKey()).intValue();
                if(i == ((Integer)entry.getValue()).intValue() && mSessionIdMap.containsKey(Integer.valueOf(j)))
                    mSessionIdMap.remove(Integer.valueOf(j));
            } while(true);
            break MISSING_BLOCK_LABEL_129;
            Exception exception;
            exception;
            map;
            JVM INSTR monitorexit ;
            throw exception;
            map;
            JVM INSTR monitorexit ;
            Message message = Message.obtain();
            message.what = 1004;
            Bundle bundle = new Bundle();
            bundle.putInt("CLOSESESSIONID", i);
            message.setData(bundle);
            mHandler.sendMessage(message);
            return;
        }

        public void onAppDataReceived(int i, byte abyte0[])
        {
            Message message = Message.obtain();
            message.what = 1005;
            Bundle bundle = new Bundle();
            bundle.putInt("DATASESSIONID", i);
            bundle.putByteArray("DATAARRAY", abyte0);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        public void onAppOpenSession(int i, int j)
        {
            synchronized(mSessionIdMap)
            {
                mSessionIdMap.put(Integer.valueOf(i), Integer.valueOf(j));
            }
            Message message = Message.obtain();
            message.what = 1003;
            Bundle bundle = new Bundle();
            bundle.putInt("APPID", i);
            bundle.putInt("OPENSESSIONID", j);
            message.setData(bundle);
            mHandler.sendMessage(message);
            return;
            exception;
            map;
            JVM INSTR monitorexit ;
            throw exception;
        }

        public void onConnectionInfoReceived(int i, int j, int k, int l, int i1, String s, String s1, 
                String s2)
        {
            Message message = Message.obtain();
            message.what = 1001;
            Bundle bundle = new Bundle();
            bundle.putInt("LINGOMAJOR", i);
            bundle.putInt("LINGOMINOR", j);
            bundle.putInt("IPODMAJOR", k);
            bundle.putInt("IPODMINOR", l);
            bundle.putInt("IPODREVISION", i1);
            bundle.putString("IPODNAME", s);
            bundle.putString("IPODMODEL", s1);
            bundle.putString("IPODSERIAL", s2);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        public void onIPodNotification(int i)
        {
            Message message = Message.obtain();
            message.what = 1007;
            Bundle bundle = new Bundle();
            bundle.putInt("EVENTNUMBER", i);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        public void onNowPlayingApplicationNameData(int i, String s)
        {
            Message message = Message.obtain();
            message.what = 1008;
            Bundle bundle = new Bundle();
            bundle.putInt("NOTITYPE", i);
            bundle.putString("BUNDLENAME", s);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        public void onOtgReset()
        {
            Message message = Message.obtain();
            message.what = 1009;
            mHandler.sendMessage(message);
        }

        public void onPlayStatusChanged(int i, int j)
        {
            Message message = Message.obtain();
            message.what = 1002;
            Bundle bundle = new Bundle();
            bundle.putInt("PLAYSTATUSTYPE", i);
            bundle.putInt("PLAYSTATUS", j);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        final IPodManager this$0;

        private IPodListenerWrapper()
        {
            this$0 = IPodManager.this;
            super();
        }

    }

    private final class IPodObserverListenerWrapper extends IIPodObserverListener.Stub
    {

        public void onConnectionChanged(int i, String s, String s1, String s2)
            throws RemoteException
        {
            Message message = Message.obtain();
            message.what = 1000;
            Bundle bundle = new Bundle();
            mIPodConnectionState = i;
            mIPodName = s;
            mIPodModel = s1;
            mIPodSerial = s2;
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        final IPodManager this$0;

        private IPodObserverListenerWrapper()
        {
            this$0 = IPodManager.this;
            super();
        }

    }

    private final class IPodServiceBinderListener
        implements android.os.IBinder.DeathRecipient
    {

        public void binderDied()
        {
            Log.d("IPodManager", "[binderDied] IPOD DAEMON IS DEAD");
            mIBinder.unlinkToDeath(this, 0);
            mHandler.sendEmptyMessageDelayed(1006, 200L);
        }

        private IBinder mIBinder;
        final IPodManager this$0;

        IPodServiceBinderListener(IBinder ibinder)
        {
            this$0 = IPodManager.this;
            super();
            mIBinder = ibinder;
        }
    }


    public IPodManager(Context context)
    {
        this(context, true);
    }

    public IPodManager(Context context, boolean flag)
    {
        mListeners = new ArrayList();
        mWrapperIPodListener = null;
        mWrapperIPodObserverListener = null;
        mIPodConnectionState = -1;
        mSessionIdMap = new HashMap();
        mContext = context;
        mUseObserver = flag;
        mIPodService = getIPodService();
        if(flag)
            mIPodObserverService = getIPodObserverService();
        mHandler = new Handler(context.getMainLooper()) {

            public void handleMessage(Message message)
            {
                message.what;
                JVM INSTR tableswitch 1000 1009: default 60
            //                           1000 61
            //                           1001 151
            //                           1002 307
            //                           1003 397
            //                           1004 487
            //                           1005 562
            //                           1006 652
            //                           1007 848
            //                           1008 923
            //                           1009 1013;
                   goto _L1 _L2 _L3 _L4 _L5 _L6 _L7 _L8 _L9 _L10 _L11
_L1:
                return;
_L2:
                ArrayList arraylist8 = mListeners;
                arraylist8;
                JVM INSTR monitorenter ;
                for(Iterator iterator8 = mListeners.iterator(); iterator8.hasNext(); ((IPodListener)iterator8.next()).onConnectionChanged(mIPodConnectionState, mIPodName, mIPodModel, mIPodSerial));
                  goto _L12
                Exception exception9;
                exception9;
                arraylist8;
                JVM INSTR monitorexit ;
                throw exception9;
_L12:
                arraylist8;
                JVM INSTR monitorexit ;
                return;
_L3:
                int i2;
                int j2;
                int k2;
                int l2;
                int i3;
                String s1;
                String s2;
                String s3;
                Bundle bundle5 = message.getData();
                i2 = bundle5.getInt("LINGOMAJOR");
                j2 = bundle5.getInt("LINGOMINOR");
                k2 = bundle5.getInt("IPODMAJOR");
                l2 = bundle5.getInt("IPODMINOR");
                i3 = bundle5.getInt("IPODREVISION");
                s1 = bundle5.getString("IPODNAME");
                s2 = bundle5.getString("IPODMODEL");
                s3 = bundle5.getString("IPODSERIAL");
                ArrayList arraylist7 = mListeners;
                arraylist7;
                JVM INSTR monitorenter ;
                for(Iterator iterator7 = mListeners.iterator(); iterator7.hasNext(); ((IPodListener)iterator7.next()).onConnectionInfoReceived(i2, j2, k2, l2, i3, s1, s2, s3));
                  goto _L13
                Exception exception8;
                exception8;
                arraylist7;
                JVM INSTR monitorexit ;
                throw exception8;
_L13:
                arraylist7;
                JVM INSTR monitorexit ;
                return;
_L4:
                int k1;
                int l1;
                Bundle bundle4 = message.getData();
                k1 = bundle4.getInt("PLAYSTATUSTYPE");
                l1 = bundle4.getInt("PLAYSTATUS");
                ArrayList arraylist6 = mListeners;
                arraylist6;
                JVM INSTR monitorenter ;
                for(Iterator iterator6 = mListeners.iterator(); iterator6.hasNext(); ((IPodListener)iterator6.next()).onPlayStatusChanged(k1, l1));
                  goto _L14
                Exception exception7;
                exception7;
                arraylist6;
                JVM INSTR monitorexit ;
                throw exception7;
_L14:
                arraylist6;
                JVM INSTR monitorexit ;
                return;
_L5:
                int i1;
                int j1;
                Bundle bundle3 = message.getData();
                i1 = bundle3.getInt("APPID");
                j1 = bundle3.getInt("OPENSESSIONID");
                ArrayList arraylist5 = mListeners;
                arraylist5;
                JVM INSTR monitorenter ;
                for(Iterator iterator5 = mListeners.iterator(); iterator5.hasNext(); ((IPodListener)iterator5.next()).onAppOpenSession(i1, j1));
                  goto _L15
                Exception exception6;
                exception6;
                arraylist5;
                JVM INSTR monitorexit ;
                throw exception6;
_L15:
                arraylist5;
                JVM INSTR monitorexit ;
                return;
_L6:
                int l = message.getData().getInt("CLOSESESSIONID");
                ArrayList arraylist4 = mListeners;
                arraylist4;
                JVM INSTR monitorenter ;
                for(Iterator iterator4 = mListeners.iterator(); iterator4.hasNext(); ((IPodListener)iterator4.next()).onAppCloseSession(l));
                  goto _L16
                Exception exception5;
                exception5;
                arraylist4;
                JVM INSTR monitorexit ;
                throw exception5;
_L16:
                arraylist4;
                JVM INSTR monitorexit ;
                return;
_L7:
                int k;
                byte abyte0[];
                Bundle bundle2 = message.getData();
                k = bundle2.getInt("DATASESSIONID");
                abyte0 = bundle2.getByteArray("DATAARRAY");
                ArrayList arraylist3 = mListeners;
                arraylist3;
                JVM INSTR monitorenter ;
                for(Iterator iterator3 = mListeners.iterator(); iterator3.hasNext(); ((IPodListener)iterator3.next()).onAppDataReceived(k, abyte0));
                  goto _L17
                Exception exception4;
                exception4;
                arraylist3;
                JVM INSTR monitorexit ;
                throw exception4;
_L17:
                arraylist3;
                JVM INSTR monitorexit ;
                return;
_L8:
                IPodManager.sIPodService = null;
                mIPodService = getIPodService();
                if(mIPodService == null)
                {
                    Log.d("IPodManager", "[MESSAGE_IPOD_RECONNECTION]  FAIL TO Connect ----------------------------");
                    sendEmptyMessageDelayed(1006, 200L);
                    return;
                }
                ArrayList arraylist;
                Exception exception;
                Iterator iterator;
                Bundle bundle;
                int i;
                String s;
                ArrayList arraylist1;
                Exception exception1;
                Iterator iterator1;
                int j;
                ArrayList arraylist2;
                Exception exception2;
                Iterator iterator2;
                Message message1;
                Bundle bundle1;
                if(mWrapperIPodListener != null)
                    try
                    {
                        mIPodService.addListener(mWrapperIPodListener);
                    }
                    catch(RemoteException remoteexception) { }
                synchronized(mSessionIdMap)
                {
                    mSessionIdMap.clear();
                }
                message1 = Message.obtain();
                message1.what = 1000;
                bundle1 = new Bundle();
                mIPodConnectionState = 4;
                mIPodName = "unknown";
                mIPodModel = "unknown";
                mIPodSerial = "unknown";
                message1.setData(bundle1);
                mHandler.sendMessage(message1);
                return;
                exception3;
                map;
                JVM INSTR monitorexit ;
                throw exception3;
_L9:
                j = message.getData().getInt("EVENTNUMBER");
                arraylist2 = mListeners;
                arraylist2;
                JVM INSTR monitorenter ;
                for(iterator2 = mListeners.iterator(); iterator2.hasNext(); ((IPodListener)iterator2.next()).onIPodNotification(j));
                  goto _L18
                exception2;
                arraylist2;
                JVM INSTR monitorexit ;
                throw exception2;
_L18:
                arraylist2;
                JVM INSTR monitorexit ;
                return;
_L10:
                bundle = message.getData();
                i = bundle.getInt("NOTITYPE");
                s = bundle.getString("BUNDLENAME");
                arraylist1 = mListeners;
                arraylist1;
                JVM INSTR monitorenter ;
                for(iterator1 = mListeners.iterator(); iterator1.hasNext(); ((IPodListener)iterator1.next()).onNowPlayingApplicationNameData(i, s));
                  goto _L19
                exception1;
                arraylist1;
                JVM INSTR monitorexit ;
                throw exception1;
_L19:
                arraylist1;
                JVM INSTR monitorexit ;
                return;
_L11:
                arraylist = mListeners;
                arraylist;
                JVM INSTR monitorenter ;
                for(iterator = mListeners.iterator(); iterator.hasNext(); ((IPodListener)iterator.next()).onOtgReset());
                  goto _L20
                exception;
                arraylist;
                JVM INSTR monitorexit ;
                throw exception;
_L20:
                arraylist;
                JVM INSTR monitorexit ;
            }

            final IPodManager this$0;

            
            {
                this$0 = IPodManager.this;
                super(looper);
            }
        }
;
    }

    private void ensureAddListeners()
    {
        if(mUseObserver && mWrapperIPodObserverListener == null)
        {
            mWrapperIPodObserverListener = new IPodObserverListenerWrapper();
            RemoteException remoteexception;
            if(mIPodObserverService != null)
                try
                {
                    mIPodObserverService.addListener(mWrapperIPodObserverListener);
                }
                catch(RemoteException remoteexception1) { }
        }
        if(mWrapperIPodListener != null)
            break MISSING_BLOCK_LABEL_87;
        mWrapperIPodListener = new IPodListenerWrapper();
        if(mIPodService == null)
            break MISSING_BLOCK_LABEL_87;
        mIPodService.addListener(mWrapperIPodListener);
        return;
        remoteexception;
    }

    private IIPodObserver getIPodObserverService()
    {
        if(sIPodObserverService == null)
            sIPodObserverService = IIPodObserver.Stub.asInterface(ServiceManager.getService(Service.IPOD_OBSERVER_SERVICE));
        return sIPodObserverService;
    }

    private IIPodService getIPodService()
    {
        if(sIPodService == null)
        {
            IBinder ibinder = ServiceManager.getService(Service.IPOD_SERVICE);
            if(ibinder == null)
                return null;
            IPodServiceBinderListener ipodservicebinderlistener = new IPodServiceBinderListener(ibinder);
            try
            {
                ibinder.linkToDeath(ipodservicebinderlistener, 0);
            }
            catch(RemoteException remoteexception) { }
            sIPodService = IIPodService.Stub.asInterface(ibinder);
        }
        return sIPodService;
    }

    public void addListener(IPodListener ipodlistener)
    {
        if(mWrapperIPodObserverListener != null && mIPodConnectionState != -1)
            ipodlistener.onConnectionChanged(mIPodConnectionState, mIPodName, mIPodModel, mIPodSerial);
        if(mWrapperIPodListener == null) goto _L2; else goto _L1
_L1:
        Map map = mSessionIdMap;
        map;
        JVM INSTR monitorenter ;
        int i;
        int j;
        for(Iterator iterator = mSessionIdMap.entrySet().iterator(); iterator.hasNext(); Log.d("IPodManager", (new StringBuilder()).append("[addListener] call onAppOpenSession appID:").append(i).append(" sessionId:").append(j).toString()))
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            i = ((Integer)entry.getKey()).intValue();
            j = ((Integer)entry.getValue()).intValue();
            ipodlistener.onAppOpenSession(i, j);
        }

        break MISSING_BLOCK_LABEL_175;
        Exception exception1;
        exception1;
        map;
        JVM INSTR monitorexit ;
        throw exception1;
        map;
        JVM INSTR monitorexit ;
_L2:
        ensureAddListeners();
        synchronized(mListeners)
        {
            mListeners.add(ipodlistener);
        }
        return;
        exception;
        arraylist;
        JVM INSTR monitorexit ;
        throw exception;
    }

    public int addTag(int i, int j, int k, String s, String s1, String s2, String s3, 
            int l, int i1, String s4, String s5, String s6, int j1, String s7, 
            int k1, String s8, String s9, byte abyte0[])
    {
        if(mIPodService == null)
            return -40;
        int l1;
        try
        {
            l1 = mIPodService.addTag(i, j, k, s, s1, s2, s3, l, i1, s4, s5, s6, j1, s7, k1, s8, s9, abyte0);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return l1;
    }

    public int appLaunch(int i)
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.appLaunch(i);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public int appSendData(int i, int j, byte abyte0[])
    {
        if(mIPodService == null)
            return -40;
        int k;
        try
        {
            k = mIPodService.appSendData(i, j, abyte0);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return k;
    }

    public int appSendData(int i, byte abyte0[])
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.appSendData(i, abyte0.length, abyte0);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public int cancelCmd(int i, int j)
    {
        if(mIPodService == null)
            return -40;
        int k;
        try
        {
            k = mIPodService.cancelCmd(i, j);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return k;
    }

    public int closeIPodDevice()
    {
        if(mIPodService == null)
            return -40;
        synchronized(mSessionIdMap)
        {
            mSessionIdMap.clear();
        }
        int i;
        try
        {
            i = mIPodService.closeIPodDevice();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
        exception;
        map;
        JVM INSTR monitorexit ;
        throw exception;
    }

    public int closeTagFile(int i)
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.closeTagFile(i);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public int dbGetCount(int i)
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.dbGetCount(i);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public String dbGetEntry(int i, int j)
    {
        if(mIPodService == null)
            return null;
        String s;
        try
        {
            s = mIPodService.dbGetEntry(i, j);
        }
        catch(RemoteException remoteexception)
        {
            return null;
        }
        return s;
    }

    public int dbPlaySelected(int i, int j)
    {
        if(mIPodService == null)
            return -40;
        int k;
        try
        {
            k = mIPodService.dbPlaySelected(i, j);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return k;
    }

    public int dbReset()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.dbReset();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int dbSelection(int i, int j)
    {
        if(mIPodService == null)
            return -40;
        int k;
        try
        {
            k = mIPodService.dbSelection(i, j);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return k;
    }

    public int enterIpodRemoteUIMode()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.enterIpodRemoteUIMode();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int exitIpodRemoteUIMode()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.exitIpodRemoteUIMode();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getArtWorkFormats(ArtWorkFormat aartworkformat[])
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getArtWorkFormats(aartworkformat);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getAudioMute()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getAudioMute();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getColorDisplayImageLimits(DisplayImageLimits displayimagelimits)
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getColorDisplayImageLimits(displayimagelimits);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getCurrentEqIndex()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getCurrentEqIndex();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getCurrentPlayingStatus(CurrentPlayingStatus currentplayingstatus)
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getCurrentPlayingStatus(currentplayingstatus);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public byte[] getIPodSWVersion()
    {
        if(mIPodService == null)
            return null;
        byte abyte0[];
        try
        {
            abyte0 = mIPodService.getIPodSWVersion();
        }
        catch(RemoteException remoteexception)
        {
            return null;
        }
        return abyte0;
    }

    public String getIndexedEqProfileName(int i)
    {
        if(mIPodService == null)
            return null;
        String s;
        try
        {
            s = mIPodService.getIndexedEqProfileName(i);
        }
        catch(RemoteException remoteexception)
        {
            return null;
        }
        return s;
    }

    public int getLingoProtocolVersion(int i, int ai[])
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.getLingoProtocolVersion(i, ai);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public int getMaxNumTags(int i)
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.getMaxNumTags(i);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public String getNowPlayingApplicationID()
    {
        if(mIPodService == null)
            return null;
        String s;
        try
        {
            s = mIPodService.getNowPlayingApplicationID();
        }
        catch(RemoteException remoteexception)
        {
            return null;
        }
        return s;
    }

    public int getNumEqProfiles()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getNumEqProfiles();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getPlayStatus()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getPlayStatus();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getPlayTrackCount()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getPlayTrackCount();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getPlayTrackIndex()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getPlayTrackIndex();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getPlayTrackInfo(int i, int j, StringBuilder stringbuilder)
    {
        if(mIPodService == null)
            return -40;
        String s;
        s = new String(mIPodService.getPlayTrackInfo(i, j));
        if(!s.equals("__ERROR__"))
            break MISSING_BLOCK_LABEL_61;
        stringbuilder.delete(0, stringbuilder.length());
        stringbuilder.append("Unknown");
        return -1;
        try
        {
            stringbuilder.delete(0, stringbuilder.length());
            stringbuilder.append(s);
        }
        catch(RemoteException remoteexception)
        {
            return -1;
        }
        return 0;
    }

    public int getPlayingTrackAlbumArt(int i, byte abyte0[], int j)
    {
        if(mIPodService == null)
            return -40;
        int k;
        try
        {
            k = mIPodService.getPlayingTrackAlbumArt(i, abyte0, j);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return k;
    }

    public int getPlayingTrackChapterCount()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getPlayingTrackChapterCount();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getPlayingTrackElapsedtime()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getPlayingTrackElapsedtime();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getPlayingTrackTotaltime()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getPlayingTrackTotaltime();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getRemoteUIMode()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getRemoteUIMode();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getRepeatSetting()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getRepeatSetting();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getShuffleSetting()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getShuffleSetting();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int getVolume()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.getVolume();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public boolean isAppSupported()
    {
        if(mIPodService == null)
            return false;
        boolean flag;
        try
        {
            flag = mIPodService.isAppSupported();
        }
        catch(RemoteException remoteexception)
        {
            return false;
        }
        return flag;
    }

    public boolean isTaggingSupported()
    {
        if(mIPodService == null)
            return false;
        boolean flag;
        try
        {
            flag = mIPodService.isTaggingSupported();
        }
        catch(RemoteException remoteexception)
        {
            return false;
        }
        return flag;
    }

    public void onModeChanged(int i)
    {
        if(mIPodService == null)
            return;
        try
        {
            mIPodService.onModeChanged(i);
            return;
        }
        catch(RemoteException remoteexception)
        {
            return;
        }
    }

    public int openIPodDevice(ParcelFileDescriptor parcelfiledescriptor, int i, ArrayList arraylist)
    {
        if(mIPodService == null)
            return -40;
        IosAppData aiosappdata[] = (IosAppData[])arraylist.toArray(new IosAppData[arraylist.size()]);
        synchronized(mSessionIdMap)
        {
            mSessionIdMap = new HashMap();
        }
        int j;
        try
        {
            j = mIPodService.openIPodDevice(parcelfiledescriptor, i, aiosappdata);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
        exception;
        map;
        JVM INSTR monitorexit ;
        throw exception;
    }

    public int openTagFile(int i, int j, int k, String s, String s1)
    {
        if(mIPodService == null)
            return -40;
        int l;
        try
        {
            l = mIPodService.openTagFile(i, j, k, s, s1);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return l;
    }

    public int pause()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.pause();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int play()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.play();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int playNextTrack()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.playNextTrack();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int playPrevTrack()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.playPrevTrack();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int playTrack(int i)
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.playTrack(i);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public void removeListener(IPodListener ipodlistener)
    {
label0:
        {
            synchronized(mListeners)
            {
                if(mListeners.indexOf(ipodlistener) >= 0)
                    break label0;
                Log.i("IPodManager", (new StringBuilder()).append("listener(").append(ipodlistener).append(") does not exists.").toString());
            }
            return;
        }
        mListeners.remove(ipodlistener);
        if(mListeners.size() == 0)
            break MISSING_BLOCK_LABEL_80;
        arraylist;
        JVM INSTR monitorexit ;
        return;
        exception;
        arraylist;
        JVM INSTR monitorexit ;
        throw exception;
        boolean flag = mUseObserver;
        if(!flag)
            break MISSING_BLOCK_LABEL_109;
        try
        {
            mIPodObserverService.removeListener(mWrapperIPodObserverListener);
            mWrapperIPodObserverListener = null;
        }
        catch(RemoteException remoteexception1) { }
        try
        {
            mIPodService.removeListener(mWrapperIPodListener);
            mWrapperIPodListener = null;
        }
        catch(RemoteException remoteexception) { }
        arraylist;
        JVM INSTR monitorexit ;
    }

    public int resumeMusicApp()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.resumeMusicApp();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int setAudioMute(int i)
    {
        if(mIPodService == null)
            return -40;
        try
        {
            mIPodService.setAudioMute(i);
        }
        catch(RemoteException remoteexception) { }
        return 0;
    }

    public int setCurrentEqIndex(int i)
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.setCurrentEqIndex(i);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public int setDisplayImage(int i, int j, int k, byte abyte0[])
    {
        if(mIPodService == null)
            return -40;
        int l;
        try
        {
            l = mIPodService.setDisplayImage(i, j, k, abyte0.length, abyte0);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return l;
    }

    public int setPlayPosition(int i)
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.setPlayPosition(i);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public int setRepeatSetting(int i)
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.setRepeatSetting(i);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public int setShuffleSetting(int i)
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.setShuffleSetting(i);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public int setVolume(int i)
    {
        if(mIPodService == null)
            return -40;
        int j;
        try
        {
            j = mIPodService.setVolume(i);
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return j;
    }

    public int skipFwdStart()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.skipFwdStart();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int skipRwdStart()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.skipRwdStart();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int skipStop()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.skipStop();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int stop()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.stop();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    public int togglePause()
    {
        if(mIPodService == null)
            return -40;
        int i;
        try
        {
            i = mIPodService.togglePause();
        }
        catch(RemoteException remoteexception)
        {
            return -40;
        }
        return i;
    }

    private static final int MESSAGE_APP_CLOSE_SESSION = 1004;
    private static final int MESSAGE_APP_DATA_RECEIVED = 1005;
    private static final int MESSAGE_APP_OPEN_SESSION = 1003;
    private static final int MESSAGE_CONNECTION_CHANGED = 1000;
    private static final int MESSAGE_CONNECTION_INFO_RECEIVED = 1001;
    private static final int MESSAGE_IPOD_NOTIFICATION = 1007;
    private static final int MESSAGE_IPOD_RECONNECTION = 1006;
    private static final int MESSAGE_NOW_PLAYING_APP_NAME_DATA = 1008;
    private static final int MESSAGE_OTG_RESET = 1009;
    private static final int MESSAGE_PLAY_STATUS_CHNAGED = 1002;
    private static final String TAG = "IPodManager";
    private static IIPodObserver sIPodObserverService;
    private static IIPodService sIPodService;
    private Context mContext;
    private Handler mHandler;
    private int mIPodConnectionState;
    private String mIPodModel;
    private String mIPodName;
    private IIPodObserver mIPodObserverService;
    private String mIPodSerial;
    private IIPodService mIPodService;
    private ArrayList mListeners;
    private Map mSessionIdMap;
    private boolean mUseObserver;
    private IPodListenerWrapper mWrapperIPodListener;
    private IPodObserverListenerWrapper mWrapperIPodObserverListener;






/*
    static int access$202(IPodManager ipodmanager, int i)
    {
        ipodmanager.mIPodConnectionState = i;
        return i;
    }

*/



/*
    static String access$302(IPodManager ipodmanager, String s)
    {
        ipodmanager.mIPodName = s;
        return s;
    }

*/



/*
    static String access$402(IPodManager ipodmanager, String s)
    {
        ipodmanager.mIPodModel = s;
        return s;
    }

*/



/*
    static String access$502(IPodManager ipodmanager, String s)
    {
        ipodmanager.mIPodSerial = s;
        return s;
    }

*/



/*
    static IIPodService access$702(IIPodService iipodservice)
    {
        sIPodService = iipodservice;
        return iipodservice;
    }

*/



/*
    static IIPodService access$802(IPodManager ipodmanager, IIPodService iipodservice)
    {
        ipodmanager.mIPodService = iipodservice;
        return iipodservice;
    }

*/

}
