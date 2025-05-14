package com.SPAccounting.HealthX.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.SPAccounting.HealthX.R
import com.SPAccounting.HealthX.model.Doctor
import com.SPAccounting.HealthX.model.Gender
import com.SPAccounting.HealthX.model.Specialist


object Utils {

    @SuppressLint("QueryPermissionsNeeded", "IntentReset")
    fun sendEmailToGmail(activity: Activity, subject: String?, body: String?, email: String?) {
        val emailIntent = Intent().apply {
            action = Intent.ACTION_SEND
            data = Uri.parse("mailto:")
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        if (emailIntent.resolveActivity(activity.packageManager) != null) {
            emailIntent.setPackage("com.google.android.gm")
            startActivity(activity, emailIntent, null)
        } else {
            Toast.makeText(
                activity,
                "No app available to send email!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun containsLetters(phone: String, searchedData: String): Boolean {
        return phone.trim().lowercase().toStringWithoutSpaces().contains(
            searchedData.lowercase().trim().toStringWithoutSpaces()
        )
    }

    fun View.setNonDuplicateClickListener(listener: View.OnClickListener?) {
        setOnClickListener {
            var lastClickTime: Long = 0
            if (getTag(R.id.TAG_CLICK_TIME) != null) {
                lastClickTime = getTag(R.id.TAG_CLICK_TIME) as Long
            }
            val curTime = System.currentTimeMillis()
            if (curTime - lastClickTime > context.resources.getInteger(R.integer.duplicate_click_delay)) {
                listener?.onClick(this)
                setTag(R.id.TAG_CLICK_TIME, curTime)
            }
        }
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun String.toStringWithoutSpaces() : String {
        val stringBuilder = StringBuilder()
        for (char in this.toCharArray())
            if (char.isDigit() or char.isLetter())
                stringBuilder.append(char)
        return stringBuilder.toString()
    }

    fun getListOfSpecialization() : List<String> {
        return listOf(
            Specialist.ALLERGISTS.toItemString(),
            Specialist.ENT_SPECIALIST.toItemString(),
            Specialist.CARDIOLOGIST.toItemString(),
            Specialist.DENTIST.toItemString(),
            Specialist.ENT_SPECIALIST.toItemString(),
            Specialist.OBSTETRICIAN_GYNAECOLOGIST.toItemString(),
            Specialist.ORTHOPAEDIC_SURGEON.toItemString(),
            Specialist.PSYCHIATRIST.toItemString(),
            Specialist.RADIOLOGIST.toItemString(),
            Specialist.PULMONOLOGIST.toItemString(),
            Specialist.NEUROLOGIST.toItemString(),
            Specialist.ALLERGISTS.toItemString(),
            Specialist.GASTROENTEROLOGISTS.toItemString(),
        )
    }

    fun getListOfIsDoctor() : List<String> {
        return listOf(
            Doctor.IS_DOCTOR.toDisplayString(),
            Doctor.IS_NOT_DOCTOR.toDisplayString()
        )
    }

    fun getListOfGenders() : List<String> {
        return listOf(
            Gender.MALE.toDisplayString(),
            Gender.FEMALE.toDisplayString(),
            Gender.OTHER.toDisplayString()
        )
    }

    fun setDiseaseValues(context: Context): HashMap<String, Float> {
        val diseaseValue: HashMap<String, Float> = HashMap()

        // Diseases and their corresponding values are added to the diseaseValue map
        diseaseValue[context.getString(R.string.disease_not_sure)] = 6f

        // Cardiovascular diseases
        diseaseValue[context.getString(R.string.disease_high_blood_pressure)] = 6f
        diseaseValue[context.getString(R.string.disease_high_cholesterol)] = 5f
        diseaseValue[context.getString(R.string.disease_angina)] = 4f
        diseaseValue[context.getString(R.string.disease_heart_rhythm_disorders)] = 5f
        diseaseValue[context.getString(R.string.disease_atrial_fibrillation)] = 4f

        // Dental diseases
        diseaseValue[context.getString(R.string.disease_tooth_decay)] = 6f
        diseaseValue[context.getString(R.string.disease_gum_disease)] = 5f
        diseaseValue[context.getString(R.string.disease_cracked_teeth)] = 3f
        diseaseValue[context.getString(R.string.disease_root_infection)] = 4f
        diseaseValue[context.getString(R.string.disease_tooth_loss)] = 2f

        // Ear, nose, and throat diseases
        diseaseValue[context.getString(R.string.disease_hearing_problems)] = 6f
        diseaseValue[context.getString(R.string.disease_allergies)] = 4f
        diseaseValue[context.getString(R.string.disease_nasal_congestion)] = 5f
        diseaseValue[context.getString(R.string.disease_tonsil_infections)] = 5f
        diseaseValue[context.getString(R.string.disease_enlarged_tonsils)] = 4f

        // Women's health-related diseases
        diseaseValue[context.getString(R.string.disease_pregnancy_bleeding)] = 6f
        diseaseValue[context.getString(R.string.disease_female_infertility)] = 5f
        diseaseValue[context.getString(R.string.disease_heart_disease_pregnancy)] = 6f
        diseaseValue[context.getString(R.string.disease_menopause)] = 4f
        diseaseValue[context.getString(R.string.disease_menstrual_cramps)] = 6f
        diseaseValue[context.getString(R.string.disease_miscarriage)] = 5f
        diseaseValue[context.getString(R.string.disease_ovarian_cysts)] = 3f
        diseaseValue[context.getString(R.string.disease_vaginal_bleeding)] = 5f

        // Musculoskeletal diseases
        diseaseValue[context.getString(R.string.disease_bone_fractures)] = 6f
        diseaseValue[context.getString(R.string.disease_muscle_strains)] = 5f
        diseaseValue[context.getString(R.string.disease_joint_back_pain)] = 3f
        diseaseValue[context.getString(R.string.disease_injuries_tendons_ligaments)] = 5f
        diseaseValue[context.getString(R.string.disease_limb_abnormalities)] = 4f
        diseaseValue[context.getString(R.string.disease_bone_cancer)] = 4f

        // Mental health-related diseases
        diseaseValue[context.getString(R.string.disease_alcohol_use_disorder)] = 4f
        diseaseValue[context.getString(R.string.disease_alzheimers)] = 6f
        diseaseValue[context.getString(R.string.disease_anxiety_disorders)] = 5f
        diseaseValue[context.getString(R.string.disease_bipolar_disorder)] = 5f
        diseaseValue[context.getString(R.string.disease_depression)] = 6f
        diseaseValue[context.getString(R.string.disease_eating_disorder)] = 5f
        diseaseValue[context.getString(R.string.disease_mood_disorders)] = 4f
        diseaseValue[context.getString(R.string.disease_panic_disorder)] = 4f
        diseaseValue[context.getString(R.string.disease_sleep_disorders)] = 4f

        // Cancer-related diseases
        diseaseValue[context.getString(R.string.disease_brain_tumor)] = 6f
        diseaseValue[context.getString(R.string.disease_breast_cancer)] = 5f
        diseaseValue[context.getString(R.string.disease_kidney_stones)] = 4f
        diseaseValue[context.getString(R.string.disease_liver_tumors)] = 5f

        return diseaseValue
    }

    fun setConditionValue(context: Context): HashMap<String, Float> {
        val conditionValue: HashMap<String, Float> = HashMap()
        conditionValue[context.getString(R.string.condition_severe_pain)] = 2.0f
        conditionValue[context.getString(R.string.condition_mild_pain)] = 1.5f
        conditionValue[context.getString(R.string.condition_no_pain)] = 1.0f
        return conditionValue
    }

    fun initializeSpecializationWithDiseasesLists(): HashMap<String, ArrayList<String>> {
        val mapOfDiseasesList: HashMap<String, ArrayList<String>> = HashMap()

        val specialistsAndDiseases = listOf(
            "Bác sĩ dị ứng" to arrayOf(
                "Không chắc chắn",
                "Hen suyễn",
                "Dị ứng da",
                "Nôn hoặc tiêu chảy",
                "Tụt huyết áp",
                "Da đỏ và/hoặc nổi mề đay",
                "Khó thở",
                "Sưng họng và/hoặc lưỡi"
            ),
            "Bác sĩ gây mê" to arrayOf(
                "Không chắc chắn",
                "Đau lưng hoặc đau cơ",
                "Ớn lạnh do nhiệt độ cơ thể thấp",
                "Khó tiểu",
                "Mệt mỏi",
                "Đau đầu",
                "Ngứa",
                "Viêm khớp nhiễm trùng"
            ),
            "Bác sĩ tim mạch" to arrayOf(
                "Không chắc chắn",
                "Cao huyết áp",
                "Cholesterol cao",
                "Đau thắt ngực",
                "Rối loạn nhịp tim",
                "Rung nhĩ"
            ),
            "Nha sĩ" to arrayOf(
                "Không chắc chắn",
                "Sâu răng",
                "Bệnh nướu răng",
                "Răng nứt hoặc gãy",
                "Nhiễm trùng chân răng",
                "Mất răng"
            ),
            "Bác sĩ tai mũi họng" to arrayOf(
                "Không chắc chắn",
                "Vấn đề về thính giác",
                "Dị ứng",
                "Nghẹt mũi",
                "Viêm amidan",
                "Amidan phì đại"
            ),
            "Bác sĩ tiêu hóa" to arrayOf(
                "Không chắc chắn",
                "Tiêu chảy",
                "Ngộ độc thực phẩm",
                "Đầy hơi",
                "Liệt dạ dày",
                "Hội chứng ruột kích thích",
                "Bệnh gan",
                "Viêm tụy",
                "Đau dạ dày"
            ),
            "Bác sĩ tâm thần" to arrayOf(
                "Không chắc chắn",
                "Rối loạn sử dụng rượu",
                "Bệnh Alzheimer",
                "Rối loạn lo âu",
                "Rối loạn lưỡng cực",
                "Trầm cảm",
                "Rối loạn ăn uống",
                "Rối loạn tâm trạng",
                "Rối loạn hoảng sợ",
                "Rối loạn giấc ngủ"
            ),
            "Bác sĩ X-quang" to arrayOf(
                "Không chắc chắn",
                "U não",
                "Ung thư vú",
                "Sỏi thận",
                "U gan",
                "Ung thư phổi",
                "Đau cổ",
                "Ung thư tụy",
                "U tuyến yên",
                "Ung thư tinh hoàn",
                "Ung thư tuyến giáp"
            ),
            "Bác sĩ phổi" to arrayOf(
                "Không chắc chắn",
                "Hen suyễn",
                "Đau hoặc tức ngực",
                "COVID-19",
                "Bệnh phổi kẽ",
                "Tăng áp động mạch phổi",
                "Bệnh lao"
            ),
            "Bác sĩ thần kinh" to arrayOf(
                "Không chắc chắn",
                "Chấn thương tủy sống cấp tính",
                "Bệnh Alzheimer",
                "Xơ cứng teo cơ một bên",
                "U não",
                "Phình động mạch não"
            ),
            "Bác sĩ tai mũi họng" to arrayOf(
                "Không chắc chắn",
                "Mất thính lực",
                "Nhiễm trùng tai",
                "Rối loạn thăng bằng",
                "Bệnh thanh quản",
                "Đau dây thần kinh",
                "Rối loạn dây thần kinh sọ mặt"
            ),
            "Bác sĩ sản phụ khoa" to arrayOf(
                "Không chắc chắn",
                "Chảy máu khi mang thai",
                "Vô sinh nữ",
                "Bệnh tim khi mang thai",
                "Mãn kinh",
                "Đau bụng kinh",
                "Sảy thai",
                "U nang buồng trứng",
                "Chảy máu âm đạo"
            ),
            "Bác sĩ chỉnh hình" to arrayOf(
                "Không chắc chắn",
                "Gãy xương",
                "Căng cơ",
                "Đau khớp hoặc lưng",
                "Chấn thương gân hoặc dây chằng",
                "Dị tật chi",
                "Ung thư xương"
            ),
            "Bác sĩ tiết niệu" to arrayOf(
                "Không chắc chắn",
                "Sỏi thận",
                "Nhiễm trùng bàng quang",
                "Bí tiểu",
                "Tiểu ra máu",
                "Rối loạn cương dương",
                "Phì đại tuyến tiền liệt",
                "Viêm bàng quang kẽ"
            ),
            "Bác sĩ thấp khớp" to arrayOf(
                "Không chắc chắn",
                "Viêm mạch máu",
                "Viêm khớp dạng thấp",
                "Lupus",
                "Xơ cứng bì"
            )
        )

        for ((specialization, diseases) in specialistsAndDiseases) {
            mapOfDiseasesList[specialization] = diseases.toList() as java.util.ArrayList<String>
        }

        return mapOfDiseasesList
    }

    fun makePhoneCall(activity: Activity, phone: String?) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phone")
        activity.startActivity(intent)
    }
}