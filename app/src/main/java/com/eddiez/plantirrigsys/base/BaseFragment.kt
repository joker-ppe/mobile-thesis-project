package com.eddiez.plantirrigsys.base

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.marcinorlowski.fonty.Fonty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment: Fragment() {
    override fun onResume() {
        super.onResume()

        Fonty.Companion.setFonts(this.view as ViewGroup)
    }
}