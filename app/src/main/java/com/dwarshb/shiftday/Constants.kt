package com.dwarshb.shiftday

class Constants {
    object Shift {
        const val APPROVE:String  = "Approve"
        const val DENY:String = "Deny"
    }

    object Database {
        const val Shifts: String = "Shifts"
        const val RequestShifts: String = "RequestShifts"
        const val Users: String = "Users"
    }
    object User {
        const val Name = "name"
        const val Id = "id"
        const val Type = "userType"
        const val STUDENT = "student"
        const val MANAGER = "manager"
    }
}