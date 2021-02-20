package com.example.goliathconversion.utils

// A generic class that contains data and status about loading this data.
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data) {
        override fun equals(other: Any?): Boolean {
            if (other == null || other !is Success<*>) {
                return false
            }

            return data == other.data
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class Loading<T>(data: T? = null) : Resource<T>(data) {
        override fun equals(other: Any?): Boolean {
            return other != null && other is Loading<*>
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message) {
        override fun equals(other: Any?): Boolean {
            if (other == null || other !is Error<*>) {
                return false
            }

            return message == other.message
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}
