package com.welie.btserver

import java.util.*

data class SimpleNumericObservation2(
        val id : Short,
        val type : ObservationType,
        val value : Float,
        val unit : Unit,
        val timestamp : Date

)