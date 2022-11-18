package com.example.aop.part2.mask.data

class User {
    var email: String
    var beginner: Int = 0
    var intermediate: Int = 0
    var advanced: Int = 0
    var attendanced: Int = 0
    var index = ArrayList<Int>(10)

    constructor(email: String) {
        this.email = email
    }
    //getter, setter 설정

}