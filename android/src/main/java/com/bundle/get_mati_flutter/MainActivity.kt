package com.bundle.get_mati_flutter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bundle.get_mati_flutter.models.BundleData
import com.bundle.get_mati_flutter.models.INTENT_PARAMS_KEY
import com.matilock.mati_kyc_sdk.ARG_VERIFICATION_ID
import com.matilock.mati_kyc_sdk.MatiButton
import com.matilock.mati_kyc_sdk.kyc.KYCActivity
import io.flutter.plugin.common.MethodChannel


class MainActivity : AppCompatActivity() { 
    private var resultMap = HashMap<String, String>()
    private var hasSubmittedResponse = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bundleData: BundleData? = intent.getParcelableExtra(INTENT_PARAMS_KEY)

        setUpButton(bundleData!!)

    }
    
    private fun setUpButton(bundleData: BundleData) {
        val clientId: String = bundleData.clientId
        val flowId: String? = bundleData.flowId
        val buttonTitle: String = if (bundleData.buttonTitle == null) "Start" else bundleData.buttonTitle!!
        val metadata: HashMap<String, *>? = bundleData.metadata
        val metaBuilder = com.matilock.mati_kyc_sdk.Metadata.Builder();

        if (metadata != null) {
            for (key in metadata.keys) {
                metaBuilder.with(key, metadata[key]!!)
            }
        }
        findViewById<MatiButton>(R.id.matiKYCButton).setParams(
                clientId,
                flowId,
                buttonTitle,
                metaBuilder.build())
        findViewById<MatiButton>(R.id.matiKYCButton).performClick()
    }

    companion object {
        private var flutterChannelResult: MethodChannel.Result? = null

        @JvmStatic
        fun setFlutterResult(methodResult: MethodChannel.Result?) {
            flutterChannelResult = methodResult
        }
    }

    private fun returnSuccess(verificationId: String) {
        if(!hasSubmittedResponse) {
            resultMap["status"] = "success"
            resultMap["verificationId"] = verificationId
            flutterChannelResult?.success(resultMap)
            Log.i("returnSuccess", "returnSuccess")
            hasSubmittedResponse = true
            finish()
        }
    }
    
    private fun returnFailure() {
        if(!hasSubmittedResponse) {
            resultMap.clear()
            resultMap["status"] = "failure"
            flutterChannelResult?.success(resultMap)
            Log.i("returnFailure", "returnFailure")
            hasSubmittedResponse = true
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == KYCActivity.REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                returnSuccess(data?.getStringExtra(ARG_VERIFICATION_ID)!!)
            } else {
                returnFailure()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(resultMap.isEmpty()) {
            returnFailure()
        }
    }
}
