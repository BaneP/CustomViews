package com.example.epg_try.dtv;

import android.net.ParseException;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.dtv.dtvmanager.DTVManager;
import com.iwedia.dtv.dtvmanager.IDTVManager;
import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.epg.EpgServiceFilter;
import com.iwedia.dtv.epg.EpgTimeFilter;
import com.iwedia.dtv.route.broadcast.IBroadcastRouteControl;
import com.iwedia.dtv.route.broadcast.RouteDemuxDescriptor;
import com.iwedia.dtv.route.broadcast.RouteFrontendDescriptor;
import com.iwedia.dtv.route.broadcast.RouteFrontendType;
import com.iwedia.dtv.route.broadcast.RouteMassStorageDescriptor;
import com.iwedia.dtv.route.common.ICommonRouteControl;
import com.iwedia.dtv.route.common.RouteDecoderDescriptor;
import com.iwedia.dtv.route.common.RouteInputOutputDescriptor;
import com.iwedia.dtv.service.IServiceControl;
import com.iwedia.dtv.service.ServiceDescriptor;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.dtv.types.TimeDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;

public class DvbManager {
    private static final String TAG = "DVB MANAGER";
    private IDTVManager mDTVManager = null;
    /** DVB Manager Instance. */
    private static DvbManager sInstance = null;
    /** EPG Filter ID */
    private int mEPGFilterID = -1;
    private EpgCallback mEPGCallBack;
    /**
     * Routes
     */
    private int mCurrentLiveRoute = -1;
    private int mLiveRouteSat = -1;
    private int mLiveRouteTer = -1;
    private int mLiveRouteCab = -1;
    private int mLiveRouteIp = -1;
    private int mPlaybackRouteIDMain = -1;
    private int mRecordRouteTer = -1;
    private int mRecordRouteCab = -1;
    private int mRecordRouteSat = -1;
    private int mRecordRouteIp = -1;
    private int mCurrentRecordRoute = -1;
    /** Currently active list in Comedia. */
    private int mCurrentListIndex = 0;

    public static DvbManager getInstance() {
        if (sInstance == null) {
            sInstance = new DvbManager();
        }
        return sInstance;
    }

