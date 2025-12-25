package com.desynatdriver.ui.home.fragment.deliveries

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatdriver.R
import com.desynatdriver.adapter.MyDeliveriesAdapter
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.databinding.DeliveryFilterDialogBinding
import com.desynatdriver.databinding.FragmentDeliveriesBinding
import com.desynatdriver.databinding.UpdateDeliveryStatusDialogBinding
import com.desynatdriver.ui.home.fragment.deliveries.response.GetAllOrderResponse
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.SharedPreference
import com.desynatdriver.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeliveriesFragment : BaseFragment() {
    private var binding: FragmentDeliveriesBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var type:Int=1 //1-all,2-new,3-ongoing,4-completed
    private var deliveryFilter:String?=null
    private var deliveryList = ArrayList<GetAllOrderResponse.Data.Data>()
    private var myDeliveriesAdapter: MyDeliveriesAdapter?=null

    private var search: Job? = null
    private var lastQuery: String? = null

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
        type = arguments?.getInt("DELIVERY_TYPE", 1) ?: 1
        binding?.apply {
            deliveryList.clear()
            myDeliveriesAdapter = MyDeliveriesAdapter(requireActivity(),deliveryList,object :MyDeliveriesAdapter.ClickOnItem{
                override fun onClickItem(position: Int, type: Int) {
                    when(type) {
                        1->{

                        }
                        2 -> {

                            if (deliveryList[position].deliveryStatus=="3"||deliveryList[position].deliveryStatus=="8") {
                                val bundle = Bundle().apply {
                                    putString("deliveryData", Gson().toJson(deliveryList[position]))
                                }

                                findNavController().navigate(
                                    R.id.action_deliveriesFragment_to_deliveryDetailFragment,
                                    bundle
                                )

                            }
                            else{
                                val bundle = Bundle().apply {
                                    putString("deliveryData", Gson().toJson(deliveryList[position]))
                                }

                                findNavController().navigate(
                                    R.id.action_deliveriesFragment_to_newOrderFragment,
                                    bundle
                                )
                            }
                        }
                    }
                }

            })
            rvMyDeliveries.adapter = myDeliveriesAdapter

            when(type){
                1->{
                    type = 1
                    ivFilter.isVisible = true
                    getAllOrder()
                    tvAll.setTextColor(requireActivity().getColor(R.color.brown))
                    tvNew.setTextColor(Color.parseColor("#B3B3B3"))
                    tvOngoing.setTextColor(Color.parseColor("#B3B3B3"))
                    tvCompleted.setTextColor(Color.parseColor("#B3B3B3"))
                    viewAll.setBackgroundColor(requireActivity().getColor(R.color.brown))
                    viewNew.setBackgroundColor(Color.parseColor("#B3B3B3"))
                    viewCompleted.setBackgroundColor(Color.parseColor("#B3B3B3"))
                    viewOngoing.setBackgroundColor(Color.parseColor("#B3B3B3"))
                }
                2->{
                    type = 2
                    ivFilter.isVisible = false
                    getNewOrder()
                    tvNew.setTextColor(requireActivity().getColor(R.color.brown))
                    tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                    tvOngoing.setTextColor(Color.parseColor("#B3B3B3"))
                    tvCompleted.setTextColor(Color.parseColor("#B3B3B3"))
                    viewNew.setBackgroundColor(requireActivity().getColor(R.color.brown))
                    viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                    viewCompleted.setBackgroundColor(Color.parseColor("#B3B3B3"))
                    viewOngoing.setBackgroundColor(Color.parseColor("#B3B3B3"))
                }
                3->{
                    type = 3
                    ivFilter.isVisible = false
                    getOngoingOrder()
                    tvOngoing.setTextColor(requireActivity().getColor(R.color.brown))
                    tvNew.setTextColor(Color.parseColor("#B3B3B3"))
                    tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                    tvCompleted.setTextColor(Color.parseColor("#B3B3B3"))
                    viewOngoing.setBackgroundColor(requireActivity().getColor(R.color.brown))
                    viewNew.setBackgroundColor(Color.parseColor("#B3B3B3"))
                    viewCompleted.setBackgroundColor(Color.parseColor("#B3B3B3"))
                    viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                }
                4->{
                    type = 4
                    ivFilter.isVisible = false
                    getCompletedOrder()
                    tvNew.setTextColor(Color.parseColor("#B3B3B3"))
                    tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                    tvOngoing.setTextColor(Color.parseColor("#B3B3B3"))
                    tvCompleted.setTextColor(requireActivity().getColor(R.color.brown))
                    viewCompleted.setBackgroundColor(requireActivity().getColor(R.color.brown))
                    viewNew.setBackgroundColor(Color.parseColor("#B3B3B3"))
                    viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                    viewOngoing.setBackgroundColor(Color.parseColor("#B3B3B3"))
                }
            }

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        if (!isObserver)
                            when(type) {
                                1 -> {
                                    getAllOrder(search =  query.ifEmpty { null })
                                }
                                2 -> {
                                    getNewOrder(search =  query.ifEmpty { null })
                                }
                                3 -> {
                                    getOngoingOrder(search =  query.ifEmpty { null })
                                }
                                4 -> {
                                    getCompletedOrder(search =  query.ifEmpty { null })
                                }
                            }

                    }

                }
            })

        }

    }

    override fun initControl() {
        binding?.apply {
            tvAll.setOnClickListener {
                type = 1
                ivFilter.isVisible = true
                getAllOrder()
                tvAll.setTextColor(requireActivity().getColor(R.color.brown))
                tvNew.setTextColor(Color.parseColor("#B3B3B3"))
                tvOngoing.setTextColor(Color.parseColor("#B3B3B3"))
                tvCompleted.setTextColor(Color.parseColor("#B3B3B3"))
                viewAll.setBackgroundColor(requireActivity().getColor(R.color.brown))
                viewNew.setBackgroundColor(Color.parseColor("#B3B3B3"))
                viewCompleted.setBackgroundColor(Color.parseColor("#B3B3B3"))
                viewOngoing.setBackgroundColor(Color.parseColor("#B3B3B3"))
            }

            tvNew.setOnClickListener {
                type = 2
                ivFilter.isVisible = false
                getNewOrder()
                tvNew.setTextColor(requireActivity().getColor(R.color.brown))
                tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                tvOngoing.setTextColor(Color.parseColor("#B3B3B3"))
                tvCompleted.setTextColor(Color.parseColor("#B3B3B3"))
                viewNew.setBackgroundColor(requireActivity().getColor(R.color.brown))
                viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                viewCompleted.setBackgroundColor(Color.parseColor("#B3B3B3"))
                viewOngoing.setBackgroundColor(Color.parseColor("#B3B3B3"))
            }

            tvOngoing.setOnClickListener {
                type = 3
                ivFilter.isVisible = false
                getOngoingOrder()
                tvOngoing.setTextColor(requireActivity().getColor(R.color.brown))
                tvNew.setTextColor(Color.parseColor("#B3B3B3"))
                tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                tvCompleted.setTextColor(Color.parseColor("#B3B3B3"))
                viewOngoing.setBackgroundColor(requireActivity().getColor(R.color.brown))
                viewNew.setBackgroundColor(Color.parseColor("#B3B3B3"))
                viewCompleted.setBackgroundColor(Color.parseColor("#B3B3B3"))
                viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
            }

            tvCompleted.setOnClickListener {
                type = 4
                ivFilter.isVisible = false
                getCompletedOrder()
                tvNew.setTextColor(Color.parseColor("#B3B3B3"))
                tvAll.setTextColor(Color.parseColor("#B3B3B3"))
                tvOngoing.setTextColor(Color.parseColor("#B3B3B3"))
                tvCompleted.setTextColor(requireActivity().getColor(R.color.brown))
                viewCompleted.setBackgroundColor(requireActivity().getColor(R.color.brown))
                viewNew.setBackgroundColor(Color.parseColor("#B3B3B3"))
                viewAll.setBackgroundColor(Color.parseColor("#B3B3B3"))
                viewOngoing.setBackgroundColor(Color.parseColor("#B3B3B3"))
            }

            ivFilter.setOnClickListener {
                filterDialog()
            }
        }

    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }

        }

        viewModel.getAllOrderResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    deliveryList.clear()
                    deliveryList.addAll(response.data.data)
                    myDeliveriesAdapter?.notifyDataSetChanged()
                    setAllDeliveriesValue()
                    checkData()
                }
                isObserver = false
            }
        }

        viewModel.getNewOrderResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    deliveryList.clear()
                    deliveryList.addAll(response.data.data)
                    myDeliveriesAdapter?.notifyDataSetChanged()
                    checkData()
                }
                isObserver = false
            }
        }

        viewModel.getOngoingOrderResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    deliveryList.clear()
                    deliveryList.addAll(response.data.data)
                    myDeliveriesAdapter?.notifyDataSetChanged()
                    checkData()
                }
                isObserver = false
            }
        }

        viewModel.getCompletedOrderResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    deliveryList.clear()
                    deliveryList.addAll(response.data.data)
                    myDeliveriesAdapter?.notifyDataSetChanged()
                    checkData()
                }
                isObserver = false
            }
        }
    }

    private fun setAllDeliveriesValue() {
        binding?.tvAll?.text = "All(${deliveryList.size})"
    }

    private fun getAllOrder(search:String?=null) {
        val map = HashMap<String,Any>()
        search?.let { map["search"] = it }
        deliveryFilter?.let { map["filter"] = it }
        viewModel.getAllOrder(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getNewOrder(search:String?=null) {
        val map = HashMap<String,Any>()
        search?.let { map["search"] = it }
        viewModel.getNewOrder(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getOngoingOrder(search:String?=null) {
        val map = HashMap<String,Any>()
        search?.let { map["search"] = it }
        viewModel.getOngoingOrder(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getCompletedOrder(search:String?=null) {
        val map = HashMap<String,Any>()
        search?.let { map["search"] = it }
        viewModel.getCompletedOrder(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun checkData(){
        binding?.tvNoDataFound?.isVisible = deliveryList.isEmpty()
    }

    private fun filterDialog() {
        var filter = ""
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = DeliveryFilterDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            if (deliveryFilter!=null) {
                when (deliveryFilter) {
                    "7" -> dialogBinding.rbPicked.isChecked = true
                    "6" -> dialogBinding.rbConfirmed.isChecked = true
                    "3" -> dialogBinding.rbDelivered.isChecked = true
                    "8" -> dialogBinding.rbNotDelivered.isChecked = true
                }
            }

            ivBack.setOnClickListener {
                dialog.dismiss()
            }

            tvApply.setOnClickListener {
                if (filter!=""){
                    deliveryFilter = filter
                    getAllOrder()
                    dialog?.dismiss()
                }
                else{
                    showToast("Please select delivery status")
                }

            }

            tvClear.setOnClickListener {
                deliveryFilter = null
                filter = ""
                getAllOrder()
                dialog?.dismiss()
            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                filter = when (checkedId) {
                    R.id.rbPicked -> "7"
                    R.id.rbConfirmed -> "6"
                    R.id.rbDelivered -> "3"
                    R.id.rbNotDelivered -> "8"
                    else -> ""
                }
            }

        }

        dialog.show()
    }


}