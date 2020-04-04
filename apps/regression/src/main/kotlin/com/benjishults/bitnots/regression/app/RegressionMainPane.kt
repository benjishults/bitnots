package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.util.toConjunct
import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.regression.app.problem.ProblemRow
import com.benjishults.bitnots.regression.app.problem.ProblemSet
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.theory.DomainCategory
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
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
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

    private val table: TableView<ProblemRow>

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
                Button("Run").also { runButton ->
                    runButton.setOnAction {
                        val prover = FolTableauHarness().toProver()
                        problemSet.problems.forEachIndexed { index, problemDescriptor ->
                            val descriptor = TptpProblemFileDescriptor(problemDescriptor)
                            TptpFormulaClassifier().classify(
                                when (problemDescriptor.form) {
                                    TptpFormulaForm.FOF -> TptpFofParser
                                    TptpFormulaForm.CNF -> TptpCnfParser
                                    else                -> error("unsupported formula form")
                                }.parseFile(TptpFileFetcher.findProblemFile(descriptor))
                            ).let { (hyps, targets) ->
                                // clearInternTables()
                                val hypothesis = hyps.toConjunct()
                                targets.forEach { target ->
                                    prover.limitedTimeProve(
                                        FolTableau(
                                        hypothesis?.let {
                                            Implies(it, target)
                                        } ?: target),
                                        -1L
                                        // problemDescriptor.timeLimit
                                    ).indicator.isDone()
                                }
                            }
                        }
                    }
                },
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

    private fun problemTable(): TableView<ProblemRow> {
        val table = TableView<ProblemRow>()
        val fileNameCol = TableColumn<ProblemRow, String>("File")
        fileNameCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.fileName)
        }
        val sourceCol = TableColumn<ProblemRow, ProblemSource>("Source")
        sourceCol.setCellValueFactory { _ ->
            ReadOnlyObjectWrapper(TptpFileRepo)
        }
        val domainCol = TableColumn<ProblemRow, DomainCategory>("Domain")
        domainCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.descriptor.domain)
        }
        val formCol = TableColumn<ProblemRow, FormulaForm>("Form")
        formCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.descriptor.form)
        }
        val qLimitCol = TableColumn<ProblemRow, Long?>("Q-Limit")
        qLimitCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.harness.qLimit)
        }
        val stepLimitCol = TableColumn<ProblemRow, Long?>("Step Limit")
        stepLimitCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.harness.stepLimit)
        }
        val timeLimitCol = TableColumn<ProblemRow, BigDecimal?>("Time Limit (s)")
        timeLimitCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(
                column.value.harness.timeLimitMillis?.div(1000.0)
                    ?.let { num ->
                        BigDecimal(num, MathContext(2, RoundingMode.HALF_UP))
                    })
        }
        val lastTimeCol = TableColumn<ProblemRow, Long?>("Last Time (ms)")
        lastTimeCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(null)
        }
        table.columns.addAll(
            fileNameCol,
            sourceCol,
            domainCol,
            formCol,
            qLimitCol,
            stepLimitCol,
            timeLimitCol,
            lastTimeCol
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
                        problemSetName.set(builder.name)
                        // Popup().also { popup ->
                        //     popup.content.add(Text("Initializing Problem Set '${builder.name}'"))
                        //     popup.show(this.window)
                        table.items = FXCollections.observableList(builder.build().problems.map { descriptor ->
                            if (descriptor is TptpProblemFileDescriptor) {
                                ProblemRow(
                                    descriptor.toFileName(),
                                    descriptor,
                                    FolTableauHarness(3),
                                    -1L
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
