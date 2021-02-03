package com.example.mytheater.myApp.objects

/**
 * Holkds datas for one theater
 *
 * @author neamar
 */
class Theater {
    @JvmField
    var code: String? = null
    @JvmField
    var title: String? = null
    @JvmField
    var location: String? = null
    @JvmField
    var zipCode: String? = null
    var city: String? = null
    var distance = -1.0
    override fun toString(): String {
        return title!!
    }
}