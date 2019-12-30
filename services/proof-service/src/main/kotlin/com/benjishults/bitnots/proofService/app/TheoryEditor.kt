package com.benjishults.bitnots.proofService.app

import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRoles
import com.benjishults.bitnots.theory.problem.Problem
import com.benjishults.bitnots.tptp.parser.TptpFofParser
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.FileChooser
import org.apache.camel.CamelContext
import java.io.File
import java.nio.file.Path
import java.util.*

class TheoryEditor(
        val context: CamelContext,
        val uiProperties: Properties,
        width: Double,
        height: Double
) : Scene(BorderPane(), width, height) {

    private val inputFormats: List<String> = context.propertiesComponent.resolveProperty("input.formats")
            .orElse("tptp,ipr,bitnots")
            .split(",")

    private val fileName = SimpleStringProperty("no file loaded")
    private val inputFormat = SimpleStringProperty(inputFormats.first())
    private val textArea = TextArea()

    init {
        stylesheets.add("css/ui.css")
        with(root as BorderPane) {
            idProperty().set("root")
            top = Label().apply {
                textProperty().bind(fileName)
            }
            center = ScrollPane(textArea)
            bottom = FlowPane().apply {
                with(children) {
                    add(Label("format", ComboBox(FXCollections.unmodifiableObservableList(FXCollections.observableList(inputFormats))).apply {
                        inputFormat.bind(valueProperty())
                        value = uiProperties.getProperty("ui.chosenInputFormat", inputFormats.first())
                        setOnAction {
                            uiProperties.put("ui.chosenInputFormat", value)
                        }
                    }))
                    add(Button("Load Problem").apply {
                        setOnAction { _ ->
                            loadFile("problem")?.let {
                                // TODO set as current problem
                                loadProblem(it)
                            }
                        }
                    })
                    add(Button("Load Theory").apply {
                        setOnAction { _ ->
                            loadFile("theory")?.let {
                                // TODO add to theories loaded
                            }
                        }
                    })
                    add(Button("Launch Prover").apply {
                        setOnAction { _ ->
                            // TODO load theory files into KB
                            // TODO start prover on selected problem
                        }
                    })
                    add(Button("Save"))
                }
            }
        }
    }

    private fun loadProblem(path: Path) {
        val axioms = mutableListOf<FolAnnotatedFormula>()
        val conjectures = mutableListOf<FolAnnotatedFormula>()
        // TODO create a single theory and a problem for each conjecture
        TptpFofParser.parseFile(path).forEach { annotatedFormula ->
            when (annotatedFormula.formulaRole) {
                FormulaRoles.conjecture -> {
                    conjectures.add(annotatedFormula)
                }
                FormulaRoles.assumption, FormulaRoles.axiom, FormulaRoles.definition, FormulaRoles.hypothesis -> {
                    axioms.add(annotatedFormula)
                }
                else -> {
                    // ignore for now
                }
            }
        }
        println(conjectures.map {
            Problem(emptyList(),
                    FolAnnotatedFormula(
                            "problem",
                            FormulaRoles.conjecture,
                            axioms.map {
                                it.formula
                            }.fold(it.formula) { target, axiom ->
                                Implies(axiom, target)
                            }))
        })
    }

    private fun loadTheory() {

    }

    private fun loadFile(category: String): Path? {
        // TODO also save
        val folderKey = "ui.${inputFormat.get()}${category.capitalize()}InputFolder"
        return FileChooser().run {
            initialDirectory = File(uiProperties.getOrDefault(folderKey, System.getProperty("user.home")) as String)
            title = "Select ${category} file"
            showOpenDialog(this@TheoryEditor.window)
        }?.run {
            uiProperties.put(folderKey, toPath().parent.toString())
            fileName.value = absolutePath
            textArea.text = inputStream().use {
                String(it.readAllBytes())
            }
            toPath()
        }
    }

}
