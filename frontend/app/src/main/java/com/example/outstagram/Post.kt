package com.example.outstagram

import java.io.Serializable

class Post(
    val id : Int? =null,
    val owner : String? = null,
    var content : String? = null,
    var image : String? = null
):Serializable