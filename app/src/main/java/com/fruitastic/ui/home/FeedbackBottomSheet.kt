import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import com.fruitastic.R

class FeedbackBottomSheet : BottomSheetDialogFragment() {

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
                Toast.makeText(context, "Feedback sent: $feedback", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(context,
                    getString(R.string.please_enter_feedback), Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
