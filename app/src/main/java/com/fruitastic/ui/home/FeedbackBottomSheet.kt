import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.fruitastic.R
import com.fruitastic.data.ViewModelFactory
import com.fruitastic.data.remote.request.FeedbackRequest
import com.fruitastic.ui.home.HomeViewModel

class FeedbackBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_feedback, container, false)

        val feedbackInput = view.findViewById<TextInputEditText>(R.id.feedbackInput)
        val submitButton = view.findViewById<MaterialButton>(R.id.submitFeedbackButton)

        submitButton.setOnClickListener {
            val feedback = feedbackInput.text.toString()
            if (feedback.isNotEmpty()) {
                viewModel.feedback(FeedbackRequest(feedback))
                viewModel.messageFeedback.observe(viewLifecycleOwner) { message ->
                    if (message != null) {
                        showToast(message)
                        dismiss()
                    }
                }
            } else {
                showToast(R.string.please_input_feedback_first.toString())
            }
        }

        return view
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
