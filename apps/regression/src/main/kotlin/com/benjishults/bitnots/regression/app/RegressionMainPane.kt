package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.parser.FileFetcher
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.problem.ProblemRunDescriptor
import com.benjishults.bitnots.prover.problem.ProblemRunStatus
import com.benjishults.bitnots.prover.problem.ProblemSet
import com.benjishults.bitnots.prover.problem.toProblemRunStatus
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.tptp.TptpFileRepo
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import com.benjishults.bitnots.tptp.formula.TptpFormulaClassifier
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class RegressionMainPane(
    private val uiProperties: Properties,
    width: Double,
    height: Double,
    menuBar: MenuBar = MenuBar(),
    pane: BorderPane = BorderPane()
) : Scene(VBox(menuBar, pane), width, height), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx

    private val problemSetName = SimpleStringProperty("No problem set selected")

    @Volatile
    private var problemSet: ProblemSet<*> = ProblemSet.EMPTY
        set(value) {
            problemSetName.set(value.name)
            field = value
        }

    private val table: TableView<ProblemRunDescriptor<*>>

    init {
        stylesheets.add("css/ui.css")
        pane.idProperty().set("root")

        menu(menuBar)

        top(pane)

        table = problemTable()
        pane.center = ScrollPane(table)

        bottom(pane)
    }

    private fun top(pane: BorderPane) {
        pane.top = VBox().also { vbox ->
            vbox.children.addAll(
                FlowPane().also { bottomPane ->
                    bottomPane.children.addAll(
                        Button("Save").also { saveButton ->
                            saveButton.setOnAction { _ ->
                                TODO("Implement")
                            }
                        },
                        Button("Delete Problem Set").also { deleteButton ->
                            deleteButton.setOnAction { _ ->
                                TODO("Implement")
                            }
                        }
                    )
                },
                Label().also { label ->
                    label.textProperty().bind(problemSetName)
                }
            )
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
                })
        }
    }

    private fun runButton() =
        Button("Run").also { runButton ->
            runButton.setOnAction {
                // TODO extract this to be testable
                // val toParser: (FormulaForm) -> Parser<*, *> = this::toParser


                val fileFetcher = TptpFileFetcher
                problemSet.problems.forEach { problemRun ->
                    // val onProof: (ProofInProgress) -> Unit = { pip -> updateTableItem(problemRun, pip) }
                    val (fileDescriptor, harness) = problemRun
                    fileDescriptor.parser<FolAnnotatedFormula>().parseAndClassify(
                        TptpFormulaClassifier(),
                        fileDescriptor as TptpProblemFileDescriptor,
                        fileFetcher as FileFetcher<*, TptpFormulaForm, FileDescriptor<TptpFormulaForm, *>>
                    ).let { (hyps, targets) ->
                        this.launch {
                            // TODO extract data for table
                            harness.proveAllTargets(hyps, targets)
                        }
                    }
                }
            }
        }

    private fun updateTableItem(
        problemRun: ProblemRunDescriptor<*>,
        proofInProgress: ProofInProgress
    ) {
        table.items.set(
            table.items.indexOf(problemRun),
            ProblemRunDescriptor(
                problemRun.fileDescriptor,
                problemRun.harness,
                proofInProgress.indicator.toProblemRunStatus()
            )
        )
    }

    private fun problemTable(): TableView<ProblemRunDescriptor<*>> {
        val table = TableView<ProblemRunDescriptor<*>>()
        val fileNameCol = TableColumn<ProblemRunDescriptor<*>, String>("File")
        fileNameCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.fileDescriptor.toFileName())
        }
        val sourceCol = TableColumn<ProblemRunDescriptor<*>, String>("Source")
        sourceCol.setCellValueFactory { _ ->
            ReadOnlyObjectWrapper(TptpFileRepo.abbreviation)
        }
        val harnessCol = TableColumn<ProblemRunDescriptor<*>, Harness<*, *>>("Harness")
        harnessCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.harness)
        }
        val statusCol = TableColumn<ProblemRunDescriptor<*>, ProblemRunStatus>("Last Run")
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
                NewProblemSetDialog()
                    .showAndWait()
                    .ifPresentOrElse(
                        { builder ->
                            launch(Dispatchers.JavaFx.immediate) {
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
                        }
                    ) {
                        // do nothing
                    }
            }
        }
    }

}
