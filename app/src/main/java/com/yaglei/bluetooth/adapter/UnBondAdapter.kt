package com.yaglei.bluetooth.adapter

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.yaglei.bluetooth.base.BaseBindRecyclerAdapter
import com.yaglei.bluetooth.databinding.ItemBondBinding
import com.yaglei.bluetooth.databinding.ItemUnbondBinding

class UnBondAdapter(val activity: Activity) :
    BaseBindRecyclerAdapter<ItemUnbondBinding>() {
    private var list: MutableList<BluetoothDevice> = ArrayList()

    override fun getViewBinding(arent: ViewGroup): ItemUnbondBinding {
        return ItemUnbondBinding.inflate(activity.layoutInflater, arent, false)
    }

    fun update(list: MutableList<BluetoothDevice>) {
        this.list = list
        notifyDataSetChanged()
    }


    override fun initView(mBinding: ItemUnbondBinding, view: View, position: Int) {
        val item = list[position]
        mBinding.name.text = item.name
        mBinding.address.text = item.address

        mBinding.bond.setOnClickListener {
            item.createBond()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}