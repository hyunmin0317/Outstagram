package com.example.outstagram

import java.io.Serializable

class Profile (
    val id : Int? =null,
    val owner : String? = null,
    var image : String? = null
): Serializable