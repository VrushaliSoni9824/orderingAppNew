package com.tjcg.menuo.utils

class Resource<T>(val status: Status, val data: T?, val message: String?) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {
        @JvmStatic
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        @JvmStatic
        fun <T> error(msg: String, data: T?): Resource<T?> {
            return Resource(Status.ERROR, data, msg)
        }

        @JvmStatic
        fun <T> loading(data: T?): Resource<T?> {
            return Resource(Status.LOADING, data, null)
        }
    }
}