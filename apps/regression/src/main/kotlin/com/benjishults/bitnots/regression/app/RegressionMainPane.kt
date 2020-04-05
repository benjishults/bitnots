package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.util.toConjunct
import com.benjishults.bitnots.parser.Parser
import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.prover.problem.ProblemRunDescriptor
import com.benjishults.bitnots.prover.problem.ProblemRunStatus
import com.benjishults.bitnots.prover.problem.ProblemSet
import com.benjishults.bitnots.prover.problem.toProblemRunStatus
import com.benjishults.bitnots.theory.formula.FormulaForm
import com.benjishults.bitnots.tptp.TptpFileRepo
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import com.benjishults.bitnots.tptp.formula.TptpFormulaClassifier
import com.benjishults.bitnots.tptp.parser.TptpCnfParser
import com.benjishults.bitnots.tptp.parser.TptpFofParser
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import kotlinx.coroutines.runBlocking
import java.util.*

class RegressionMainPane(
    private val uiProperties: Properties,
    width: Double,
    height: Double,
    menuBar: MenuBar = MenuBar(),
    pane: BorderPane = BorderPane()
) : Scene(VBox(menuBar, pane), width, height) {

    private val problemSetName = SimpleStringProperty("No problem set selected")

    @Volatile
    private var problemSet: ProblemSet = ProblemSet.EMPTY
        set(value) {
            problemSetName.set(value.name)
            field = value
        }

    private val table: TableView<ProblemRunDescriptor>

    init {
        stylesheets.add("css/ui.css")
        pane.idProperty().set("root")

        menu(menuBar)
        top(pane)
        table = problemTable()
        pane.center = ScrollPane(table)
        bottom(pane)
    }

    private fun bottom(pane: BorderPane) {
        pane.bottom = FlowPane().also { bottomPane ->
            bottomPane.children.addAll(
                runButton(),
                Button("See History").apply
                {
                    setOnAction { _ ->
                        TODO("Implement")
                        // loadFile("problem")?.let {
                        //     // TODO set as current problem
                        //     loadProblem(it)
                        // }
                    }
                },
                Button("Unlock").apply
                {
                    setOnAction { _ ->
                        TODO("Implement")
                        // loadFile("theory")?.let {
                        //     // TODO add to theories loaded
                        // }
                    }
                },
                Button("Delete Problem Set").apply
                {
                    setOnAction { _ ->
                        TODO("Implement")
                    }
                })
        }
    }

    fun FormulaForm.toParser(): Parser<*, *, *> =
        when (this) {
            TptpFormulaForm.FOF -> TptpFofParser
            TptpFormulaForm.CNF -> TptpCnfParser
            else                -> error("unsupported formula form")
        }

    private fun runButton() =
        Button("Run").also { runButton ->
            runButton.setOnAction {
                // TODO extract this to be testable
                // TODO do this on another thread
                problemSet.problems.forEach { fileDescriptor ->

                }
                problemSet.problems.forEach { problemRun ->
                    val (fileDescriptor, harness) = problemRun
                    fileDescriptor as TptpProblemFileDescriptor
                    TptpFormulaClassifier().classify(
                        // TODO simplify these APIs
                        fileDescriptor.form.toParser().parseFile(TptpFileFetcher.findProblemFile(fileDescriptor))
                    ).let { (hyps, targets) ->
                        // clearInternTables()
                        val hypothesis = hyps.toConjunct()
                        targets.forEach { target ->
                            table.items.set(
                                table.items.indexOf(problemRun),
                                ProblemRunDescriptor(
                                    fileDescriptor,
                                    harness,
                                    runBlocking {
                                        harness.limitedTimeProve(
                                            hypothesis?.let {
                                                Implies(
                                                    it,
                                                    target
                                                )
                                            } ?: target
                                        )
                                    }.indicator.toProblemRunStatus()
                                )
                            )
                        }
                    }
                }
            }
        }

    private fun top(pane: BorderPane) {
        pane.top = Label().apply {
            textProperty().bind(problemSetName)
        }
    }

    private fun menu(menuBar: MenuBar) {
        val fileMenu = Menu("File")
        fileMenu.items.addAll(
            newProblemSetMenuItem(),
            openProblemSetMenuItem(),
            recentProblemSetsMenu()
        )

        menuBar.menus.addAll(fileMenu, Menu("Help"))
    }

    private fun problemTable(): TableView<ProblemRunDescriptor> {
        val table = TableView<ProblemRunDescriptor>()
        val fileNameCol = TableColumn<ProblemRunDescriptor, String>("File")
        fileNameCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.fileDescriptor.toFileName())
        }
        val sourceCol = TableColumn<ProblemRunDescriptor, ProblemSource>("Source")
        sourceCol.setCellValueFactory { _ ->
            ReadOnlyObjectWrapper(TptpFileRepo)
        }
        val harnessCol = TableColumn<ProblemRunDescriptor, Harness<*, *>>("Harness")
        harnessCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.harness)
        }
        val statusCol = TableColumn<ProblemRunDescriptor, ProblemRunStatus>("Last Run")
        statusCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(if (column.value is ProblemRunDescriptor) column.value.status else null)
        }
        table.columns.addAll(
            fileNameCol,
            sourceCol,
            harnessCol,
            statusCol
        )
        return table
    }

    private fun recentProblemSetsMenu() = Menu("Recent")

    private fun openProblemSetMenuItem() = MenuItem("Open...")

    private fun newProblemSetMenuItem(): MenuItem {
        return MenuItem("New").also { new ->
            new.setOnAction { e: ActionEvent ->
                NewProblemSetDialog().showAndWait().ifPresentOrElse(
                    { builder ->
                        // TODO do this in other thread
                        //      show spinny gif
                        //      load data on UI thread
                        // Popup().also { popup ->
                        //     popup.content.add(Text("Initializing Problem Set '${builder.name}'"))
                        //     popup.show(this.window)
                        problemSet = builder.build()
                        table.items = FXCollections.observableList(
                            problemSet.problems
                        )
                        // popup.hide()
                        // }
                    }
                ) {
                    // do nothing
                }
            }

        }
    }

    // private fun loadProblem(path: Path) {
    //     val axioms = mutableListOf<FolAnnotatedFormula>()
    //     val conjectures = mutableListOf<FolAnnotatedFormula>()
    //     // TODO create a single theory and a problem for each conjecture
    //     TptpFofParser.parseFile(path).forEach { annotatedFormula ->
    //         when (annotatedFormula.formulaRole) {
    //             FormulaRole.conjecture -> {
    //                 conjectures.add(annotatedFormula)
    //             }
    //             FormulaRole.assumption,
    //             FormulaRole.axiom,
    //             FormulaRole.definition,
    //             FormulaRole.hypothesis -> {
    //                 axioms.add(annotatedFormula)
    //             }
    //             else                   -> {
    //                 // ignore for now
    //             }
    //         }
    //     }
    //     // proofContext = Problem(axioms, conjectures)
    // }

    // private fun loadTheory() {
    //
    // }

    // private fun loadFile(category: String): Path? {
    //     // TODO also save
    //     val folderKey = "ui.${inputFormat.get()}${category.capitalize()}InputFolder"
    //     return FileChooser().run {
    //         initialDirectory = File(uiProperties.getOrDefault(folderKey, System.getProperty("user.home")) as String)
    //         title = "Select ${category} file"
    //         showOpenDialog(this@RegressionMainPane.window)
    //     }?.run {
    //         uiProperties.put(folderKey, toPath().parent.toString())
    //         problemSetName.value = absolutePath
    //         textArea.text = inputStream().use {
    //             String(it.readBytes())
    //         }
    //         toPath()
    //     }
    // }

}
