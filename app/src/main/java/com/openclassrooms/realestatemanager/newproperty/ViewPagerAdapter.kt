package com.openclassrooms.realestatemanager.newproperty

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.openclassrooms.realestatemanager.databinding.ItemViewpagerBinding

class ViewPagerAdapter(var list: MutableList<Uri>, var context: Context, var width: Float) : PagerAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemViewpagerBinding.inflate(LayoutInflater.from(context), container, false)
        val view = binding.root

        binding.imageViewViewPager.setImageURI(list[position])
        container.addView(view, 0)
        return view
    }

    override fun getPageWidth(position: Int): Float {
        return width
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}