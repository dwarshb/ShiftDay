package com.dwarshb.shiftday

data class User(var id: String?="",var name: String?="",var userType : String?="",
                var hoursWorkedAWeek: Int =0,var totalHours:Int = 0,var totalAmount:Int = 0)