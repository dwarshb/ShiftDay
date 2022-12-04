package com.dwarshb.shiftday

data class PendingShiftData(var id: String?="", var user : User?=null, val date: String?="", val startTime: Double?=0.0, val endTime: Double?=0.0,
                            var isApproved : Boolean?=true, var isDenied: Boolean?=true,
                            var isDroppedBy : Boolean? = true,var shiftLocation : String? = "") {
}