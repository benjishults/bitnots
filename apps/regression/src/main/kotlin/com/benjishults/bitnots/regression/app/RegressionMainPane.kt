package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.util.toConjunct
import com.benjishults.bitnots.parser.Parser
import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.prover.TimedHarness
import com.benjishults.bitnots.regression.app.problem.NotRunStatus
import com.benjishults.bitnots.regression.app.problem.ProblemRunDescriptor
import com.benjishults.bitnots.regression.app.problem.ProblemRunStatus
import com.benjishults.bitnots.regression.app.problem.ProblemSet
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.tableauProver.PropositionalTableauHarness
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
import java.time.Instant
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

    private fun versionLabel() = "${gitCommitTime()}__${gitCommitId()}"

    private fun gitCommitId() =
        gitCommitInfo()["git.commit.id"]?.let { it as String } ?: "dirty-SNAPSHOT"

    private fun gitCommitTime(): Instant? =
        gitCommitInfo()["git.commit.time"]?.let { timeString ->
            Instant.parse(timeString as String)
        } ?: Instant.now()

    private fun gitCommitInfo() =
        javaClass.classLoader.getResource("git.properties")?.let { url ->
            url.file
                .reader()
                .buffered()
                .use { reader ->
                    Properties().also {
                        it.load(reader)
                    }
                }
        } ?: Properties()

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

    fun FormulaForm.toHarness(): TimedHarness<*> =
        when (this) {
            TptpFormulaForm.FOF -> FolTableauHarness(version = versionLabel())
            TptpFormulaForm.CNF -> PropositionalTableauHarness(version = versionLabel())
            else                -> error("unsupported formula form")
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
                problemSet.problems.forEachIndexed { index, problemDescriptor ->
                    val harness = problemDescriptor.form.toHarness()
                    val descriptor = TptpProblemFileDescriptor((problemDescriptor as TptpProblemFileDescriptor).domain)
                    TptpFormulaClassifier().classify(
                        // TODO simplify these APIs
                        problemDescriptor.form.toParser()
                            // when (problemDescriptor.form) {
                            //     TptpFormulaForm.FOF -> TptpFofParser
                            //     TptpFormulaForm.CNF -> TptpCnfParser
                            //     else                -> error("unsupported formula form")
                            // }
                            .parseFile(TptpFileFetcher.findProblemFile(descriptor))
                    ).let { (hyps, targets) ->
                        // clearInternTables()
                        val hypothesis = hyps.toConjunct()
                        targets.forEach { target ->
                            runBlocking {
                                harness.limitedTimeProve(
                                    hypothesis?.let {
                                        Implies(it, target)
                                    } ?: target
                                )
                            }.indicator.isDone()
                            // TODO create new runs and update table
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
        val harnessCol = TableColumn<ProblemRunDescriptor, Harness<*>>("Harness")
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
                        // Popup().also { popup ->
                        //     popup.content.add(Text("Initializing Problem Set '${builder.name}'"))
                        //     popup.show(this.window)
                        problemSet = builder.build()
                        table.items = FXCollections.observableList(problemSet.problems.map { descriptor ->
                            if (descriptor is TptpProblemFileDescriptor) {
                                ProblemRunDescriptor(
                                    descriptor,
                                    FolTableauHarness(3, version = versionLabel()),
                                    NotRunStatus
                                )
                            } else error("not TPTP")
                        }.toList())
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
