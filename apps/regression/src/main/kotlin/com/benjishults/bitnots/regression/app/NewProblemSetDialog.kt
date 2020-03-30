package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.jfx.FormBuilder
import com.benjishults.bitnots.parser.BitnotsFileRepo
import com.benjishults.bitnots.parser.IprFileRepo
import com.benjishults.bitnots.regression.app.problem.ProblemSetBuilder
import com.benjishults.bitnots.tptp.TptpFileRepo
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.util.Callback
import javafx.util.StringConverter
import org.controlsfx.control.CheckComboBox

class NewProblemSetDialog() : Dialog<ProblemSetBuilder>() {

    // TODO get from config
    private val inputFormats: List<String> = "tptp,ipr,bitnots".split(",")

    private val nameProperty: StringProperty
    private val formatProperty: ObjectProperty<String>
    private val domainsProperty: ObservableList<TptpDomain>
    private val formProperty: ObjectProperty<TptpFormulaForm>

    init {
        title = "Customize new problem set"
        dialogPane.stylesheets.add("css/ui.css")
        dialogPane.buttonTypes.addAll(
                ButtonType("Create", ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL)
        dialogPane.content = VBox().also { content ->
            content.children.add(
                    FormBuilder().also { builder ->
                        builder.addLabelAndControl(
                                Label("name"),
                                TextField().also {
                                    nameProperty = it.textProperty()
                                })
                        builder.addLabelAndControl(
                                Label("format"),
                                ComboBox(FXCollections.unmodifiableObservableList(
                                        FXCollections.observableList(inputFormats))).also { comboBox ->
                                    comboBox.value = inputFormats.first()
                                    comboBox.isEditable = false
                                    formatProperty = comboBox.valueProperty()
                                })
                        builder.addLabelAndControl(
                                Label("domains"),
                                CheckComboBox<TptpDomain>(FXCollections.unmodifiableObservableList(
                                        FXCollections.observableList(TptpDomain.values().asList()))
                                ).also { comboBox ->
                                    comboBox.converter = object : StringConverter<TptpDomain>() {

                                        override fun toString(
                                                `object`: TptpDomain): String {
                                            return "${`object`.name} ${`object`.field} - ${`object`.subfield}"
                                        }

                                        override fun fromString(
                                                string: String): TptpDomain {
                                            return TptpDomain.valueOf(string)
                                        }

                                    }
                                    domainsProperty = comboBox.checkModel.checkedItems
                                })
                        builder.addLabelAndControl(
                                Label("form"),
                                ComboBox<TptpFormulaForm>(FXCollections.unmodifiableObservableList(
                                        FXCollections.observableList(
                                                TptpFormulaForm.values().toList()))).also { comboBox ->
                                    formProperty = comboBox.valueProperty()
                                }
                        )
                    }.form
            )
        }
        resultConverter = Callback { buttonType: ButtonType ->
            when (buttonType.buttonData) {
                ButtonBar.ButtonData.OK_DONE ->
                    ProblemSetBuilder(
                            nameProperty.get(),
                            stringToProblemSource(formatProperty.get()),
                            domainsProperty.toList(),
                            formProperty.value)

                else          -> null
            }
        }
    }

    companion object {
        fun stringToProblemSource(source: String) =
                when (source) {
                    "tptp"    -> TptpFileRepo
                    "bitnots" -> BitnotsFileRepo
                    "ipr"     -> IprFileRepo
                    else      -> error("Unrecognized problem source: ${source}")
                }
    }
}

