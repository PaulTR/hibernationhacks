package com.ptruiz.test_face

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Vibrator
import com.ford.syncV4.exception.SyncException
import com.ford.syncV4.exception.SyncExceptionCause
import com.ford.syncV4.proxy.SyncProxyALM
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM
import com.ford.syncV4.proxy.rpc.*
import com.ford.syncV4.proxy.rpc.enums.*

class AppLinkService : Service(), IProxyListenerALM {

    var proxy: SyncProxyALM? = null
        private set
    private var mCorrelationId = 0
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent != null && BluetoothAdapter.getDefaultAdapter() != null &&
            BluetoothAdapter.getDefaultAdapter().isEnabled
        ) {
            startProxy()
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        removeSyncProxy()
        instance = null
        super.onDestroy()
    }

    private fun removeSyncProxy() {
        if (proxy == null) return
        try {
            proxy!!.dispose()
        } catch (e: SyncException) {
        }
        proxy = null
    }

    fun startProxy() {
        if (proxy == null) {
            try {
                proxy = SyncProxyALM(
                    this,
                    "Display Title",
                    true,
                    "App Link ID"
                )
            } catch (e: SyncException) {
                if (proxy == null) {
                    stopSelf()
                }
            }
        }
    }

    fun reset() {
        if (proxy != null) {
            try {
                proxy!!.resetProxy()
            } catch (e: SyncException) {
                if (proxy == null) stopSelf()
            }
        } else {
            startProxy()
        }
    }

    override fun onOnHMIStatus(onHMIStatus: OnHMIStatus) {
        when (onHMIStatus.systemContext) {
            SystemContext.SYSCTXT_MAIN, SystemContext.SYSCTXT_VRSESSION, SystemContext.SYSCTXT_MENU -> {
            }
            else -> return
        }
        when (onHMIStatus.audioStreamingState) {
            AudioStreamingState.AUDIBLE -> {

            }
            AudioStreamingState.NOT_AUDIBLE -> {

            }
        }
        if (proxy == null) return
        if (onHMIStatus.hmiLevel == HMILevel.HMI_FULL && onHMIStatus.firstRun) {
            //setup app with SYNC
            try {
                proxy!!.show(
                    "Drive safe!",
                    "HibernationHacks",
                    TextAlignment.CENTERED,
                    mCorrelationId++
                )
            } catch (e: SyncException) {
            }
            subscribeToButtons()
        }
    }

    private fun subscribeToButtons() {
        if (proxy == null) return
        try {
            proxy!!.subscribeButton(ButtonName.OK, mCorrelationId++)
        } catch (e: SyncException) {
        }
    }

    override fun onOnButtonPress(notification: OnButtonPress) {
        if (ButtonName.OK == notification.buttonName) {
            //start vibrating
            val vibrator = applicationContext?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(0, 2000.toLong())
            vibrator.vibrate(pattern, -1)
        }
    }

    override fun onProxyClosed(s: String, e: Exception) {
        val syncException = e as SyncException
        if (syncException.syncExceptionCause != SyncExceptionCause.SYNC_PROXY_CYCLED &&
            syncException.syncExceptionCause != SyncExceptionCause.BLUETOOTH_DISABLED
        ) {
            reset()
        }
    }

    override fun onError(s: String, e: Exception) {}
    override fun onGenericResponse(genericResponse: GenericResponse) {}
    override fun onOnCommand(onCommand: OnCommand) {}
    override fun onAddCommandResponse(addCommandResponse: AddCommandResponse) {}
    override fun onAddSubMenuResponse(addSubMenuResponse: AddSubMenuResponse) {}
    override fun onCreateInteractionChoiceSetResponse(createInteractionChoiceSetResponse: CreateInteractionChoiceSetResponse) {}
    override fun onAlertResponse(alertResponse: AlertResponse) {}
    override fun onDeleteCommandResponse(deleteCommandResponse: DeleteCommandResponse) {}
    override fun onDeleteInteractionChoiceSetResponse(deleteInteractionChoiceSetResponse: DeleteInteractionChoiceSetResponse) {}
    override fun onDeleteSubMenuResponse(deleteSubMenuResponse: DeleteSubMenuResponse) {}
    override fun onPerformInteractionResponse(performInteractionResponse: PerformInteractionResponse) {}
    override fun onResetGlobalPropertiesResponse(resetGlobalPropertiesResponse: ResetGlobalPropertiesResponse) {}
    override fun onSetGlobalPropertiesResponse(setGlobalPropertiesResponse: SetGlobalPropertiesResponse) {}
    override fun onSetMediaClockTimerResponse(setMediaClockTimerResponse: SetMediaClockTimerResponse) {}
    override fun onShowResponse(showResponse: ShowResponse) {}
    override fun onSpeakResponse(speakResponse: SpeakResponse) {}
    override fun onOnButtonEvent(onButtonEvent: OnButtonEvent) {}
    override fun onSubscribeButtonResponse(subscribeButtonResponse: SubscribeButtonResponse) {}
    override fun onUnsubscribeButtonResponse(unsubscribeButtonResponse: UnsubscribeButtonResponse) {}
    override fun onOnPermissionsChange(onPermissionsChange: OnPermissionsChange) {}
    override fun onSubscribeVehicleDataResponse(subscribeVehicleDataResponse: SubscribeVehicleDataResponse) {}
    override fun onUnsubscribeVehicleDataResponse(unsubscribeVehicleDataResponse: UnsubscribeVehicleDataResponse) {}
    override fun onGetVehicleDataResponse(getVehicleDataResponse: GetVehicleDataResponse) {}
    override fun onReadDIDResponse(readDIDResponse: ReadDIDResponse) {}
    override fun onGetDTCsResponse(getDTCsResponse: GetDTCsResponse) {}
    override fun onOnVehicleData(onVehicleData: OnVehicleData) {}
    override fun onPerformAudioPassThruResponse(performAudioPassThruResponse: PerformAudioPassThruResponse) {}
    override fun onEndAudioPassThruResponse(endAudioPassThruResponse: EndAudioPassThruResponse) {}
    override fun onOnAudioPassThru(onAudioPassThru: OnAudioPassThru) {}
    override fun onPutFileResponse(putFileResponse: PutFileResponse) {}
    override fun onDeleteFileResponse(deleteFileResponse: DeleteFileResponse) {}
    override fun onListFilesResponse(listFilesResponse: ListFilesResponse) {}
    override fun onSetAppIconResponse(setAppIconResponse: SetAppIconResponse) {}
    override fun onScrollableMessageResponse(scrollableMessageResponse: ScrollableMessageResponse) {}
    override fun onChangeRegistrationResponse(changeRegistrationResponse: ChangeRegistrationResponse) {}
    override fun onSetDisplayLayoutResponse(setDisplayLayoutResponse: SetDisplayLayoutResponse) {}
    override fun onOnLanguageChange(onLanguageChange: OnLanguageChange) {}
    override fun onSliderResponse(sliderResponse: SliderResponse) {}
    override fun onOnDriverDistraction(onDriverDistraction: OnDriverDistraction) {}
    override fun onEncodedSyncPDataResponse(encodedSyncPDataResponse: EncodedSyncPDataResponse) {}
    override fun onSyncPDataResponse(syncPDataResponse: SyncPDataResponse) {}
    override fun onOnEncodedSyncPData(onEncodedSyncPData: OnEncodedSyncPData) {}
    override fun onOnSyncPData(onSyncPData: OnSyncPData) {}
    override fun onOnTBTClientState(onTBTClientState: OnTBTClientState) {}
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        var instance: AppLinkService? = null
            private set
    }
}