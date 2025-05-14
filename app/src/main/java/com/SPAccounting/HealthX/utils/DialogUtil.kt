package com.SPAccounting.HealthX.utils

import android.app.Activity
import android.content.Context
import android.view.View
import com.SPAccounting.HealthX.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


object DialogUtil {

    fun Context.createBottomSheet(): BottomSheetDialog {
        return BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
    }

    fun Activity.createBottomSheet(): BottomSheetDialog {
        return BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
    }

    fun View.setBottomSheet(bottomSheet: BottomSheetDialog) {
        bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.setContentView(this)
        bottomSheet.create()
        bottomSheet.show()
    }
}