package com.app.azovatask.model

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

open class DoctorDetailsRealmModel(
    @PrimaryKey
    var id: Int?,
    var docName: String?,
    var specialization: String?,

    @Ignore
    var checkedStatus: Boolean
) : RealmObject() {
    constructor() : this(null, "", "", false)
}