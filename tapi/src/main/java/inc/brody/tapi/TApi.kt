package inc.brody.tapi

import android.content.Context
import android.util.Log
import inc.brody.tapi.data.State
import inc.brody.tapi.data.appdata.TConstants
import inc.brody.tapi.requests.TCheckAuthenticationCode
import inc.brody.tapi.requests.TCheckAuthenticationPassword
import inc.brody.tapi.requests.TGetCurrentState
import inc.brody.tapi.requests.TSetAuthenticationPhoneNumber
import inc.brody.tapi.utils.Session
import inc.brody.tapi.utils.TelegramAPIController
import org.drinkless.td.libcore.telegram.TdApi

class TApi {

    private val TAG = "TApi"

    companion object {
        private var instance: TApi? = null

        @JvmStatic
        fun init(context: Context): TApi =
            instance ?: synchronized(this) {
                instance = TApi()
                TelegramAPIController.init()
                Session.init(context)
                instance!!.initInternalProcesses()
                return instance!!
            }
    }

    private var _authState: Int = -1
        set(value) {
            stateInstance.value = value
            stateInstance.listener?.onChange()
            field = value
        }

    /*private var _signedState: Int = -1
        set(value) {
            signedInstance.value = value
            signedInstance.listener?.onChange()
            field = value
        }

    private val signedInstance = State()


    val signedState: State
        get() = signedInstance*/

    private val stateInstance = State()

    val authState: State
        get() = stateInstance


    fun sendCodeOnPhone(phone: String) {
        Session.myPhone = phone
        TSetAuthenticationPhoneNumber {}
    }

    fun loginWithPassword(password: String) {
        Session.myPassword = password
        TCheckAuthenticationPassword {}
    }

    fun loginWithReceivedCode(code: String) {
        Session.myCode = code
        TCheckAuthenticationCode {
//            Log.d("TApi", it.toString() + "Hurray")
        }
    }

    fun logout() = Session.confirmLogout()

    fun initInternalProcesses() {
        TGetCurrentState (
            onError = {
                authState.error = it
                _authState = TConstants.AUTH_ERROR
        },
            onSuccess = {
                when (it) {
//                    is TdApi.Updates ->
//                        _authState = Session.checkAuthState(
//                            (it.updates[0]) as TdApi.UpdateAuthorizationState
//                        ) //todo
                    is TdApi.UpdateAuthorizationState -> {
                        _authState = Session.checkAuthState(it)
                    }
                    else -> {
                        // Log.d("TApiTEST", it.toString())
                    }
                }
            })
    }
}