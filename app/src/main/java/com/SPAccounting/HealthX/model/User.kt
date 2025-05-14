package com.SPAccounting.HealthX.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var Name: String? = null,
    var Email: String? = null,
    var Phone: String? = null,
    var UID: String? = null,
    var isDoctor: String = Doctor.IS_NOT_DOCTOR.toItemString(),
    var Age: Int = 0,
    var Gender: String? = null,
    var Address: String? = null,
    var Specialist: String? = null,
    var Stats: String? = "0:0:0:0:0?0:0:0:0:0?0:0:0:0:0?0:0:0:0:0",
    var Prescription: String? = null,
    var totalRating: Float = 5F,
    var ratings: List<Rating> = emptyList()
) : Parcelable

enum class Doctor {
    IS_DOCTOR,
    IS_NOT_DOCTOR;

    companion object {
        fun isDoctor(isDoctor: String): Doctor {
            return if (isDoctor == IS_DOCTOR.toDisplayString())
                IS_DOCTOR
            else
                IS_NOT_DOCTOR
        }

        fun isDoctorString(isDoctor: String): String {
            return if (isDoctor == IS_DOCTOR.toDisplayString())
                "Doctor"
            else
                "Patient"
        }
    }

    fun isUserDoctor(): Boolean {
        return this.toItemString() == IS_DOCTOR.toItemString()
    }

    fun toDisplayString(): String {
        return if (this == IS_DOCTOR) {
            "Vâng, tôi là bác sĩ"
        } else "Không, tôi không phải là bác sĩ"
    }

    fun toItemString(): String {
        return if (this == IS_DOCTOR) {
            "Doctor"
        } else "Patient"
    }
}

enum class Specialist {
    CARDIOLOGIST,
    DENTIST,
    ENT_SPECIALIST,
    OBSTETRICIAN_GYNAECOLOGIST,
    ORTHOPAEDIC_SURGEON,
    PSYCHIATRIST,
    RADIOLOGIST,
    PULMONOLOGIST,
    NEUROLOGIST,
    ALLERGISTS,
    GASTROENTEROLOGISTS;

    companion object {
        fun fromString(value: String): Specialist? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

    }

    fun toItemString(): String {
        return when (this) {
            CARDIOLOGIST -> "Bác sĩ tim mạch"
            DENTIST -> "Nha sĩ"
            ENT_SPECIALIST -> "Bác sĩ tai mũi họng"
            OBSTETRICIAN_GYNAECOLOGIST -> "Bác sĩ sản phụ khoa"
            ORTHOPAEDIC_SURGEON -> "Bác sĩ chỉnh hình"
            PSYCHIATRIST -> "Bác sĩ tâm thần"
            RADIOLOGIST -> "Bác sĩ X-quang"
            PULMONOLOGIST -> "Bác sĩ phổi"
            NEUROLOGIST -> "Bác sĩ thần kinh"
            ALLERGISTS -> "Bác sĩ dị ứng"
            GASTROENTEROLOGISTS -> "Bác sĩ tiêu hóa"
        }
    }
}


enum class Gender(gender: String) {
    MALE("nam"),
    FEMALE("nu"),
    OTHER("khac");

    companion object {
        fun getGenderToGender(gender: String): Gender {
            return when (gender) {
                MALE.toDisplayString() -> MALE
                FEMALE.toDisplayString() -> FEMALE
                else -> OTHER
            }
        }

        fun getGenderToString(gender: String): String {
            return when (gender) {
                MALE.toDisplayString() -> "nam"
                FEMALE.toDisplayString() -> "nu"
                else -> "khac"
            }
        }
    }

    fun toDisplayString(): String {
        return when (this) {
            MALE -> "Nam"
            FEMALE -> "Nữ"
            else -> "Khác"
        }
    }
    fun toItemString(): String {
        return when (this) {
            MALE -> "nam"
            FEMALE -> "nu"
            else -> "khac"
        }
    }
}

@Parcelize
data class Rating(
    val patientId: String,
    val doctorId: String,
    val rating: Float,
    val review: String,
    val timestamp: String,
    val patientName: String,
) : Parcelable