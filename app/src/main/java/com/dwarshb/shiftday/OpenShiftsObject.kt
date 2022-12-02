package com.dwarshb.shiftday

data class OpenShiftsObject (
    var id: Int = 0,
    var company: Int = 0,
    var company_name: String = "",
    var employee: Int? = null,
    var employee_name: String? = null,
    var position: String = "",
    var is_open: Boolean,
    var is_dropped: Boolean,
    var date: String,
    var time_start: String,
    var time_end: String
)