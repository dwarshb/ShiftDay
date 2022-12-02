package com.dwarshb.shiftday

data class ShiftData(var id: String?="",var user : User?=null, val date: String?="", val startTime: Double?=0.0, val endTime: Double?=0.0,
                     var isAvailable : Boolean?=true)