package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.jfx.FormBuilder
import com.benjishults.bitnots.util.file.isValidFileName
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
import javafx.scene.control.Control
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.util.Callback
import javafx.util.StringConverter
import org.controlsfx.control.CheckComboBox
import org.controlsfx.validation.ValidationResult
import org.controlsfx.validation.ValidationSupport
import org.controlsfx.validation.Validator

class NewProblemSetDialog() : Dialog<TptpProblemSetBuilder>() {

    // TODO get from config
    private val inputFormats: List<String> = "tptp,ipr,bitnots".split(",")

    private var formSelection: ComboBox<FormulaForm>
    private var domainSelection: CheckComboBox<TptpDomain>
    private var formatSelection: ComboBox<String>

    private val nameProperty: StringProperty
    private val formatProperty: ObjectProperty<String>
    private val domainsProperty: ObservableList<TptpDomain>
    private val formProperty: ObjectProperty<FormulaForm>
    private val qLimitProperty: StringProperty
    private var stepLimitProperty: StringProperty
    private var timeLimitProperty: StringProperty

    private val nameField: TextField

    init {
        title = "Customize new problem set"
        dialogPane.stylesheets.add("css/ui.css")
        val createButtonType = ButtonType("Create", ButtonBar.ButtonData.OK_DONE)
        dialogPane.buttonTypes.addAll(
            createButtonType,
            ButtonType.CANCEL
        )
        dialogPane.content = VBox().also { content ->
            content.children.addAll(
                FormBuilder().also { builder ->

                    fun addDomainSelection(builder: FormBuilder): CheckComboBox<TptpDomain> {
                        val checkComboBox = CheckComboBox<TptpDomain>(
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
                        }
                        return builder.addLabelAndControl(
                            Label("domains"),
                            checkComboBox
                        ) as CheckComboBox<TptpDomain>
                    }

                    fun addFormatSelection(builder: FormBuilder): ComboBox<String> {
                        val control = ComboBox(
                            FXCollections.unmodifiableObservableList(
                                FXCollections.observableList(inputFormats)
                            )
                        ).also { comboBox ->
                            comboBox.value = inputFormats.first()
                            comboBox.isEditable = false
                        }
                        return builder.addLabelAndControl(
                            Label("format"),
                            control
                        ) as ComboBox<String>
                    }

                    fun addFormSelection(builder: FormBuilder): ComboBox<FormulaForm> {
                        val control = ComboBox(
                            FXCollections.unmodifiableObservableList(
                                FXCollections.observableList(
                                    listOf(FOF.IMPL, CNF.IMPL)
                                )
                            )
                        ).also { comboBox ->
                            comboBox.converter = object : StringConverter<FormulaForm>() {
                                override fun toString(`object`: FormulaForm?): String =
                                    `object`?.abbreviation ?: ""

                                override fun fromString(string: String?): FormulaForm {
                                    TODO("Not yet implemented")
                                }
                            }
                            comboBox.value = comboBox.items.first()
                        }
                        return builder.addLabelAndControl(
                            Label("form"),
                            control
                        ) as ComboBox<FormulaForm>
                    }

                    nameField = addLabeledTextField(builder, "name")
                    nameProperty = nameField.textProperty()

                    formatSelection = addFormatSelection(builder)
                    formatProperty = formatSelection.valueProperty()
                    domainSelection = addDomainSelection(builder)
                    domainsProperty = domainSelection.checkModel.checkedItems

                    formSelection = addFormSelection(builder)
                    formProperty = formSelection.valueProperty()

                    qLimitProperty = addLabeledTextField(builder, "qLimit", "3").textProperty()
                    stepLimitProperty = addLabeledTextField(builder, "step limit").textProperty()
                    timeLimitProperty = addLabeledTextField(builder, "time limit millis").textProperty()

                }.form
            )
            // Platform.runLater {
            //     configureValidation(
            //         nameField,
            //         formSelection,
            //         domainSelection,
            //         formatSelection,
            //         validationMessages,
            //         createButtonType
            //     )
            // }
        }
        resultConverter = Callback { buttonType: ButtonType ->
            when (buttonType.buttonData) {
                ButtonBar.ButtonData.OK_DONE -> {
                    TptpProblemSetBuilder(
                        nameProperty.get(),
                        domainsProperty.toList(),
                        // TODO abstract this appropriately
                        when (formProperty.value) {
                            is CNF -> TptpCnf
                            is FOF -> TptpFof
                            else   -> {
                                throw IllegalArgumentException()
                            }
                        },
                        FolTableauHarness(
                            qLimitProperty.value.takeIf { it.trim().isNotEmpty() }?.toLong() ?: -1L,
                            stepLimitProperty.value.takeIf { it.trim().isNotEmpty() }?.toLong() ?: -1L,
                            timeLimitProperty.value.takeIf { it.trim().isNotEmpty() }?.toLong() ?: -1L
                        )
                    )
                }
                else                         -> null
            }
        }
    }

    private fun configureValidation(
        nameField: TextField,
        formSelection: ComboBox<FormulaForm>,
        domainsSelection: CheckComboBox<TptpDomain>,
        formatSelection: ComboBox<String>,
        messageList: TextArea,
        createButtonType: ButtonType
    ) {
        val validationSupport = ValidationSupport()
        validationSupport.registerValidator<TextField>(
            nameField,
            Validator.createEmptyValidator("Name is required")
        )
        validationSupport.registerValidator(nameField) { c: Control, newValue: String ->
            ValidationResult.fromErrorIf(c, "Disallowed character found in name", !newValue.isValidFileName())
        }

        validationSupport.registerValidator<ComboBox<String>>(
            formSelection,
            Validator.createEmptyValidator("Form selection required")
        )
        validationSupport.registerValidator<ComboBox<String>>(
            formatSelection,
            Validator.createEmptyValidator("Format selection required")
        )
        validationSupport.registerValidator<CheckComboBox<String>>(
            domainsSelection,
            Validator.createEmptyValidator("Domains selection required")
        )
        // validationSupport
        //     .validationResultProperty()
        //     .addListener { _, oldValue, newValue ->
        //         messageList.text = buildString {
        //             dialogPane.lookupButton(createButtonType)
        //                 .disableProperty()
        //                 .set(newValue.messages.isNotEmpty())
        //
        //             newValue.messages.forEach {
        //                 append(it.text)
        //                 append('\n')
        //             }
        //         }
        //     }
    }

    private fun addLabeledTextField(
        builder: FormBuilder,
        label: String,
        initial: String = "",
        config: (TextField) -> Unit = {}
    ): TextField =
        builder.addLabelAndControl(
            Label(label),
            TextField(initial).also(config)
        ) as TextField

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

