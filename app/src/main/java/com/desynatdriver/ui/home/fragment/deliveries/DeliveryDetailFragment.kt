package com.desynatdriver.ui.home.fragment.deliveries

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.databinding.FragmentDeliveryDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeliveryDetailFragment : BaseFragment() {
    private var binding: FragmentDeliveryDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveryDetailBinding.inflate(layoutInflater,container,false)
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
            var status = ""
            when(status){
                "1"->{
                    tvStatus.text = "Confirmed"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                }
                "2"->{
                    tvStatus.text = "Picked"
                    tvStatus.setTextColor(Color.parseColor("#2A74B8"))
                }
                "3"->{
                    tvStatus.text = "Delivered"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                }
                "4"->{
                    tvStatus.text = "Not Delivered"
                    tvStatus.setTextColor(Color.parseColor("#EB2525"))
                    tvReason.isVisible = false
                    tvReasonForNotDelivered.isVisible = false
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun observe() {

    }

}