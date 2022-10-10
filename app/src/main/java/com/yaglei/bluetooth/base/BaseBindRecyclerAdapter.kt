package com.yaglei.bluetooth.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Type
import kotlin.reflect.KClass


abstract class BaseBindRecyclerAdapter<T : ViewBinding> :
    androidx.recyclerview.widget.RecyclerView.Adapter<BaseBindRecyclerAdapter.Holder<T>>() {

    abstract fun initView(mBinding: T, view: View, position: Int)

    abstract fun getViewBinding(arent: ViewGroup): T

    open fun initView(holder: Holder<T>, position: Int) {

    }

    override fun onBindViewHolder(holder: Holder<T>, position: Int) {
        initView(holder.mBinding, holder.itemView, position)
        initView(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<T> {
        val viewBinding = getViewBinding(parent)
        return Holder(viewBinding, viewBinding.root)
    }

    class Holder<T : ViewBinding>(val mBinding: T, view: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}