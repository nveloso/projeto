package ps.leic.isel.pt.gis.uis.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.layout_new_house_dialog.view.*
import ps.leic.isel.pt.gis.R
import ps.leic.isel.pt.gis.model.House
import ps.leic.isel.pt.gis.utils.EditTextUtils
import ps.leic.isel.pt.gis.utils.RestrictionsUtils

class NewHouseDialogFragment : DialogFragment() {

    private var listener: OnNewHouseDialogFragmentInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNewHouseDialogFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnNewHouseDialogFragmentInteractionListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        val view = inflater.inflate(R.layout.layout_new_house_dialog, null)
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, { _, _ ->
                    addHouse(view)
                })
                .setNegativeButton(R.string.cancel, { _, _ -> this@NewHouseDialogFragment.dialog.cancel() })

        // Set listeners
        setButtonsListeners(view)

        return builder.create()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /***
     * Auxiliary Methods
     ***/

    private fun setButtonsListeners(view: View) {
        // Set Plus buttons listeners
        view.babiesPlusBtn.setOnClickListener {
            EditTextUtils.incNumberText(
                    view.babiesNumEditText,
                    RestrictionsUtils.characteristicsMinValue,
                    RestrictionsUtils.characteristicsMaxValue)
        }

        view.childrenPlusBtn.setOnClickListener {
            EditTextUtils.incNumberText(
                    view.childrenNumEditText,
                    RestrictionsUtils.characteristicsMinValue,
                    RestrictionsUtils.characteristicsMaxValue)
        }

        view.adultsPlusBtn.setOnClickListener {
            EditTextUtils.incNumberText(
                    view.adultsNumEditText,
                    RestrictionsUtils.characteristicsMinValue,
                    RestrictionsUtils.characteristicsMaxValue)
        }

        view.seniorsPlusBtn.setOnClickListener {
            EditTextUtils.incNumberText(
                    view.seniorNumEditText,
                    RestrictionsUtils.characteristicsMinValue,
                    RestrictionsUtils.characteristicsMaxValue)
        }

        // Set Minus buttons listeners
        view.babiesMinusBtn.setOnClickListener {
            EditTextUtils.decNumberText(
                    view.babiesNumEditText,
                    RestrictionsUtils.characteristicsMinValue)
        }
        view.childrenMinusBtn.setOnClickListener {
            EditTextUtils.decNumberText(
                    view.childrenNumEditText,
                    RestrictionsUtils.characteristicsMinValue)
        }

        view.adultsMinusBtn.setOnClickListener {
            EditTextUtils.decNumberText(
                    view.adultsNumEditText,
                    RestrictionsUtils.characteristicsMinValue)
        }

        view.seniorsMinusBtn.setOnClickListener {
            EditTextUtils.decNumberText(
                    view.seniorNumEditText,
                    RestrictionsUtils.characteristicsMinValue)
        }
    }

    private fun addHouse(view: View) {
        val houseName: String = view.housesnameEditText.text.toString()
        val babiesNumber: Short = view.babiesNumEditText.text.toString().toShortOrNull() ?: 0
        val childrenNumber: Short = view.childrenNumEditText.text.toString().toShortOrNull() ?: 0
        val adultsNumber: Short = view.adultsNumEditText.text.toString().toShortOrNull() ?: 0
        val seniorsNumber: Short = view.seniorNumEditText.text.toString().toShortOrNull() ?: 0
        listener?.onAddHouse(House(houseName, babiesNumber, childrenNumber, adultsNumber, seniorsNumber))
    }

    /***
     * Auxiliary Methods
     ***/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnNewHouseDialogFragmentInteractionListener {
        fun onAddHouse(house: House)
    }

    /**
     * NewHouseDialogFragment Factory
     */
    companion object {
        const val TAG: String = "NewHouseDialogFragment"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment NewListDialogFragment.
         */
        @JvmStatic
        fun newInstance() = NewHouseDialogFragment()
    }
}