package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.jfx.FormBuilder
import com.benjishults.bitnots.regression.problem.TptpProblemSetBuilder
import com.benjishults.bitnots.sexpParser.IprFileRepo
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.theory.formula.CNF
import com.benjishults.bitnots.theory.formula.FOF
import com.benjishults.bitnots.theory.formula.FormulaForm
import com.benjishults.bitnots.tptp.TptpFileRepo
import com.benjishults.bitnots.tptp.files.TptpCnf
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFof
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

class NewProblemSetDialog() : Dialog<TptpProblemSetBuilder>() {

    // TODO get from config
    private val inputFormats: List<String> = "tptp,ipr,bitnots".split(",")

    private val nameProperty: StringProperty
    private val formatProperty: ObjectProperty<String>
    private val domainsProperty: ObservableList<TptpDomain>
    private val formProperty: ObjectProperty<FormulaForm>
    private val qLimitProperty: StringProperty
    private var stepLimitProperty: StringProperty
    private var timeLimitProperty: StringProperty

    init {
        title = "Customize new problem set"
        dialogPane.stylesheets.add("css/ui.css")
        dialogPane.buttonTypes.addAll(
            ButtonType("Create", ButtonBar.ButtonData.OK_DONE),
            ButtonType.CANCEL
        )
        dialogPane.content = VBox().also { content ->
            content.children.add(
                FormBuilder().also { builder ->
                    builder.addLabelAndControl(
                        Label("name"),
                        TextField().also { field ->
                            nameProperty = field.textProperty()
                            field.requestFocus()
                        })
                    builder.addLabelAndControl(
                        Label("format"),
                        ComboBox(
                            FXCollections.unmodifiableObservableList(
                                FXCollections.observableList(inputFormats)
                            )
                        ).also { comboBox ->
                            comboBox.value = inputFormats.first()
                            comboBox.isEditable = false
                            formatProperty = comboBox.valueProperty()
                        })
                    builder.addLabelAndControl(
                        Label("domains"),
                        CheckComboBox<TptpDomain>(
                            FXCollections.unmodifiableObservableList(
                                FXCollections.observableList(TptpDomain.values().asList())
                            )
                        ).also { comboBox ->
                            comboBox.converter = object : StringConverter<TptpDomain>() {

                                override fun toString(
                                    `object`: TptpDomain
                                ): String {
                                    return "${`object`.name} ${`object`.field} - ${`object`.subfield}"
                                }

                                override fun fromString(
                                    string: String
                                ): TptpDomain {
                                    return TptpDomain.valueOf(string)
                                }

                            }
                            domainsProperty = comboBox.checkModel.checkedItems
                        })
                    builder.addLabelAndControl(
                        Label("form"),
                        ComboBox(
                            FXCollections.unmodifiableObservableList(
                                FXCollections.observableList(
                                    listOf(FOF, CNF)
                                )
                            )
                        ).also { comboBox ->
                            formProperty = comboBox.valueProperty()
                        }
                    )
                    builder.addLabelAndControl(
                        Label("qLimit"),
                        TextField("3").also { field ->
                            qLimitProperty = field.textProperty()
                        }
                    )
                    builder.addLabelAndControl(
                        Label("step limit"),
                        TextField().also { field ->
                            stepLimitProperty = field.textProperty()
                        }
                    )
                    builder.addLabelAndControl(
                        Label("time limit millis"),
                        TextField().also { field ->
                            timeLimitProperty = field.textProperty()
                        }
                    )
                }.form
            )
        }
        resultConverter = Callback { buttonType: ButtonType ->
            when (buttonType.buttonData) {
                ButtonBar.ButtonData.OK_DONE ->
                    TptpProblemSetBuilder(
                        nameProperty.get(),
                        domainsProperty.toList(),
                        // TODO abstract this appropriately
                        when (formProperty.value) {
                            CNF -> TptpCnf
                            FOF -> TptpFof
                            else -> {
                                throw IllegalArgumentException()
                            }
                        },
                        FolTableauHarness(
                            qLimitProperty.value.takeIf { it.trim().length > 0 }?.toLong() ?: -1L,
                            stepLimitProperty.value.takeIf { it.trim().length > 0 }?.toLong() ?: -1L,
                            timeLimitProperty.value.takeIf { it.trim().length > 0 }?.toLong() ?: -1L
                        )
                    )
                else                         -> null
            }
        }
    }

    companion object {
        fun stringToProblemSource(source: String) =
            when (source) {
                "tptp" -> TptpFileRepo
                // "bitnots" -> BitnotsFileRepo
                "ipr"  -> IprFileRepo
                else   -> error("Unrecognized problem source: ${source}")
            }
    }
}

