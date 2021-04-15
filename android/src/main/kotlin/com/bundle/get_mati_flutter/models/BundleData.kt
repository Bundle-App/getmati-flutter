@file:Suppress("UNCHECKED_CAST")

package com.bundle.get_mati_flutter.models

import android.os.Parcel
import android.os.Parcelable

const val INTENT_PARAMS_KEY = "INTENT_PARAMS_KEY"


open class BundleData : Parcelable {
    var clientId: String
    var flowId: String?
    var buttonTitle: String?
    var metadata: HashMap<String, *>?

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<BundleData> {
            override fun createFromParcel(parcel: Parcel) = BundleData(parcel)
            override fun newArray(size: Int) = arrayOfNulls<BundleData>(size)
        }
    }

    constructor(clientId: String,
                flowId: String?,
                buttonTitle: String?,
                metadata: HashMap<String, *>?) {
        this.clientId = clientId
        this.flowId = flowId
        this.buttonTitle = buttonTitle
        this.metadata = metadata
    }

    protected constructor(`in`: Parcel) {
        clientId = `in`.readString()!!
        flowId = `in`.readString()
        buttonTitle = `in`.readString()
        metadata = `in`.readHashMap(HashMap::class.java.classLoader) as HashMap<String, *>?
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(clientId)
        dest.writeString(flowId)
        dest.writeString(buttonTitle)
        dest.writeMap(metadata)
    }

    override fun describeContents(): Int {
        return 0
    }
}