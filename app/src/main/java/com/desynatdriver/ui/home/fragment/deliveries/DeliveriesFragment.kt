package com.desynatdriver.ui.home.fragment.deliveries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desynatdriver.adapter.MyDeliveriesAdapter
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.databinding.FragmentDeliveriesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeliveriesFragment : BaseFragment() {
    private var binding: FragmentDeliveriesBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var deliveryList = ArrayList<String>()
    private var myDeliveriesAdapter: MyDeliveriesAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveriesBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            deliveryList.clear()
            myDeliveriesAdapter = MyDeliveriesAdapter(requireActivity(),deliveryList,object :MyDeliveriesAdapter.ClickOnItem{
                override fun onClickItem(position: Int, type: Int) {

                }

            })
            rvMyDeliveries.adapter = myDeliveriesAdapter
        }

    }

    override fun initControl() {

    }

    override fun observe() {

    }

}