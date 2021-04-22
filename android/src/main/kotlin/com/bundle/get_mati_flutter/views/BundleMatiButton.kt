package com.bundle.get_mati_flutter.views

import com.matilock.mati_kyc_sdk.*

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.matilock.mati_kyc_sdk.Metadata.Companion.toJson
import com.matilock.mati_kyc_sdk.kyc.KYCActivity
import com.matilock.mati_kyc_sdk.models.IdToken.Companion.fromBase64
import com.matilock.mati_kyc_sdk.server.RequestManager
import com.matilock.mati_kyc_sdk.server.request.CreateVerificationRequest
import com.matilock.mati_kyc_sdk.server.request.NewAuthorizationTokenRequest
import kotlinx.coroutines.launch

class BundleMatiButton(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    val vm by lazy { MutableLiveData<State>() }

    private val btnTxt = context.obtainStyledAttributes(attrs, R.styleable.MatiLoginButton, 0, 0).let {
        if (it.hasValue(R.styleable.MatiLoginButton_text)) it.getString(R.styleable.MatiLoginButton_text)
                ?: ""
        else getContext().getString(R.string.label_verify_me)
    }

    private lateinit var buttonLogin: String

    init {
        this.addView(LayoutInflater.from(context).inflate(R.layout.frag_login_button, this, false))

        if (context !is AppCompatActivity) throw RuntimeException("Please use Mati button inside AppCompatActivity")


        val progress = findViewById<View>(R.id.progress)
        val matiBtn = findViewById<MatiLoginButton>(R.id.matiBtn)
        val errorTxt = findViewById<TextView>(R.id.errorTxt)
        matiBtn.visibility = View.GONE

        vm.observe(context) {
            when (it) {
                is SuccessState -> {
                    progress.visibility = View.INVISIBLE
                    errorTxt.visibility = View.INVISIBLE
                    matiBtn.visibility = View.INVISIBLE
                    matiBtn.text = buttonLogin

                    val idToken = it.idToken.fromBase64()
                    if (idToken != null) {
                        matiBtn.setBackgroundColor(idToken.flow.color)
                        matiBtn.setTextColor(ContextCompat.getColor(context, if (idToken.flow.isDarkBackgroundColor) R.color.matiPrimaryTextInverse else R.color.matiPrimaryText))
                        context.startActivityForResult(Intent(context, KYCActivity::class.java).apply {
                            putExtra(ARG_ID_TOKEN, it.idToken)
                            putExtra(ARG_VOICE_TXT, it.voiceDataTxt)
                            putExtra(ARG_CLIENT_ID, it.clientId)
                            putExtra(ARG_ACCESS_TOKEN, it.accessToken)
                            putExtra(ARG_VERIFICATION_ID, it.verificationId)
                            it.metadata?.let { data -> putExtra(ARG_METADATA, data.toJson()) }
                        }, KYCActivity.REQUEST_CODE)

                    } else {
                        vm.value = ErrorState(it.clientId, it.metadata, it.flowId, "Unable to parse Id token")
                    }
                }
                is ErrorState -> {
                    progress.visibility = View.INVISIBLE
                    matiBtn.visibility = View.INVISIBLE
                    errorTxt.visibility = View.VISIBLE
                    errorTxt.text = it.error
                    errorTxt.setOnClickListener { _ ->
                        authorise(it.clientId, it.flowId, it.metadata)
                    }
                }
                else -> {
                    progress.visibility = View.VISIBLE
                    matiBtn.visibility = View.INVISIBLE
                    errorTxt.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun setParams(clientId: String, flowId: String?, metadata: Metadata? = null) {
        setParams(clientId, flowId, btnTxt, metadata)
    }

    fun setParams(clientId: String, flowId: String?, buttonLogin: String, metadata: Metadata? = null) {
        this.buttonLogin = buttonLogin
        authorise(clientId, flowId, metadata)
    }

    fun authorise(clientId: String, flowId: String?, metadata: Metadata?) {
        vm.value = LoadingState()

        (context as AppCompatActivity).lifecycleScope.launch {
            RequestManager.instance.baseRequest(NewAuthorizationTokenRequest(clientId, flowId)).let { authReponse ->
                when {
                    authReponse.isSuccess -> {
                        RequestManager.instance.baseRequest(CreateVerificationRequest(authReponse.accessToken, flowId, metadata)).let {
                            vm.value = SuccessState(clientId, metadata, flowId, authReponse.accessToken, authReponse.idToken, it.id, it.identityId, it.voiceDataText)
                        }
                    }

                    else -> vm.value = ErrorState(clientId, metadata, flowId, authReponse.errorMessage
                            ?: context.getString(R.string.label_something_went_wrong))
                }
            }

        }
    }

    abstract class State
    class LoadingState : State()
    data class SuccessState(val clientId: String, val metadata: Metadata?, val flowId: String?, val accessToken: String, val idToken: String, val verificationId: String, val userIdentityId: String, val voiceDataTxt: String?) : State()
    data class ErrorState(val clientId: String, val metadata: Metadata?, val flowId: String?, val error: String) : State()
}