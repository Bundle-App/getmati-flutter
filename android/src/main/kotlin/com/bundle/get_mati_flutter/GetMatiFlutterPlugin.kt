@file:Suppress("UNCHECKED_CAST")

package com.bundle.get_mati_flutter

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import com.bundle.get_mati_flutter.models.BundleData
import com.bundle.get_mati_flutter.models.INTENT_PARAMS_KEY

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

/** GetMatiFlutterPlugin */
class GetMatiFlutterPlugin : FlutterPlugin, ActivityAware {
    private lateinit var channel: MethodChannel
    lateinit var outerResult: Result


    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "get_mati_flutter")
            if (registrar.activity() == null) {
                return
            }

            channel.setMethodCallHandler { call, result ->
                GetMatiFlutterPlugin().onMethodCall(registrar.activity(), call, result)
            }
        }
    }

    fun onMethodCall(activity: Activity, @NonNull call: MethodCall, @NonNull result: Result) {
        outerResult = result
        if (call.method == "startVerification") {
            val arguments = call.argument<HashMap<String, Any>>("body")!!

            val clientId = if (arguments.containsKey("clientId")) {
                arguments["clientId"].toString()
            } else {
                ""
            }

            val flowId = if (arguments.containsKey("flowId")) {
                arguments["flowId"].toString()
            } else {
                null
            }

            val buttonTitle = if (arguments.containsKey("buttonTitle")) {
                arguments["buttonTitle"].toString()
            } else {
                null
            }

            val metadata = if (arguments.containsKey("metadata")) {
                arguments["metadata"] as HashMap<String, *>
            } else {
                null
            }
            val bundleData = BundleData(clientId, flowId, buttonTitle, metadata);
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(INTENT_PARAMS_KEY, bundleData)
            activity.startActivityForResult(intent, 404)
            MainActivity.setFlutterResult(outerResult)

        } else {
            result.notImplemented()
        }
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "get_mati_flutter")
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {}

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        channel.setMethodCallHandler { call, result ->
            onMethodCall(binding.activity, call, result)
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {}

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

    override fun onDetachedFromActivity() {}
}
