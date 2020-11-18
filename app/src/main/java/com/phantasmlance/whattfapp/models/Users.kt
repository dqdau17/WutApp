package com.phantasmlance.whattfapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Users(val uid: String, val username: String, val profileImageUrl: String) : Parcelable {
    constructor() : this("", "", "")
}