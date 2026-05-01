package com.desynatdriver.ui.home.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desynatdriver.R
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.databinding.FragmentCmsContentBinding
import com.desynatdriver.ui.home.HomeActivity
import com.desynatdriver.ui.home.cms.response.GetCmsResponse
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.Constants
import com.desynatdriver.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class CmsContentFragment : BaseFragment() {

    private var binding: FragmentCmsContentBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var type = 1 //1-terms,2-privacy,3-about,4-return,5-exchange,6-cancellation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCmsContentBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        type = arguments?.getInt("type")?: 1
        binding?.apply {
            toolbar.tvToolbar.text =  when(type){
                1-> getString(R.string.terms_conditions)
                2-> getString(R.string.privacy_policy)
                3-> getString(R.string.about_us)
                4-> getString(R.string.return_policy)
                5-> getString(R.string.exchange_policy)
                6-> getString(R.string.cancellation_policy)
                else -> getString(R.string.terms_conditions)
            }

            getContent()

        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.termsAndConditionResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    setContent(response.data)

                }
                isObserver = false
            }
        }

        viewModel.privacyPolicyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    setContent(response.data)

                }
                isObserver = false
            }
        }

        viewModel.aboutUsResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    setContent(response.data)

                }
                isObserver = false
            }
        }
        viewModel.returnPolicyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    setContent(response.data)

                }
                isObserver = false
            }
        }
        viewModel.exchangePolicyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    setContent(response.data)

                }
                isObserver = false
            }
        }

        viewModel.cancellationPolicyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    setContent(response.data)

                }
                isObserver = false
            }
        }
    }

    private fun setContent(content: GetCmsResponse.Data){
        binding?.apply {
            tvContent.text = if (Constants.language=="ar") CommonMethod.setHtmlText(content?.contentArabic.toString()) else CommonMethod.setHtmlText(content?.contentEnglish.toString())
        }
    }

    fun getContent(){
        val map = HashMap<String, Any>()
        map["type"] = 3
        when(type){
            1-> viewModel.termsAndCondition(map)
            2-> viewModel.privacyPolicy(map)
            3-> viewModel.aboutUs(map)
            4-> viewModel.returnPolicy(map)
            5-> viewModel.exchangePolicy(map)
            6-> viewModel.cancellationPolicy(map)
        }

        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isObserver = true
    }
}