    private DvbManager() {
        mDTVManager = new DTVManager();
        try {
            initializeDTVService();
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize Service.
     * 
     * @throws InternalException
     */
    public void initializeDTVService() throws InternalException {
        initializeRouteId();
        mEPGFilterID = mDTVManager.getEpgControl().createEventList();
        mEPGCallBack = new EpgCallback();
        mDTVManager.getEpgControl()
                .registerCallback(mEPGCallBack, mEPGFilterID);
    }

    /**
     * Initialize Descriptors For Live Route.
     */
    private void initializeRouteId() {
        IBroadcastRouteControl broadcastRouteControl = mDTVManager
                .getBroadcastRouteControl();
        ICommonRouteControl commonRouteControl = mDTVManager
                .getCommonRouteControl();
        /**
         * RETRIEVE DEMUX DESCRIPTOR.
         */
        RouteDemuxDescriptor demuxDescriptor = broadcastRouteControl
                .getDemuxDescriptor(0);
        /**
         * RETRIEVE DECODER DESCRIPTOR.
         */
        RouteDecoderDescriptor decoderDescriptor = commonRouteControl
                .getDecoderDescriptor(0);
        /**
         * RETRIEVING OUTPUT DESCRIPTOR.
         */
        RouteInputOutputDescriptor outputDescriptor = commonRouteControl
                .getInputOutputDescriptor(0);
        /**
         * RETRIEVING MASS STORAGE DESCRIPTOR.
         */
        RouteMassStorageDescriptor massStorageDescriptor = new RouteMassStorageDescriptor();
        massStorageDescriptor = broadcastRouteControl
                .getMassStorageDescriptor(0);
        /**
         * GET NUMBER OF FRONTENDS.
         */
        int numberOfFrontends = broadcastRouteControl.getFrontendNumber();
        /**
         * FIND DVB and IP front-end descriptors.
         */
        EnumSet<RouteFrontendType> frontendTypes = null;
        for (int i = 0; i < numberOfFrontends; i++) {
            RouteFrontendDescriptor frontendDescriptor = broadcastRouteControl
                    .getFrontendDescriptor(i);
            frontendTypes = frontendDescriptor.getFrontendType();
            for (RouteFrontendType frontendType : frontendTypes) {
                switch (frontendType) {
                    case SAT: {
                        if (mLiveRouteSat == -1) {
                            mLiveRouteSat = getLiveRouteId(frontendDescriptor,
                                    demuxDescriptor, decoderDescriptor,
                                    outputDescriptor, broadcastRouteControl);
                        }
                        /**
                         * RETRIEVE RECORD ROUTES
                         */
                        if (mRecordRouteSat == -1) {
                            mRecordRouteSat = broadcastRouteControl
                                    .getRecordRoute(frontendDescriptor
                                            .getFrontendId(), demuxDescriptor
                                            .getDemuxId(),
                                            massStorageDescriptor
                                                    .getMassStorageId());
                        }
                        break;
                    }
                    case CAB: {
                        if (mLiveRouteCab == -1) {
                            mLiveRouteCab = getLiveRouteId(frontendDescriptor,
                                    demuxDescriptor, decoderDescriptor,
                                    outputDescriptor, broadcastRouteControl);
                        }
                        /**
                         * RETRIEVE RECORD ROUTES
                         */
                        if (mRecordRouteCab == -1) {
                            mRecordRouteCab = broadcastRouteControl
                                    .getRecordRoute(frontendDescriptor
                                            .getFrontendId(), demuxDescriptor
                                            .getDemuxId(),
                                            massStorageDescriptor
                                                    .getMassStorageId());
                        }
                        break;
                    }
                    case TER: {
                        if (mLiveRouteTer == -1) {
                            mLiveRouteTer = getLiveRouteId(frontendDescriptor,
                                    demuxDescriptor, decoderDescriptor,
                                    outputDescriptor, broadcastRouteControl);
                        }
                        /**
                         * RETRIEVE RECORD ROUTES
                         */
                        if (mRecordRouteTer == -1) {
                            mRecordRouteTer = broadcastRouteControl
                                    .getRecordRoute(frontendDescriptor
                                            .getFrontendId(), demuxDescriptor
                                            .getDemuxId(),
                                            massStorageDescriptor
                                                    .getMassStorageId());
                        }
                        break;
                    }
                    case IP: {
                        if (mLiveRouteIp == -1) {
                            mLiveRouteIp = getLiveRouteId(frontendDescriptor,
                                    demuxDescriptor, decoderDescriptor,
                                    outputDescriptor, broadcastRouteControl);
                        }
                        /**
                         * RETRIEVE RECORD ROUTES
                         */
                        if (mRecordRouteIp == -1) {
                            mRecordRouteIp = broadcastRouteControl
                                    .getRecordRoute(frontendDescriptor
                                            .getFrontendId(), demuxDescriptor
                                            .getDemuxId(),
                                            massStorageDescriptor
                                                    .getMassStorageId());
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        /**
         * RETRIEVE PLAYBACK ROUTE
         */
        mPlaybackRouteIDMain = broadcastRouteControl.getPlaybackRoute(
                massStorageDescriptor.getMassStorageId(),
                demuxDescriptor.getDemuxId(), decoderDescriptor.getDecoderId());
        if (mLiveRouteIp != -1
                && (mLiveRouteCab != -1 || mLiveRouteSat != -1 || mLiveRouteTer != -1)) {
            // ipAndSomeOtherTunerType = true;
        }
        Log.d(TAG, "mLiveRouteTer=" + mLiveRouteTer + ", mLiveRouteCab="
                + mLiveRouteCab + ", mLiveRouteIp=" + mLiveRouteIp);
    }

    /**
     * Get Live Route From Descriptors.
     * 
     * @param fDescriptor
     * @param mDemuxDescriptor
     * @param mDecoderDescriptor
     * @param mOutputDescriptor
     */
    private int getLiveRouteId(RouteFrontendDescriptor fDescriptor,
            RouteDemuxDescriptor mDemuxDescriptor,
            RouteDecoderDescriptor mDecoderDescriptor,
            RouteInputOutputDescriptor mOutputDescriptor,
            IBroadcastRouteControl routeControl) {
        return routeControl.getLiveRoute(fDescriptor.getFrontendId(),
                mDemuxDescriptor.getDemuxId(),
                mDecoderDescriptor.getDecoderId());
    }

    /**
     * Stop MW video playback.
     * 
     * @throws InternalException
     */
    public void stopDTV() throws InternalException {
        mDTVManager.getEpgControl().releaseEventList(mEPGFilterID);
        mDTVManager.getEpgControl().unregisterCallback(mEPGCallBack,
                mEPGFilterID);
        mDTVManager.getServiceControl().stopService(mCurrentLiveRoute);
        sInstance = null;
    }

    /**
     * Change Channel by Number.
     * 
     * @return Channel Info Object or null if error occurred.
     * @throws IllegalArgumentException
     * @throws InternalException
     */
    public void changeChannelByNumber(int channelNumber)
            throws InternalException {
        int listSize = getChannelListSize();
        if (listSize == 0) {
            return;
        }
        channelNumber = (channelNumber + listSize) % listSize;
        ServiceDescriptor desiredService = mDTVManager.getServiceControl()
                .getServiceDescriptor(mCurrentListIndex, channelNumber);
        int route = getActiveRouteByServiceType(desiredService.getSourceType());
        if (route == -1) {
            return;
        }
        mCurrentLiveRoute = route;
        mDTVManager.getServiceControl().startService(route, mCurrentListIndex,
                channelNumber);
    }

    /**
     * Return route by service type.
     * 
     * @param serviceType
     *        Service type to check.
     * @return Desired route, or 0 if service type is undefined.
     */
    private int getActiveRouteByServiceType(SourceType sourceType) {
        switch (sourceType) {
            case CAB: {
                return mLiveRouteCab;
            }
            case TER: {
                return mLiveRouteTer;
            }
            case SAT: {
                return mLiveRouteSat;
            }
            case IP: {
                return mLiveRouteIp;
            }
            default:
                return -1;
        }
    }

    /**
     * Get Size of Channel List.
     */
    public int getChannelListSize() {
        int serviceCount = mDTVManager.getServiceControl().getServiceListCount(
                mCurrentListIndex);
        return serviceCount;
    }

    /**
     * Get Channel Names.
     */
    public ArrayList<String> getChannelNames() {
        ArrayList<String> channelNames = new ArrayList<String>();
        String channelName = "";
        int channelListSize = getChannelListSize();
        IServiceControl serviceControl = mDTVManager.getServiceControl();
        for (int i = 0; i < channelListSize; i++) {
            channelName = serviceControl.getServiceDescriptor(
                    mCurrentListIndex, i).getName();
            channelNames.add(channelName);
        }
        return channelNames;
    }

    /**
     * Get Current Channel Number.
     */
    public int getCurrentChannelNumber() {
        return (int) (mDTVManager.getServiceControl().getActiveService(
                mCurrentLiveRoute).getServiceIndex());
    }

    public TimeDate getTimeFromStream() {
        return mDTVManager.getSetupControl().getTimeDate();
    }

    /**
     * Load Events From MW.
     * 
     * @param day
     *        -Load EPG for previous or current or next day.
     * @param channelListSize
     *        Number of services in channel list
     * @throws ParseException
     * @throws RemoteException
     */
    public synchronized ArrayList<EpgEvent> loadEvents(int channelNumber,
            int oneMinutePixelWidth, int day) throws ParseException,
            IllegalArgumentException {
        int count = getChannelListSize();
        if (channelNumber < 0 || channelNumber >= count) {
            throw new IllegalArgumentException("Channel number cant be: "
                    + channelNumber + ", service list size is: " + count);
        }
        ArrayList<EpgEvent> events = new ArrayList<EpgEvent>();
        EpgEvent lEvent = null;
        int lEpgEventsSize = 0;
        TimeDate lCurrentTime = mDTVManager.getSetupControl().getTimeDate();
        Calendar lCalendar = lCurrentTime.getCalendar();
        lCalendar.add(Calendar.DATE, day);
        TimeDate lEpgStartTime = new TimeDate(1, 1, 0,
                lCalendar.get(Calendar.DAY_OF_MONTH),
                lCalendar.get(Calendar.MONTH) + 1, lCalendar.get(Calendar.YEAR));
        TimeDate lEpgEndTime = new TimeDate(0, 0, 0,
                lCalendar.get(Calendar.DAY_OF_MONTH),
                lCalendar.get(Calendar.MONTH) + 1, lCalendar.get(Calendar.YEAR));
        /** Create Time Filter */
        EpgTimeFilter lEpgTimeFilter = new EpgTimeFilter();
        lEpgTimeFilter.setTime(lEpgStartTime, lEpgEndTime);
        /** Make filter list by time. */
        mDTVManager.getEpgControl().setFilter(mEPGFilterID, lEpgTimeFilter);
        int indexInMasterList = mDTVManager.getServiceControl()
                .getServiceDescriptor(mCurrentListIndex, channelNumber)
                .getMasterIndex();
        /** Create Service Filter. */
        EpgServiceFilter lEpgServiceFilter = new EpgServiceFilter();
        lEpgServiceFilter.setServiceIndex(indexInMasterList);
        /** Set Service Filter. */
        mDTVManager.getEpgControl().setFilter(mEPGFilterID, lEpgServiceFilter);
        mDTVManager.getEpgControl().startAcquisition(mEPGFilterID);
        lEpgEventsSize = mDTVManager.getEpgControl().getAvailableEventsNumber(
                mEPGFilterID, indexInMasterList);
        for (int eventIndex = 0; eventIndex < lEpgEventsSize; eventIndex++) {
            lEvent = mDTVManager.getEpgControl().getRequestedEvent(
                    mEPGFilterID, indexInMasterList, eventIndex);
            events.add(lEvent);
        }
        mDTVManager.getEpgControl().stopAcquisition(mEPGFilterID);
        return events;
    }
}
